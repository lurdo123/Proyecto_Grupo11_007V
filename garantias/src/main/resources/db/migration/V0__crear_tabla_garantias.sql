CREATE TABLE IF NOT EXISTS garantias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    producto_id BIGINT,
    orden_id BIGINT,
    meses_cobertura INT,
    fecha_vencimiento DATE,
    PRIMARY KEY (id)
);