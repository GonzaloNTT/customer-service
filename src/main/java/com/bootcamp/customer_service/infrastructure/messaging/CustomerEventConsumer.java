package com.bootcamp.customer_service.infrastructure.messaging;

import com.bootcamp.customer_service.application.port.in.CustomerEventListenerPort;
import com.bootcamp.customer_service.events.CustomerCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerEventConsumer {
    private final CustomerEventListenerPort customerEventListenerPort;

    public CustomerEventConsumer(CustomerEventListenerPort customerEventListenerPort) {
        this.customerEventListenerPort = customerEventListenerPort;
    }

    @KafkaListener(topics = "customer-created", groupId = "customer-service-group")
    public void listen(CustomerCreatedEvent event) {
        try {
            customerEventListenerPort.listen("Customer created: " + event.getCustomerId());
            log.info("📥 Received: {}", event);
        } catch (Exception e) {
            log.error("❌ Error processing event: {}", event, e);
            //Se puede enviar a un DLQ
        }
    }
}

