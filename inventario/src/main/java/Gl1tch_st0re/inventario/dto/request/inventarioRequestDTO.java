package Gl1tch_st0re.inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class inventarioRequestDTO {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    @NotBlank(message = "El estado físico es obligatorio")
    private String estadoFisico;

    @NotNull(message = "La cantidad disponible es obligatoria")
    private Integer cantidadDisponible;

    @NotBlank(message = "La ubicación en bodega es obligatoria")
    private String ubicacionBodega;
}