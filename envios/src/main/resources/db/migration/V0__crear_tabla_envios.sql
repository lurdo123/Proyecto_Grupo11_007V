CREATE TABLE IF NOT EXISTS envios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    orden_id BIGINT NOT NULL,
    usuario VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    transportista VARCHAR(100),
    fecha_envio DATETIME,
    fecha_entrega_estimada DATETIME,
    PRIMARY KEY (id)
);