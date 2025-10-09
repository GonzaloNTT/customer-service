package com.bootcamp.customer_service.domain.aggregate;


import com.bootcamp.customer_service.domain.valueobject.DatosUsuario;
import lombok.Getter;

import java.util.UUID;

@Getter
public class NoCliente {
    private final String id;
    private final DatosUsuario usuario;

    public NoCliente(DatosUsuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        this.id = new String(UUID.randomUUID().toString());
        this.usuario = usuario;
    }

    public NoCliente(String id, DatosUsuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        this.id = id;
        this.usuario = usuario;
    }
}

