package Gl1tch_st0re.preventas.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "preventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class preventasModelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;

    private String producto;

    private Integer cantidad;

    private String estado;

    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_lanzamiento")
    private LocalDateTime fechaLanzamiento;
}