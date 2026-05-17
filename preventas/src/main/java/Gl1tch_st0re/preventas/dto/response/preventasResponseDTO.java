package Gl1tch_st0re.preventas.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class preventasResponseDTO {
    private Long id;
    private String usuario;
    private String producto;
    private Integer cantidad;
    private String estado;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaLanzamiento;
}