package Gl1tch_st0re.resenas.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class resenaRequestDTO {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    @NotNull(message = "El usuario_id es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La calificacion es obligatoria")
    @Min(value = 1, message = "La calificacion minima es 1")
    @Max(value = 5, message = "La calificacion maxima es 5")
    private Integer calificacion;

    @NotBlank(message = "El comentario es obligatorio")
    private String comentario;

    @NotNull(message = "El campo es_compra_verificada es obligatorio")
    private Boolean esCompraVerificada;

    @NotNull(message = "La fecha de publicacion es obligatoria")
    private LocalDate fechaPublicacion;
}