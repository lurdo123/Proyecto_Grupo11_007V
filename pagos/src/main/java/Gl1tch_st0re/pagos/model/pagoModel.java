package Gl1tch_st0re.pagos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class pagoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id")
    private Long ordenId;

    @Column(name = "id_transaccion_externa")
    private String idTransaccionExterna;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "monto_pagado")
    private Double montoPagado;

    @Column(name = "estado_pago")
    private String estadoPago;
}