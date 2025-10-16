package com.bootcamp.customer_service.application.service;

import com.bootcamp.customer_service.application.mapper.command.ClienteNaturalCommand;
import com.bootcamp.customer_service.application.port.out.ClienteNaturalRepositoryPort;
import com.bootcamp.customer_service.application.port.out.event.ClienteNaturalOrquestador;
import com.bootcamp.customer_service.application.port.out.event.CustomerEventPublisherPort;
import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.domain.enums.TipoClienteNatural;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;
import com.bootcamp.customer_service.domain.service.ClienteNaturalService;

import com.bootcamp.customer_service.events.CustomerCreatedEvent;
import com.bootcamp.customer_service.infrastructure.cache.CustomerReactiveCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Slf4j
public class ClienteNaturalServiceImpl implements ClienteNaturalService {
    private final ClienteNaturalRepositoryPort clienteNaturalRepositoryPort;
    private final CustomerReactiveCache cache;
    private final CustomerEventPublisherPort eventPublisher;
    private final ClienteNaturalOrquestador clienteNaturalOrquestador;

    public ClienteNaturalServiceImpl(ClienteNaturalRepositoryPort clienteNaturalRepositoryPort,
                                      CustomerReactiveCache cache,
                                      CustomerEventPublisherPort eventPublisher,
                                     ClienteNaturalOrquestador clienteNaturalOrquestador) {
        this.clienteNaturalRepositoryPort = clienteNaturalRepositoryPort;
        this.cache = cache;
        this.eventPublisher = eventPublisher;
        this.clienteNaturalOrquestador = clienteNaturalOrquestador;
    }



    @Override
    public Mono<String> registrarClienteNatural(ClienteNaturalCommand clienteNaturalCommand) {
        log.debug("Registrando cliente natural: {}", clienteNaturalCommand);

        Mono<Void> validarDocumento = this.obtenerClienteNaturalPorDocumento(
                clienteNaturalCommand.usuario().documento().type().name(),
                        String.valueOf(clienteNaturalCommand.usuario().documento().numero()))
                .flatMap(existing -> Mono.<Void>error(new IllegalArgumentException(
                        "Ya existe un cliente natural con documento: " +clienteNaturalCommand.usuario())))
                .switchIfEmpty(Mono.empty());

        ClienteNatural ClienteNatural = new ClienteNatural(
                clienteNaturalCommand.usuario(),
                clienteNaturalCommand.tipo()
        );

        return Mono.when(validarDocumento)
                .then(clienteNaturalRepositoryPort.save(ClienteNatural))
                .map(com.bootcamp.customer_service.domain.aggregate.ClienteNatural::getId)
                .doOnSuccess(savedId -> log.info("Cliente natural registrado exitosamente: {}", savedId))
                .doOnError(ex -> log.error("Error registrando cliente natural {}", clienteNaturalCommand.id(), ex));
    }


    @Override
    public Mono<ClienteNatural> obtenerClienteNaturalPorId(String id) {
        log.info("Obteniendo cliente natural por ID: {}", id);
        String cacheKey = "customer:natural:" + id;
        return cache.get(cacheKey)
                .cast(ClienteNatural.class)
                .switchIfEmpty(
                        clienteNaturalRepositoryPort.findById(new String(id))
                                .flatMap(customer -> {
                                    if (customer instanceof ClienteNatural nc) {
                                        return cache.put(cacheKey, nc, Duration.ofMinutes(10))
                                                .thenReturn(nc);
                                    }
                                    return Mono.empty();
                                })
                );
    }


    @Override
    public Flux<ClienteNatural> obtenerClientesNaturales(Optional<TipoClienteNatural> type) {
        log.info("Obteniendo clientes naturales{}", type.map(t -> " con tipo " + t).orElse(""));

        Predicate<ClienteNatural> filtroPorTipo = c -> type.map(t -> c.getTipo() == t).orElse(true);

        return clienteNaturalRepositoryPort.findAll()
                .filter(ClienteNatural.class::isInstance)
                .cast(ClienteNatural.class)
                .filter(filtroPorTipo); // aplicamos
    }



    @Override
    public Mono<Void> actualizarClienteNatural(String id, ClienteNaturalCommand clienteNaturalCommand) {
        log.debug("Actualizando cliente natural con ID: {}", id);

        return clienteNaturalRepositoryPort.findById(new String(id))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente natural no encontrado con ID: " + id)))
                .flatMap(clienteExistente -> {
                    ClienteNatural actualizadoCliente = new ClienteNatural(
                            clienteExistente.getId(),
                            clienteNaturalCommand.usuario(),
                            clienteNaturalCommand.tipo()
                    );
                    return clienteNaturalRepositoryPort.save(actualizadoCliente).then();
                })
                .doOnSuccess(v -> log.info("Cliente natural actualizado exitosamente: {}", id))
                .doOnError(ex ->
                        log.error(
                                "Error al actualizar cliente natural con ID {}: {}", id, ex.getMessage(), ex));
    }

    @Override
    public Mono<Void> eliminarClienteNatural(String id) {
        log.info("Eliminando cliente natural por ID: {}", id);
        String cacheKey = "customer:natural:" + id;

        return clienteNaturalRepositoryPort.findById(new String(id))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente natural no encontrado: " + id)))
                .flatMap(cliente ->
                        clienteNaturalRepositoryPort.delete(cliente)
                                .then(cache.evict(cacheKey))
                )
                .doOnSuccess(v -> log.info("Cliente natural eliminado correctamente: {}", id))
                .doOnError(ex -> log.error("Error al eliminar cliente natural {}: {}", id, ex.getMessage(), ex)).then();
    }

    private Mono<ClienteNatural> obtenerClienteNaturalPorDocumento(String type, String numero) {
        log.info("Obteniendo cliente natural por Docuemtno: {} {}", type, numero);
        return clienteNaturalRepositoryPort.findByDocument(type, numero);
    }

}
