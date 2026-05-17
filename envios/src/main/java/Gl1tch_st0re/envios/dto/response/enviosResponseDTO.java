package Gl1tch_st0re.envios.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class enviosResponseDTO {
    private Long id;
    private Long ordenId;
    private String usuario;
    private String direccion;
    private String estado;
    private String transportista;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntregaEstimada;
}