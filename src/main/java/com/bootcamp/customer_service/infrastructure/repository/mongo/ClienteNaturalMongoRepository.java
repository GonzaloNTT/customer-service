package com.bootcamp.customer_service.infrastructure.repository.mongo;

import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ClienteNaturalMongoRepository extends ReactiveMongoRepository<ClienteNatural, String> {
    Mono<ClienteNatural> findByUsuario_Documento_TypeAndUsuario_Documento_Numero(
            String type,
            String numero
    );
}
