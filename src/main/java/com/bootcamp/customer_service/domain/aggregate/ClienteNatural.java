package com.bootcamp.customer_service.domain.aggregate;

import com.bootcamp.customer_service.domain.enums.TipoClienteNatural;

import com.bootcamp.customer_service.domain.valueobject.DatosUsuario;
import lombok.Getter;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.UUID;

@Getter
public class ClienteNatural {
    private final String id;
    private final DatosUsuario usuario;
    private final TipoClienteNatural tipo;

    public ClienteNatural(DatosUsuario usuario, TipoClienteNatural tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de cliente no puede ser nulo");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        this.id = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.tipo = tipo;
    }

    @PersistenceConstructor
    public ClienteNatural(String id, DatosUsuario usuario, TipoClienteNatural tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de cliente no puede ser nulo");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        this.id = id;
        this.usuario = usuario;
        this.tipo = tipo;
    }
}

