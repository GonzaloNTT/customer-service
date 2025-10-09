package com.bootcamp.customer_service.domain.aggregate;

import com.bootcamp.customer_service.domain.enums.TipoClienteJuridico;

import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
public class ClienteJuridico {
    private final String id;
    private final String nombreComercial;
    private final Set<String> representantes;
    private final TipoClienteJuridico tipo;

    public ClienteJuridico(Set<String> representantes, TipoClienteJuridico tipo, String nombreComercial) {
        if (nombreComercial == null || nombreComercial.isBlank()) {
            throw new IllegalArgumentException("El nombre juridico no puede ser nulo o vacio.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de cliente juridico no puede ser nulo.");
        }
        if (representantes == null || representantes.isEmpty()) {
            throw new IllegalArgumentException("El cliente juridico debe tener al menos un representante.");
        }
        this.id = new String(UUID.randomUUID().toString());
        this.representantes = representantes;
        this.tipo = tipo;
        this.nombreComercial = nombreComercial;
    }

    public ClienteJuridico(String id, Set<String> representantes, TipoClienteJuridico tipo, String nombreComercial) {
        if (nombreComercial == null || nombreComercial.isBlank()) {
            throw new IllegalArgumentException("El nombre juridico no puede ser nulo o vacio.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de cliente juridico no puede ser nulo.");
        }
        if (representantes == null || representantes.isEmpty()) {
            throw new IllegalArgumentException("El cliente juridico debe tener al menos un representante.");
        }
        this.id = id;
        this.representantes = representantes;
        this.tipo = tipo;
        this.nombreComercial = nombreComercial;
    }

    public void agregarRepresentante(String representante) {
        this.representantes.add(representante);
    }

    public void eliminarRepresentante(String representante) {
        this.representantes.remove(representante);
    }
}
