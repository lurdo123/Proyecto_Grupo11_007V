package Gl1tch_st0re.garantias.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class catalogoClienteDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Boolean disponible;
}