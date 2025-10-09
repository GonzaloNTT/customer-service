package com.bootcamp.customer_service.domain.service;

import com.bootcamp.customer_service.domain.aggregate.ClienteJuridico;
import com.bootcamp.customer_service.application.mapper.command.ClienteJuridicoCommand;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteJuridicoService {
    Mono<String> registrarClienteJuridico(@Valid ClienteJuridicoCommand clienteJuridicoCommand);
    Mono<ClienteJuridico> obtenerClienteJuridicoPorId(String id);
    Flux<ClienteJuridico> obtenerTodosClientesJuridicos();
    Mono<Void> actualizarClienteJuridico(String id, ClienteJuridicoCommand clienteJuridicoCommand);
    Mono<Void> elminarClienteJuridico(String id);
}