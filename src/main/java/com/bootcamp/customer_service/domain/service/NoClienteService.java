package com.bootcamp.customer_service.domain.service;

import com.bootcamp.customer_service.domain.aggregate.NoCliente;
import com.bootcamp.customer_service.application.mapper.command.NoClienteCommand;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NoClienteService {
    Mono<String> registrarNoCliente(@Valid NoClienteCommand noCustomerCommand);
    Mono<NoCliente> obtenerNoClientePorId(String id);
    Flux<NoCliente> obtenerTodosNoClientes();
    Mono<NoCliente> actualizarNoCliente(String id, NoClienteCommand noCustomerCommand);
    Mono<Void> eliminarNoCliente(String id);
}
