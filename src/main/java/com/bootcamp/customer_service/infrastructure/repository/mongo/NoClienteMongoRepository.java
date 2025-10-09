package com.bootcamp.customer_service.infrastructure.repository.mongo;

import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.domain.aggregate.NoCliente;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface NoClienteMongoRepository extends ReactiveMongoRepository<NoCliente, String> {
    Mono<Boolean> existsByUsuario_Documento_TypeAndUsuario_Documento_Numero(
            TipoDocumento type,
            String numero
    );

    Mono<NoCliente> findByUsuario_Documento_TypeAndUsuario_Documento_Numero(
            TipoDocumento type,
            String numero
    );
}
