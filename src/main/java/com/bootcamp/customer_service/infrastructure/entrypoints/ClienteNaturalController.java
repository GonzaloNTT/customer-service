package com.bootcamp.customer_service.infrastructure.entrypoints;

import com.bootcamp.customer_service.application.mapper.CommandMapper;
import com.bootcamp.customer_service.application.mapper.command.ClienteNaturalCommand;
import com.bootcamp.customer_service.domain.service.ClienteJuridicoService;
import com.bootcamp.customer_service.domain.service.ClienteNaturalService;
import com.bootcamp.customer_service.server.ClienteApi;
import com.bootcamp.customer_service.server.models.ClienteJuridicoRequest;
import com.bootcamp.customer_service.server.models.ClienteNaturalRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@Slf4j
public class ClienteNaturalController implements ClienteApi {

    private final ClienteNaturalService clienteNaturalService;
    private final ClienteJuridicoService clienteJuridicoService;

    ClienteNaturalController(ClienteNaturalService clienteNaturalService,
                             ClienteJuridicoService clienteJuridicoService) {
        this.clienteNaturalService = clienteNaturalService;
        this.clienteJuridicoService = clienteJuridicoService;
    }

    @Override
    public Mono<ResponseEntity<Void>> clienteJuridicoIdDelete(String id, final ServerWebExchange exchange) {
        log.debug("delete Request={}", id);
        return clienteJuridicoService.elminarClienteJuridico(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Override
    public Mono<ResponseEntity<ClienteJuridicoRequest>> clienteJuridicoIdGet(
            String id,  final ServerWebExchange exchange) {
        log.debug("getById Request={}", id);
        return clienteJuridicoService.obtenerClienteJuridicoPorId(id)
                .map(CommandMapper::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(ex -> log.error("Error fetching customer by id {}", id, ex));
    }

    @Override
    public Mono<ResponseEntity<Void>> clienteJuridicoIdPut(
            String id,
            Mono<ClienteJuridicoRequest> clienteJuridicoRequest,
            final ServerWebExchange exchange) {

        log.debug("PUT /cliente/juridico/{}", id);

        return clienteJuridicoRequest
                .flatMap(request -> {
                    return clienteJuridicoService.actualizarClienteJuridico(id, CommandMapper.toCommand(request))
                            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
                })
                .onErrorResume(ex -> {
                    log.error("Error updating cliente juridico id {}", id, ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build()); // 👈 Y aquí
                });
    }

    @Override
    public Mono<ResponseEntity<Void>> clienteNaturalIdDelete(String id, final ServerWebExchange exchange) {
        log.debug("delete Request={}", id);
        return clienteNaturalService.eliminarClienteNatural(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Override
    public Mono<ResponseEntity<ClienteNaturalRequest>> clienteNaturalIdGet(
            String id,  final ServerWebExchange exchange) {
        log.debug("getById Request={}", id);
        return clienteNaturalService.obtenerClienteNaturalPorId(id)
                .map(CommandMapper::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(ex -> log.error("Error fetching customer by id {}", id, ex));
    }

    @Override
    public Mono<ResponseEntity<Void>> clienteNaturalIdPut(
            @PathVariable String id,
            @RequestBody Mono<ClienteNaturalRequest> clienteNaturalRequest,
            final ServerWebExchange exchange) {

        log.debug("PUT /cliente/natural/{}", id);

        return clienteNaturalRequest
                .flatMap(request ->
                        clienteNaturalService
                                .actualizarClienteNatural(id, CommandMapper.toCommand(request))
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                )
                .onErrorResume(ex -> {
                    log.error("Error updating cliente natural id {}", id, ex);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Void>build());
                });
    }


    @Override
    public Mono<ResponseEntity<Void>> crearClienteJuridico(
            Mono<ClienteJuridicoRequest> clienteJuridicoRequest,
            final ServerWebExchange exchange) {

        log.debug("POST /cliente/juridico");
        return clienteJuridicoRequest
                .flatMap(request -> {
                    var command = CommandMapper.toCommand(request);
                    return clienteJuridicoService.registrarClienteJuridico(command)
                            .map(id -> ResponseEntity
                                    .created(URI.create("/api/v1/cliente/juridico/" + id))
                                    .<Void>build()  // Body vacío
                            );
                })
                .onErrorResume(ex -> {
                    log.error("Error creating cliente juridico", ex);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Void>build());
                });
    }

    @Override
    public Mono<ResponseEntity<Void>> crearClienteNatural(
            Mono<ClienteNaturalRequest> clienteNaturalRequest,
            final ServerWebExchange exchange) {

        log.debug("POST /cliente/natural");
        return clienteNaturalRequest
                .flatMap(request -> {
                    ClienteNaturalCommand command = CommandMapper.toCommand(request);
                    return clienteNaturalService.registrarClienteNatural(command)
                            .map(id -> ResponseEntity
                                    .created(URI.create("/api/v1/cliente/natural/" + id))
                                    .<Void>build()  // Body vacío
                            );
                })
                .onErrorResume(ex -> {
                    log.error("Error creating cliente natural", ex);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Void>build());
                });
    }


    @Override
    public Mono<ResponseEntity<Flux<ClienteJuridicoRequest>>> listarClientesJuridicos(
            final ServerWebExchange exchange) {

        log.debug("GET /cliente/juridico");

        Flux<ClienteJuridicoRequest> clientesFlux = clienteJuridicoService.obtenerTodosClientesJuridicos()
                .map(CommandMapper::toResponse)
                .doOnError(ex -> log.error("Error mapping ClienteJuridico", ex));

        return clientesFlux.hasElements()
                .flatMap(hasElements -> {
                    if (hasElements) {
                        return Mono.just(ResponseEntity.ok(clientesFlux));
                    } else {
                        return Mono.just(ResponseEntity.noContent().<Flux<ClienteJuridicoRequest>>build());
                    }
                })
                .onErrorResume(ex -> {
                    log.error("Error listing clientes juridicos", ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Flux<ClienteJuridicoRequest>>build());
                });
    }

    @Override
    public Mono<ResponseEntity<Flux<ClienteNaturalRequest>>> listarClientesNaturales(
            final ServerWebExchange exchange) {

        log.debug("GET /cliente/natural");

        Flux<ClienteNaturalRequest> clientesFlux = clienteNaturalService.obtenerTodosClientesNaturales()
                .map(CommandMapper::toResponse)
                .doOnError(ex -> log.error("Error mapping ClienteJuridico", ex));

        return clientesFlux.hasElements()
                .flatMap(hasElements -> {
                    if (hasElements) {
                        return Mono.just(ResponseEntity.ok(clientesFlux));
                    } else {
                        return Mono.just(ResponseEntity.noContent().<Flux<ClienteNaturalRequest>>build());
                    }
                })
                .onErrorResume(ex -> {
                    log.error("Error listing clientes juridicos", ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Flux<ClienteNaturalRequest>>build());
                });
    }
}
