CREATE TABLE IF NOT EXISTS preventas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario VARCHAR(100) NOT NULL,
    producto VARCHAR(150) NOT NULL,
    cantidad INT NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'RESERVADO',
    fecha_reserva DATETIME,
    fecha_lanzamiento DATETIME,
    PRIMARY KEY (id)
);