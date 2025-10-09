package com.bootcamp.customer_service.application.port.out.event;

import com.bootcamp.customer_service.events.CustomerCreatedEvent;

public interface CustomerEventPublisherPort {
    void publishCustomerCreated(CustomerCreatedEvent event);
}
