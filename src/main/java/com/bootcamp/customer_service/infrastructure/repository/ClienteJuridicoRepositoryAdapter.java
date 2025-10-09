package com.bootcamp.customer_service.infrastructure.repository;

import com.bootcamp.customer_service.application.port.out.ClienteJuridicolRepositoryPort;
import com.bootcamp.customer_service.domain.aggregate.ClienteJuridico;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import com.bootcamp.customer_service.infrastructure.repository.mongo.ClienteJuridicoMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClienteJuridicoRepositoryAdapter implements ClienteJuridicolRepositoryPort {
    private final ClienteJuridicoMongoRepository repository;
    public ClienteJuridicoRepositoryAdapter(ClienteJuridicoMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<ClienteJuridico> save(ClienteJuridico customer) {
        return repository.save(customer);
    }

    @Override
    public Mono<ClienteJuridico> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<ClienteJuridico> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Boolean> delete(ClienteJuridico customer) {
        return repository.delete(customer).then(Mono.just(true));
    }

    @Override
    public Mono<ClienteJuridico> findByNombreComercial(String nombreComercial) {
        return repository.findByNombreComercial(nombreComercial);
    }
}
