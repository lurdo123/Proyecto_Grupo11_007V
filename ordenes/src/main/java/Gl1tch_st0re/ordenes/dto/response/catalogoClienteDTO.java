package Gl1tch_st0re.ordenes.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class catalogoClienteDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private Boolean disponible;
}