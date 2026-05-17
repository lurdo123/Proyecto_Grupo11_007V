package Gl1tch_st0re.compatibilidad.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class compatibilidadResponseDTO {
    private Long id;
    private String componenteBase;
    private String componenteCompatible;
    private String tipo;
}