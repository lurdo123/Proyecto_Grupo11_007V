package Gl1tch_st0re.garantias.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "garantias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class garantiasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "orden_id")
    private Long ordenId;

    @Column(name = "meses_cobertura")
    private Integer mesesCobertura;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
}