package Gl1tch_st0re.clientes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "perfiles_cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class perfilClienteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", unique = true)
    private Long usuarioId;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "nivel_fidelidad")
    private String nivelFidelidad;

    @Column(name = "total_comprado_historico")
    private Double totalCompradoHistorico;
}