package Gl1tch_st0re.compatibilidad.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class compatibilidadRequestDTO {

    @NotBlank(message = "El componente base no puede estar vacío")
    private String componenteBase;

    @NotBlank(message = "El componente compatible no puede estar vacío")
    private String componenteCompatible;
}