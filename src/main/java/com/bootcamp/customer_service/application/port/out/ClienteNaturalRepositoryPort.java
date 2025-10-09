package com.bootcamp.customer_service.application.port.out;

import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteNaturalRepositoryPort {
    Mono<ClienteNatural> save(ClienteNatural customer);

    Mono<ClienteNatural> findByDocument(String documentType, String documentNumber);

    Mono<ClienteNatural> findById(String id);

    Flux<ClienteNatural> findAll();

    Mono<Boolean> delete(ClienteNatural customer);
}
