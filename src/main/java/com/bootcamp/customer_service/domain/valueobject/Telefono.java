package com.bootcamp.customer_service.domain.valueobject;

import java.util.Objects;

public record Telefono(String numero, String imei) {
    public Telefono {
        Objects.requireNonNull(numero);
        Objects.requireNonNull(imei);
        if (!numero.matches("\\+?[0-9]+")) throw new IllegalArgumentException("Invalid phone");
        if (!imei.matches("\\+?[0-9]+")) throw new IllegalArgumentException("Invalid imei");
    }
}