package com.bootcamp.customer_service.application.service.port;

import com.bootcamp.customer_service.application.port.in.CustomerEventListenerPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerEventListenerServiceImpl implements CustomerEventListenerPort {
    @Override
    public void listen(String message) {
        log.info("KAFKA LSITENER: {}", message);
    }
}
