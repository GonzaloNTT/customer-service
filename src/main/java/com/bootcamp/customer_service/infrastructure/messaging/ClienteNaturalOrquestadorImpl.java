package com.bootcamp.customer_service.infrastructure.messaging;

import com.bootcamp.customer_service.application.port.out.event.ClienteNaturalOrquestador;
import com.bootcamp.customer_service.application.port.out.event.CustomerEventPublisherPort;
import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.events.CustomerCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ClienteNaturalOrquestadorImpl implements ClienteNaturalOrquestador {

    @Override
    public Flux<Object> promoverAClienteVIP(ClienteNatural actualizadoCliente) {
        return null;
    }
}