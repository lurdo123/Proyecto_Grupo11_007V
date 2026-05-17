package Gl1tch_st0re.pagos.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class pagoRequestDTO {

    @NotNull(message = "El orden_id es obligatorio")
    private Long ordenId;

    @NotBlank(message = "El id de transaccion externa es obligatorio")
    private String idTransaccionExterna;

    @NotBlank(message = "El metodo de pago es obligatorio")
    private String metodoPago;

    @NotNull(message = "El monto pagado es obligatorio")
    @Positive(message = "El monto pagado debe ser mayor a 0")
    private Double montoPagado;

    @NotBlank(message = "El estado de pago es obligatorio")
    private String estadoPago;
}