package com.bootcamp.customer_service.application.mapper.command;

import com.bootcamp.customer_service.domain.enums.TipoClienteNatural;
import com.bootcamp.customer_service.domain.valueobject.DatosUsuario;

import java.util.Objects;

public record ClienteNaturalCommand(
        String id,
        DatosUsuario usuario,
        TipoClienteNatural tipo
) {
    public ClienteNaturalCommand {
        Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
        Objects.requireNonNull(tipo, "El tipo de cliente natural no puede ser nulo");
    }
}
