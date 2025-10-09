package com.bootcamp.customer_service.domain.valueobject;

import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import java.util.Objects;

public record DocumentoIdentificacion(TipoDocumento type, String numero) {
    public DocumentoIdentificacion {
        Objects.requireNonNull(type);
        Objects.requireNonNull(numero);
    }
}
