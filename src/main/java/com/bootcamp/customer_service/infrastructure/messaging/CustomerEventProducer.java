package com.bootcamp.customer_service.infrastructure.messaging;

import com.bootcamp.customer_service.application.port.out.event.CustomerEventPublisherPort;
import com.bootcamp.customer_service.events.CustomerCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventProducer implements CustomerEventPublisherPort {

    private final KafkaTemplate<String, CustomerCreatedEvent> kafkaTemplate;

    public CustomerEventProducer(KafkaTemplate<String, CustomerCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishCustomerCreated(CustomerCreatedEvent event) {
        kafkaTemplate.send("customer-created", event.getCustomerId(), event);
    }
}