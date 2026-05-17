CREATE TABLE IF NOT EXISTS resenas_producto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    producto_id BIGINT,
    usuario_id BIGINT,
    calificacion INT CHECK (calificacion >= 1 AND calificacion <= 5),
    comentario TEXT,
    es_compra_verificada TINYINT(1) CHECK (es_compra_verificada IN (0, 1)),
    fecha_publicacion DATE,
    PRIMARY KEY (id)
);