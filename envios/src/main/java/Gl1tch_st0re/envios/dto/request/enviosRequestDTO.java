package Gl1tch_st0re.envios.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class enviosRequestDTO {

    @NotNull(message = "El orden_id es obligatorio")
    private Long ordenId;

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @NotBlank(message = "El transportista es obligatorio")
    private String transportista;

    @NotNull(message = "La fecha de entrega estimada es obligatoria")
    private LocalDateTime fechaEntregaEstimada;
}