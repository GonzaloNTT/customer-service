package com.bootcamp.customer_service.application.mapper.command;

import java.util.Set;


public record ClienteJuridicoCommand(
        String id,
        Set<String> representantes,
        String tipo,
        String nombreComercial
) {
    public ClienteJuridicoCommand {
        if (representantes == null || representantes.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de representantes no puede ser nulo o vacío");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo no puede ser nulo o vacío");
        }
    }
}
