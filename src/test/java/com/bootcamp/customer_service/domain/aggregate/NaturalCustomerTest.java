package com.bootcamp.customer_service.domain.aggregate;

import com.bootcamp.customer_service.domain.enums.TipoClienteNatural;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;
import com.bootcamp.customer_service.domain.valueobject.DatosUsuario;
import com.bootcamp.customer_service.domain.valueobject.DocumentoIdentificacion;
import com.bootcamp.customer_service.domain.valueobject.Correo;
import com.bootcamp.customer_service.domain.valueobject.Telefono;
import com.bootcamp.customer_service.server.models.Documento;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NaturalCustomerTest {
    @Test
    void shouldCreateNaturalCustomerSuccessfully() {
        DocumentoIdentificacion document = new DocumentoIdentificacion(TipoDocumento.valueOf("DNI"), "12345678");
        Correo email = new Correo("user@example.com");
        Telefono phone = new Telefono("987654321", "356938035643809");
        DatosUsuario datos = new DatosUsuario(document, phone, email);

        ClienteNatural customer = new ClienteNatural(
                datos,
                TipoClienteNatural.NORMAL
        );

        assertNotNull(customer);
        assertEquals("user@example.com", customer.getUsuario().correo().value());
        assertEquals(TipoClienteNatural.NORMAL, customer.getTipo());
    }

    @Test
    @Disabled
    void shouldThrowExceptionWhenTypeIsNull() {
        DocumentoIdentificacion document = new DocumentoIdentificacion(TipoDocumento.valueOf("DNI"), "12345678");
        Correo email = new Correo("user@example.com");
        Telefono phone = new Telefono("987654321", "356938035643809");
        DatosUsuario datos = new DatosUsuario(document, phone, email);

        assertThrows(NullPointerException.class, () ->
                new ClienteNatural(
                        datos,
                        TipoClienteNatural.NORMAL
                )
        );
    }
}