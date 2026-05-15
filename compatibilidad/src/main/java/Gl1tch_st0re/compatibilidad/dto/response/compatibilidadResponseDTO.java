package Gl1tch_st0re.compatibilidad.dto.response;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class compatibilidadResponseDTO {
    private Long id;
    private String componenteBase;
    private String tipo;
}