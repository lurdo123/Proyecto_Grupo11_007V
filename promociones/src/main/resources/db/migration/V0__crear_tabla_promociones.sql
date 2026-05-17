CREATE TABLE IF NOT EXISTS promociones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    descuento_porcentaje DECIMAL(5,2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
);