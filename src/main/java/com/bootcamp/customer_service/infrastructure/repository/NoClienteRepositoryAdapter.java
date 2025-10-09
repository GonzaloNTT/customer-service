package com.bootcamp.customer_service.infrastructure.repository;

import com.bootcamp.customer_service.application.port.out.ClienteNaturalRepositoryPort;
import com.bootcamp.customer_service.application.port.out.NoClienteRepositoryPort;
import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.domain.aggregate.NoCliente;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import com.bootcamp.customer_service.infrastructure.repository.mongo.ClienteNaturalMongoRepository;
import com.bootcamp.customer_service.infrastructure.repository.mongo.NoClienteMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NoClienteRepositoryAdapter implements NoClienteRepositoryPort {

    private final NoClienteMongoRepository repository;
    public NoClienteRepositoryAdapter(NoClienteMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<NoCliente> save(NoCliente customer) {
        return repository.save(customer);
    }

    @Override
    public Mono<Boolean> existsByDocument(TipoDocumento documentType, String documentNumber) {
        return repository.existsByUsuario_Documento_TypeAndUsuario_Documento_Numero(documentType, documentNumber);
    }

    @Override
    public Mono<NoCliente> findById(String id) {
        return repository.findById(new String(id));
    }

    @Override
    public Flux<NoCliente> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Void> delete(NoCliente customer) {
        return repository.delete(customer);
    }

    @Override
    public Mono<NoCliente> findByDocument(TipoDocumento type, String numero) {
        return repository.findByUsuario_Documento_TypeAndUsuario_Documento_Numero(type, numero);
    }
}