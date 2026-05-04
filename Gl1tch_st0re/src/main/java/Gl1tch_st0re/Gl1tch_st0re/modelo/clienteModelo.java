package Gl1tch_st0re.Gl1tch_st0re.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "CLIENTE", schema = "GL1TCH_ST0RE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)

public class clienteModelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE")
    private Integer idCliente;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "APELLIDO")
    private String apellido;

    @Column(name = "EMAIL")
    private String email;

}
