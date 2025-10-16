package com.bootcamp.customer_service.infrastructure.entrypoints;

import com.bootcamp.customer_service.application.mapper.CommandMapper;
import com.bootcamp.customer_service.domain.service.ClienteJuridicoService;
import com.bootcamp.customer_service.domain.service.ClienteNaturalService;
import com.bootcamp.customer_service.domain.service.NoClienteService;
import com.bootcamp.customer_service.server.ApiUtil;
import com.bootcamp.customer_service.server.ClienteApi;
import com.bootcamp.customer_service.server.NoclienteApi;
import com.bootcamp.customer_service.server.models.ClienteJuridicoRequest;
import com.bootcamp.customer_service.server.models.ClienteNaturalRequest;
import com.bootcamp.customer_service.server.models.NoClienteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@Slf4j
public class NoClienteController implements NoclienteApi {
    private final NoClienteService noClienteService;

    NoClienteController(NoClienteService noClienteService) {
        this.noClienteService = noClienteService;
    }

    @Override
    public Mono<ResponseEntity<Void>> crearNoCliente(
            Mono<NoClienteRequest> noClienteRequest,
            final ServerWebExchange exchange) {

        log.debug("POST /nocliente/");
        return noClienteRequest
                .flatMap(request -> {
                    var command = CommandMapper.NO_CLIENTE_TO_COMMAND.apply(request);
                    return noClienteService.registrarNoCliente(command)
                            .then(Mono.just(ResponseEntity
                                    .created(URI.create("/api/v1/cliente/natural/" + command.id()))
                                    .<Void>build()));
                })
                .onErrorResume(ex -> {
                    log.error("Error creating cliente natural", ex);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Void>build());
                });
    }

    @Override
    public Mono<ResponseEntity<Flux<NoClienteRequest>>> listarNoClientes(
            final ServerWebExchange exchange) {

        log.debug("GET /nocliente/");

        Flux<NoClienteRequest> noClientesFlux = noClienteService.obtenerTodosNoClientes()
                .map(CommandMapper.NO_CLIENTE_TO_RESPONSE)
                .doOnError(ex -> log.error("Error mapping ClienteJuridico", ex));

        return noClientesFlux.hasElements()
                .flatMap(hasElements -> {
                    if (hasElements) {
                        return Mono.just(ResponseEntity.ok(noClientesFlux));
                    } else {
                        return Mono.just(ResponseEntity.noContent().<Flux<NoClienteRequest>>build());
                    }
                })
                .onErrorResume(ex -> {
                    log.error("Error listing clientes juridicos", ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Flux<NoClienteRequest>>build());
                });
    }

    @Override
    public Mono<ResponseEntity<Void>> noclienteIdDelete(String id, final ServerWebExchange exchange) {
        log.debug("delete Request={}", id);
        return noClienteService.eliminarNoCliente(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Override
    public Mono<ResponseEntity<NoClienteRequest>> noclienteIdGet(String id,  final ServerWebExchange exchange) {
        log.debug("getById Request={}", id);
        return noClienteService.obtenerNoClientePorId(id)
                .map(CommandMapper.NO_CLIENTE_TO_RESPONSE)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(ex -> log.error("Error fetching customer by id {}", id, ex));
    }

    @Override
    public Mono<ResponseEntity<Void>> noclienteIdPut(
            String id,
            Mono<NoClienteRequest> noClienteRequest,
            final ServerWebExchange exchange) {

        log.debug("PUT /nocliente/{} {}", id, noClienteRequest);

        return noClienteRequest
                .flatMap(request -> {
                    return noClienteService.actualizarNoCliente(id, CommandMapper.NO_CLIENTE_TO_COMMAND.apply(request))
                            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
                })
                .onErrorResume(ex -> {
                    log.error("Error updating cliente juridico id {}", id, ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build());
                });
    }
}
