CREATE TABLE IF NOT EXISTS inventario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    producto_id BIGINT,
    estado_fisico VARCHAR(20),
    cantidad_disponible INT,
    ubicacion_bodega VARCHAR(50),
    PRIMARY KEY (id)
);