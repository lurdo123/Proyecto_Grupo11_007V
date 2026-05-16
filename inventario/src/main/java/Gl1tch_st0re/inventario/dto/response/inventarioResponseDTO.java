package Gl1tch_st0re.inventario.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class inventarioResponseDTO {
    private Long id;
    private Long productoId;
    private String estadoFisico;
    private Integer cantidadDisponible;
    private String ubicacionBodega;
}