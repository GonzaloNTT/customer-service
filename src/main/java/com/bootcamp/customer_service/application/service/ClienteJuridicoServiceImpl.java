package com.bootcamp.customer_service.application.service;

import com.bootcamp.customer_service.application.mapper.command.ClienteJuridicoCommand;
import com.bootcamp.customer_service.application.port.out.ClienteJuridicolRepositoryPort;
import com.bootcamp.customer_service.application.port.out.ClienteNaturalRepositoryPort;
import com.bootcamp.customer_service.application.port.out.event.CustomerEventPublisherPort;
import com.bootcamp.customer_service.domain.aggregate.ClienteJuridico;
import com.bootcamp.customer_service.domain.enums.TipoClienteJuridico;
import com.bootcamp.customer_service.domain.service.ClienteJuridicoService;
import com.bootcamp.customer_service.domain.service.ClienteNaturalService;
import com.bootcamp.customer_service.events.CustomerCreatedEvent;
import com.bootcamp.customer_service.infrastructure.cache.CustomerReactiveCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class ClienteJuridicoServiceImpl implements ClienteJuridicoService {
    private final ClienteJuridicolRepositoryPort clienteJuridicolRepositoryPort;
    private final CustomerReactiveCache cache;
    private final CustomerEventPublisherPort eventPublisher;
    private final ClienteNaturalService clienteNaturalService;

    public ClienteJuridicoServiceImpl(ClienteJuridicolRepositoryPort clienteJuridicolRepositoryPort,
                                          CustomerReactiveCache cache,
                                          CustomerEventPublisherPort eventPublisher,
                                          ClienteNaturalService clienteNaturalService  ) {
        this.clienteJuridicolRepositoryPort = clienteJuridicolRepositoryPort;
        this.cache = cache;
        this.eventPublisher = eventPublisher;
        this.clienteNaturalService = clienteNaturalService;
    }

    @Override
    public Mono<String> registrarClienteJuridico(ClienteJuridicoCommand command) {
        log.debug("Registrando cliente jurídico: {}", command.nombreComercial());

        // 1️⃣ Validar si ya existe un cliente jurídico con ese nombre comercial
        Mono<Void> validarExistencia = obtenerClienteJuridicoPorNombreComercial(command.nombreComercial())
                .flatMap(existing -> Mono.<Void>error(new IllegalArgumentException(
                        "Ya existe un cliente jurídico con nombre comercial: " + command.nombreComercial())))
                .switchIfEmpty(Mono.empty())
                // Mover la operación a un Scheduler separado para no bloquear Netty
                .subscribeOn(Schedulers.boundedElastic());

        // 2️⃣ Validar que los representantes existan
        Mono<Void> validarRepresentantes = Flux.fromIterable(command.representantes())
                .flatMap(representanteId ->
                        clienteNaturalService.obtenerClienteNaturalPorId(representanteId)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                        "Representante no encontrado con ID: " + representanteId)))
                                .subscribeOn(Schedulers.boundedElastic()) // Scheduler por cada llamada a BD
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "La lista de representantes no es válida")))
                .then();

        // 3️⃣ Crear la entidad cliente jurídico
        ClienteJuridico clienteJuridico = new ClienteJuridico(
                command.representantes(),
                TipoClienteJuridico.valueOf(command.tipo()),
                command.nombreComercial()
        );

        // 4️⃣ Guardar en el repositorio y devolver el ID
        return Mono.when(validarExistencia, validarRepresentantes)
                .then(clienteJuridicolRepositoryPort.save(clienteJuridico)
                        .subscribeOn(Schedulers.boundedElastic()) // Scheduler para la operación de guardado
                )
                .map(ClienteJuridico::getId)
                .doOnSuccess(savedId -> log.info("Cliente jurídico registrado exitosamente: {}", savedId))
                .doOnError(ex -> log.error("Error registrando cliente jurídico {}", command.nombreComercial(), ex));

    }

    @Override
    public Mono<ClienteJuridico> obtenerClienteJuridicoPorId(String id) {
        log.info("Obteniendo cliente jurídico por ID: {}", id);
        String cacheKey = "customer:juridico:" + id;
        return cache.get(cacheKey)
                .cast(ClienteJuridico.class)
                .switchIfEmpty(
                        clienteJuridicolRepositoryPort.findById(id)
                                .flatMap(customer -> {
                                    if (customer instanceof ClienteJuridico nc) {
                                        return cache.put(cacheKey, nc, Duration.ofMinutes(10))
                                                .thenReturn(nc);
                                    }
                                    return Mono.empty();
                                })
                );
    }

    @Override
    public Flux<ClienteJuridico> obtenerTodosClientesJuridicos() {
        log.info("Obteniendo todos los clientes jurídicos");
        return clienteJuridicolRepositoryPort.findAll()
                .filter(c -> c instanceof ClienteJuridico)
                .cast(ClienteJuridico.class);
    }

    @Override
    public Mono<Void> actualizarClienteJuridico(String id, ClienteJuridicoCommand command) {
        log.debug("Actualizando cliente jurídico con ID: {}", id);

        Mono<ClienteJuridico> clienteExistenteMono = clienteJuridicolRepositoryPort.findById(id)
                .switchIfEmpty(
                        Mono.error(new IllegalArgumentException("Cliente jurídico no encontrado con ID: " + id)));

        Mono<Void> validarNombreDuplicado = obtenerClienteJuridicoPorNombreComercial(command.nombreComercial())
                .filter(existing -> !existing.getId().equals(id))
                .flatMap(existing -> Mono.<Void>error(new IllegalArgumentException(
                        "Ya existe otro cliente jurídico con el nombre comercial: " + command.nombreComercial())))
                .switchIfEmpty(Mono.empty());

        Mono<Void> validarRepresentantes = Flux.fromIterable(command.representantes())
                .flatMap(this::obtenerClienteJuridicoPorId) // verifica cada representante
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Debe haber al menos un representante válido")))
                .then();

        return Mono.when(validarNombreDuplicado, validarRepresentantes)
                .then(clienteExistenteMono)
                .flatMap(clienteExistente -> {
                    ClienteJuridico nuevoCliente = new ClienteJuridico(
                            clienteExistente.getId(),
                            command.representantes(),
                            TipoClienteJuridico.valueOf(command.tipo()),
                            command.nombreComercial()
                    );

                    // Guardar cambios
                    return clienteJuridicolRepositoryPort.save(clienteExistente);
                })
                .doOnSuccess(
                        saved -> log.info("Cliente jurídico actualizado exitosamente: {}", saved.getId()))
                .doOnError(
                        ex -> log.error("Error al actualizar cliente jurídico con ID {}: {}", id, ex.getMessage(), ex))
                .then(); // retorna Mono<Void>
    }

    @Override
    public Mono<Void> elminarClienteJuridico(String id) {
        log.info("Eliminando cliente jurídico por ID: {}", id);
        String cacheKey = "customer:juridico:" + id;

        return clienteJuridicolRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente jurídico no encontrado: " + id)))
                .flatMap(cliente ->
                        clienteJuridicolRepositoryPort.delete(cliente)
                                .then(cache.evict(cacheKey))
                )
                .doOnSuccess(v -> log.info("Cliente jurídico eliminado correctamente: {}", id))
                .doOnError(
                        ex -> log.error("Error al eliminar cliente jurídico {}: {}", id, ex.getMessage(), ex)).then();
    }


    public Mono<ClienteJuridico> obtenerClienteJuridicoPorNombreComercial(String nombreComercial) {
        return clienteJuridicolRepositoryPort.findByNombreComercial(nombreComercial);
    }
}
