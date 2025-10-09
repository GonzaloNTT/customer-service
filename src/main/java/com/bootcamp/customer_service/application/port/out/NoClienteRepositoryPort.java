package com.bootcamp.customer_service.application.port.out;

import com.bootcamp.customer_service.domain.aggregate.NoCliente;
import com.bootcamp.customer_service.domain.aggregate.NoCliente;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NoClienteRepositoryPort {
    Mono<NoCliente> save(NoCliente customer);

    Mono<Boolean> existsByDocument(TipoDocumento documentType, String documentNumber);

    Mono<NoCliente> findById(String id);

    Flux<NoCliente> findAll();

    Mono<Void> delete(NoCliente customer);

    Mono<NoCliente> findByDocument(TipoDocumento type, String numero);
}
