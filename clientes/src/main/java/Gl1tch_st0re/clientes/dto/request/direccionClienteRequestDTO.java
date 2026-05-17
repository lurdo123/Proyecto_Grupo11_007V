package Gl1tch_st0re.clientes.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class direccionClienteRequestDTO {

    @NotNull(message = "El cliente_id es obligatorio")
    private Long clienteId;

    @NotBlank(message = "El tipo de direccion es obligatorio")
    private String tipoDireccion;

    @NotBlank(message = "La calle y numero es obligatoria")
    private String calleNumero;

    @NotBlank(message = "La comuna o ciudad es obligatoria")
    private String comunaCiudad;

    @NotBlank(message = "La region o estado es obligatoria")
    private String regionEstado;

    @NotNull(message = "El campo es_principal es obligatorio")
    private Boolean esPrincipal;
}