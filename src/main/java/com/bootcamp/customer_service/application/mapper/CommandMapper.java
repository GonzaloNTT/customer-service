package com.bootcamp.customer_service.application.mapper;

import com.bootcamp.customer_service.application.mapper.command.ClienteJuridicoCommand;
import com.bootcamp.customer_service.application.mapper.command.ClienteNaturalCommand;
import com.bootcamp.customer_service.application.mapper.command.NoClienteCommand;
import com.bootcamp.customer_service.domain.aggregate.ClienteJuridico;
import com.bootcamp.customer_service.domain.aggregate.ClienteNatural;
import com.bootcamp.customer_service.domain.aggregate.NoCliente;
import com.bootcamp.customer_service.domain.enums.TipoClienteNatural;
import com.bootcamp.customer_service.domain.enums.TipoDocumento;

import com.bootcamp.customer_service.domain.valueobject.DatosUsuario;
import com.bootcamp.customer_service.server.models.*;

public class CommandMapper {

    private CommandMapper() {}

    public static ClienteJuridicoCommand toCommand(ClienteJuridicoRequest request) {
        return new ClienteJuridicoCommand(
                request.getId(),
                request.getRepresentantes(),
                request.getTipo(),
                request.getNombreComercial()
        );
    }

    public static ClienteNaturalCommand toCommand(ClienteNaturalRequest request) {
        return new ClienteNaturalCommand(request.getId(),
                new DatosUsuario(TipoDocumento.valueOf(request.getUsuario().getDocumento().getTipoDocumento()),
                        request.getUsuario().getDocumento().getNumeroDocumento(),
                        request.getUsuario().getTelefono().getNumero(),
                        request.getUsuario().getTelefono().getImei(),
                        request.getUsuario().getEmail()), TipoClienteNatural.valueOf(request.getTipo()));
    }


    public static NoClienteCommand toCommand(NoClienteRequest request) {
        return new NoClienteCommand(new String(request.getId()),
                new DatosUsuario(TipoDocumento.valueOf(request.getUsuario().getDocumento().getTipoDocumento()),
                        request.getUsuario().getDocumento().getNumeroDocumento(),
                        request.getUsuario().getTelefono().getNumero(),
                        request.getUsuario().getTelefono().getImei(),
                        request.getUsuario().getEmail()));
    }

    public static NoClienteRequest toResponse(NoCliente domain) {
        return new NoClienteRequest()
                .id(domain.getId())
                .usuario(new com.bootcamp.customer_service.server.models.DatosUsuario()
                        .documento(new Documento()
                                .tipoDocumento(domain.getUsuario().documento().type().name())
                                .numeroDocumento(domain.getUsuario().documento().numero())
                        )
                        .telefono(new Telefono()
                                .numero(domain.getUsuario().telefono().numero())
                                .imei(domain.getUsuario().telefono().imei())
                        )
                        .email(domain.getUsuario().correo().value())
                );
    }

    public static ClienteJuridicoRequest toResponse(ClienteJuridico domain) {
        return new ClienteJuridicoRequest()
                .id(domain.getId())
                .representantes(domain.getRepresentantes())
                .tipo(domain.getTipo().name());
    }

    public static ClienteNaturalRequest toResponse(ClienteNatural domain) {
        return new ClienteNaturalRequest()
                .id(domain.getId())
                .usuario(new com.bootcamp.customer_service.server.models.DatosUsuario()
                        .documento(new Documento()
                                .tipoDocumento(domain.getUsuario().documento().type().name())
                                .numeroDocumento(domain.getUsuario().documento().numero())
                        )
                        .telefono(new Telefono()
                                .numero(domain.getUsuario().telefono().numero())
                                .imei(domain.getUsuario().telefono().imei())
                        )
                        .email(domain.getUsuario().correo().value())
                )
                .tipo(domain.getTipo().name());
    }
}
