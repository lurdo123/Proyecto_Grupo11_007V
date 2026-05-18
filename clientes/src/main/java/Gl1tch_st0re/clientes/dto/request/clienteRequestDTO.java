package Gl1tch_st0re.clientes.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class clienteRequestDTO {

    @NotNull(message = "El usuario_id es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    private String nivelFidelidad = "Bronce";

    @NotNull(message = "El total comprado historico es obligatorio")
    @PositiveOrZero(message = "El total comprado debe ser mayor o igual a 0")
    private Double totalCompradoHistorico;
}