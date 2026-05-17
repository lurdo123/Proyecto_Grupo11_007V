CREATE TABLE IF NOT EXISTS catalogo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria VARCHAR(50),
    marca VARCHAR(50),
    disponible BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
);