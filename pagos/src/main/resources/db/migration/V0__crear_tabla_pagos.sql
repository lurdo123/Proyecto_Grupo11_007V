CREATE TABLE IF NOT EXISTS pagos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    orden_id BIGINT,
    id_transaccion_externa VARCHAR(100),
    metodo_pago VARCHAR(50),
    monto_pagado DECIMAL(10, 2),
    estado_pago VARCHAR(20),
    PRIMARY KEY (id)
);