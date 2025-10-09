package com.bootcamp.customer_service.domain.valueobject;

public record Correo(String value) {
    public Correo {
        if (!value.contains("@")) throw new IllegalArgumentException("Invalid email");
    }
}
