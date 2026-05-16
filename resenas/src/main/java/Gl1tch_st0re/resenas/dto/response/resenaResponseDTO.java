package Gl1tch_st0re.resenas.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class resenaResponseDTO {
    private Long id;
    private Long productoId;
    private Long usuarioId;
    private Integer calificacion;
    private String comentario;
    private Boolean esCompraVerificada;
    private LocalDate fechaPublicacion;
}