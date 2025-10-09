package com.bootcamp.customer_service.application.port.out;

import com.bootcamp.customer_service.domain.aggregate.ClienteJuridico;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteJuridicolRepositoryPort {
    Mono<ClienteJuridico> save(ClienteJuridico customer);

    Mono<ClienteJuridico> findById(String id);

    Flux<ClienteJuridico> findAll();

    Mono<Boolean> delete(ClienteJuridico customer);

    Mono<ClienteJuridico> findByNombreComercial(String nombreComercial);
}