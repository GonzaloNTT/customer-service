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

import java.util.function.Function;

public class CommandMapper {

    private CommandMapper() {}

    // Funciones de mapeo de Request -> Command
    public static final Function<ClienteJuridicoRequest, ClienteJuridicoCommand> CLIENTE_JURIDICO_TO_COMMAND =
            request -> new ClienteJuridicoCommand(
                    request.getId(),
                    request.getRepresentantes(),
                    request.getTipo(),
                    request.getNombreComercial()
            );

    public static final Function<ClienteNaturalRequest, ClienteNaturalCommand> CLIENTE_NATURAL_TO_COMMAND =
            request -> new ClienteNaturalCommand(
                    request.getId(),
                    new DatosUsuario(
                            TipoDocumento.valueOf(request.getUsuario().getDocumento().getTipoDocumento()),
                            request.getUsuario().getDocumento().getNumeroDocumento(),
                            request.getUsuario().getTelefono().getNumero(),
                            request.getUsuario().getTelefono().getImei(),
                            request.getUsuario().getEmail()
                    ),
                    TipoClienteNatural.valueOf(request.getTipo())
            );

    public static final Function<NoClienteRequest, NoClienteCommand> NO_CLIENTE_TO_COMMAND =
            request -> new NoClienteCommand(
                    new String(request.getId()),
                    new DatosUsuario(
                            TipoDocumento.valueOf(request.getUsuario().getDocumento().getTipoDocumento()),
                            request.getUsuario().getDocumento().getNumeroDocumento(),
                            request.getUsuario().getTelefono().getNumero(),
                            request.getUsuario().getTelefono().getImei(),
                            request.getUsuario().getEmail()
                    )
            );

    // Funciones de mapeo de Domain -> Response
    public static final Function<NoCliente, NoClienteRequest> NO_CLIENTE_TO_RESPONSE =
            domain -> new NoClienteRequest()
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

    public static final Function<ClienteJuridico, ClienteJuridicoRequest> CLIENTE_JURIDICO_TO_RESPONSE =
            domain -> new ClienteJuridicoRequest()
                    .id(domain.getId())
                    .representantes(domain.getRepresentantes())
                    .tipo(domain.getTipo().name());

    public static final Function<ClienteNatural, ClienteNaturalRequest> CLIENTE_NATURAL_TO_RESPONSE =
            domain -> new ClienteNaturalRequest()
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
