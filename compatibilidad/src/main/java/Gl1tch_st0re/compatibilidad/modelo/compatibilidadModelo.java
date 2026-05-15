package Gl1tch_st0re.compatibilidad.modelo;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "compatibilidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class compatibilidadModelo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El componente base no puede estar vacío")
    private String componenteBase;

    @NotBlank(message = "El componente compatible no puede estar vacío")
    private String componenteCompatible;

    @NotBlank(message = "El tipo no puede estar vacío")
    private String tipo;
}
