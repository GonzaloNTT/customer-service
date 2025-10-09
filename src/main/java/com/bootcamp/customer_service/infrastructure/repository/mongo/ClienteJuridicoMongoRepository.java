package com.bootcamp.customer_service.infrastructure.repository.mongo;

import com.bootcamp.customer_service.domain.aggregate.ClienteJuridico;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ClienteJuridicoMongoRepository extends ReactiveMongoRepository<ClienteJuridico, String> {

    Mono<ClienteJuridico> findByNombreComercial(String nombreComercial);
}
