package com.bootcamp.customer_service.application.port.out.event;

import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.events.CustomerCreatedEvent;
import reactor.core.publisher.Flux;

public interface ClienteNaturalOrquestador {
    Flux<Object> promoverAClienteVIP(ClienteNatural actualizadoCliente);
}
