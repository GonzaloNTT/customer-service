package com.bootcamp.customer_service.domain.service;

import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.application.mapper.command.ClienteNaturalCommand;
import com.bootcamp.customer_service.domain.enums.TipoClienteNatural;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ClienteNaturalService {
    Mono<String> registrarClienteNatural(@Valid ClienteNaturalCommand clienteNaturalCommand);
    Mono<ClienteNatural> obtenerClienteNaturalPorId(String id);
    Mono<Void> actualizarClienteNatural(String id, ClienteNaturalCommand clienteNaturalCommand);
    Mono<Void> eliminarClienteNatural(String id);

    Flux<ClienteNatural> obtenerClientesNaturales(Optional<TipoClienteNatural> type);
}
