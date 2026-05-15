package Gl1tch_st0re.garantias.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class garantiasRequestDTO {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    @NotNull(message = "El orden_id es obligatorio")
    private Long ordenId;

    @NotNull(message = "Los meses de cobertura son obligatorios")
    private Integer mesesCobertura;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;
}