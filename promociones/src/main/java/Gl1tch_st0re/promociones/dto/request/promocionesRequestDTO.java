package Gl1tch_st0re.promociones.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class promocionesRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    private String descripcion;

    @NotNull(message = "El descuento es obligatorio")
    private BigDecimal descuentoPorcentaje;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}