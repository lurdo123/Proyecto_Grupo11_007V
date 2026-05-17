package Gl1tch_st0re.clientes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "direcciones_cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class direccionClienteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "tipo_direccion")
    private String tipoDireccion;

    @Column(name = "calle_numero")
    private String calleNumero;

    @Column(name = "comuna_ciudad")
    private String comunaCiudad;

    @Column(name = "region_estado")
    private String regionEstado;

    @Column(name = "es_principal")
    private Boolean esPrincipal;
}