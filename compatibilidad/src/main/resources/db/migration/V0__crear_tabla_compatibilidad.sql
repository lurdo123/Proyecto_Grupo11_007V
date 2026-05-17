CREATE TABLE IF NOT EXISTS compatibilidad (
    id BIGINT NOT NULL AUTO_INCREMENT,
    componente_base VARCHAR(100) NOT NULL,
    componente_compatible VARCHAR(100) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);