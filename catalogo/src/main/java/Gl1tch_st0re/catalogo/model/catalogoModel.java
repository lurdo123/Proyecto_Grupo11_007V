package Gl1tch_st0re.catalogo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class catalogoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    private String categoria;

    private String marca;

    @Column(nullable = false)
    private Boolean disponible;
}