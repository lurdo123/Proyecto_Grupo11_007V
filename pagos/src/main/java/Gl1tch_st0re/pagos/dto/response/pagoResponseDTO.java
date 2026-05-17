package Gl1tch_st0re.pagos.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class pagoResponseDTO {
    private Long id;
    private Long ordenId;
    private String idTransaccionExterna;
    private String metodoPago;
    private Double montoPagado;
    private String estadoPago;
}