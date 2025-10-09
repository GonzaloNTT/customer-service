package com.bootcamp.customer_service.domain.valueobject;

import com.bootcamp.customer_service.domain.enums.TipoDocumento;
import java.util.Objects;

public record DatosUsuario(DocumentoIdentificacion documento, Telefono telefono, Correo correo) {

    // Compact constructor: válido para records
    public DatosUsuario {
        Objects.requireNonNull(documento, "El documento de identificación no puede ser nulo");
        Objects.requireNonNull(telefono, "El teléfono no puede ser nulo");
        Objects.requireNonNull(correo, "El correo no puede ser nulo");
    }

    // Sobrecarga de constructor con valores primitivos
    public DatosUsuario(TipoDocumento tipoDocumento, String numeroDocumento,
                        String telefono, String imei, String correo) {
        this(new DocumentoIdentificacion(tipoDocumento, numeroDocumento),
                new Telefono(telefono, imei),
                new Correo(correo));
    }
}
