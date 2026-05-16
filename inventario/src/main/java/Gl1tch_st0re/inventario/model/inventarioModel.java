package Gl1tch_st0re.inventario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class inventarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "estado_fisico")
    private String estadoFisico;

    @Column(name = "cantidad_disponible")
    private Integer cantidadDisponible;

    @Column(name = "ubicacion_bodega")
    private String ubicacionBodega;
}