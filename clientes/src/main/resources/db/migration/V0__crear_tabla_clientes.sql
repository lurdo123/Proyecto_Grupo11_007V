CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT UNIQUE,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    telefono VARCHAR(20),
    nivel_fidelidad VARCHAR(20) DEFAULT 'Bronce',
    total_comprado_historico DECIMAL(12, 2),
    PRIMARY KEY (id)
);