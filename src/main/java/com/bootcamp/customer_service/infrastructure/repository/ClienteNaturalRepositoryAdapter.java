package com.bootcamp.customer_service.infrastructure.repository;

import com.bootcamp.customer_service.application.port.out.ClienteNaturalRepositoryPort;
import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import com.bootcamp.customer_service.infrastructure.repository.mongo.ClienteNaturalMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClienteNaturalRepositoryAdapter implements ClienteNaturalRepositoryPort {
    private final ClienteNaturalMongoRepository repository;
    public ClienteNaturalRepositoryAdapter(ClienteNaturalMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<ClienteNatural> save(ClienteNatural customer) {
        return repository.save(customer);
    }

    @Override
    public Mono<ClienteNatural> findByDocument(String documentType, String documentNumber) {
        return repository.findByUsuario_Documento_TypeAndUsuario_Documento_Numero(documentType, documentNumber);
    }

    @Override
    public Mono<ClienteNatural> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<ClienteNatural> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Boolean> delete(ClienteNatural customer) {
        return repository.delete(customer).then(Mono.just(true));
    }

}