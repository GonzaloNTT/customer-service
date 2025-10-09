package com.bootcamp.customer_service.application.mapper.command;


import com.bootcamp.customer_service.domain.valueobject.DatosUsuario;

import java.util.Objects;

public record NoClienteCommand(
        String id,
        DatosUsuario usuario
) {
    public NoClienteCommand {
        Objects.requireNonNull(usuario, "Los datos del usuario no pueden ser nulos");
    }
}
