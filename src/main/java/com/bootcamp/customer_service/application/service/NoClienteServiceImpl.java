package com.bootcamp.customer_service.application.service;

import com.bootcamp.customer_service.application.mapper.command.NoClienteCommand;
import com.bootcamp.customer_service.application.port.out.event.CustomerEventPublisherPort;
import com.bootcamp.customer_service.application.port.out.NoClienteRepositoryPort;
import com.bootcamp.customer_service.domain.aggregate.NoCliente;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;
import com.bootcamp.customer_service.domain.service.NoClienteService;
import com.bootcamp.customer_service.events.CustomerCreatedEvent;
import com.bootcamp.customer_service.infrastructure.cache.CustomerReactiveCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class NoClienteServiceImpl implements NoClienteService {
    private final NoClienteRepositoryPort noClienteRepositoryPort;
    private final CustomerReactiveCache cache;
    private final CustomerEventPublisherPort eventPublisher;

    public NoClienteServiceImpl(NoClienteRepositoryPort noClienteRepositoryPort,
                                     CustomerReactiveCache cache,
                                     CustomerEventPublisherPort eventPublisher) {
        this.noClienteRepositoryPort = noClienteRepositoryPort;
        this.cache = cache;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Mono<String> registrarNoCliente(NoClienteCommand noCustomerCommand) {
        log.debug("Registrando no cliente : {}", noCustomerCommand.id());

        Mono<Void> validarExistencia = obtenerNoClientePorId(noCustomerCommand.id())
                .flatMap(existing -> Mono.<Void>error(new IllegalArgumentException(
                        "Ya existe un cliente  con nombre comercial: " + noCustomerCommand.id())))
                .switchIfEmpty(Mono.empty());

        Mono<Void> validarDocumento = obtenerNoClientePorDocumento(noCustomerCommand.usuario().documento().type(),
                noCustomerCommand.usuario().documento().numero())
                .flatMap(existing -> Mono.<Void>error(new IllegalArgumentException(
                        "Ya existe un cliente  con documento: " +noCustomerCommand.usuario())))
                .switchIfEmpty(Mono.empty());

        NoCliente noCliente = new NoCliente(
                noCustomerCommand.usuario()
        );

        return Mono.when(validarExistencia, validarDocumento)
                .then(noClienteRepositoryPort.save(noCliente))
                .map(saved -> {
                    CustomerCreatedEvent event = new CustomerCreatedEvent(
                            saved.getId(),
                            saved.getUsuario().documento().numero(),
                            saved.getUsuario().correo().value()
                    );
                    eventPublisher.publishCustomerCreated(event);
                    return saved.getId();
                })
                .doOnSuccess(savedId -> log.info("Cliente  registrado exitosamente: {}", savedId))
                .doOnError(ex -> log.error("Error registrando cliente  {}", noCustomerCommand.id(), ex));
    }

    @Override
    public Mono<NoCliente> obtenerNoClientePorId(String id) {
        log.info("Obteniendo cliente  por ID: {}", id);
        String cacheKey = "customer::" + id;
        return cache.get(cacheKey)
                .cast(NoCliente.class)
                .switchIfEmpty(
                        noClienteRepositoryPort.findById(id)
                                .flatMap(customer -> {
                                    if (customer instanceof NoCliente nc) {
                                        return cache.put(cacheKey, nc, Duration.ofMinutes(10))
                                                .thenReturn(nc);
                                    }
                                    return Mono.empty();
                                })
                );
    }

    @Override
    public Flux<NoCliente> obtenerTodosNoClientes() {
        log.info("Obteniendo todos los clientes s");
        return noClienteRepositoryPort.findAll()
                .filter(c -> c instanceof NoCliente)
                .cast(NoCliente.class);
    }

    @Override
    public Mono<NoCliente> actualizarNoCliente(String id, NoClienteCommand noCustomerCommand) {
        log.debug("Actualizando cliente  con ID: {}", id);

        Mono<NoCliente> clienteExistenteMono = noClienteRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente  no encontrado con ID: " + id)));

        return Mono.when(clienteExistenteMono)
                .then(clienteExistenteMono)
                .flatMap(clienteExistente -> {
                    NoCliente nuevoCliente = new NoCliente(
                            clienteExistente.getId(),
                            noCustomerCommand.usuario()
                    );

                    // Guardar cambios
                    return noClienteRepositoryPort.save(clienteExistente);
                })
                .doOnSuccess(saved -> log.info("Cliente  actualizado exitosamente: {}", saved.getId()))
                .doOnError(ex -> log.error("Error al actualizar cliente  con ID {}: {}", id, ex.getMessage(), ex));
    }

    @Override
    public Mono<Void> eliminarNoCliente(String id) {
        log.info("Eliminando cliente  por ID: {}", id);
        String cacheKey = "customer::" + id;

        return noClienteRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente  no encontrado: " + id)))
                .flatMap(cliente ->
                        noClienteRepositoryPort.delete(cliente)
                                .then(cache.evict(cacheKey))
                )
                .doOnSuccess(v -> log.info("Cliente  eliminado correctamente: {}", id))
                .doOnError(ex -> log.error("Error al eliminar cliente  {}: {}", id, ex.getMessage(), ex)).then();
    }

    private Mono<NoCliente> obtenerNoClientePorDocumento(TipoDocumento type, String numero) {
        log.info("Obteniendo NoCliente  por Docuemtno: {} {}", type, numero);
        return noClienteRepositoryPort.findByDocument(type, numero);
    }
}
