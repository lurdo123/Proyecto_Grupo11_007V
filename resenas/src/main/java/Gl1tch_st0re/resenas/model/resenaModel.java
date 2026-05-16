package Gl1tch_st0re.resenas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "resenas_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class resenaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "calificacion")
    private Integer calificacion;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "es_compra_verificada")
    private Boolean esCompraVerificada;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;
}