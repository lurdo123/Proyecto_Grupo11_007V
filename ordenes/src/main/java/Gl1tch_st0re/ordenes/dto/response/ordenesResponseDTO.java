package Gl1tch_st0re.ordenes.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ordenesResponseDTO {
    private Long id;
    private String usuario;
    private String producto;
    private Integer cantidad;
    private String estado;
    private LocalDateTime fechaCreacion;
}