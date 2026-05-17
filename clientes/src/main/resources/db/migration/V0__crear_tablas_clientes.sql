CREATE TABLE IF NOT EXISTS perfiles_cliente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT UNIQUE,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    telefono VARCHAR(20),
    nivel_fidelidad VARCHAR(20) DEFAULT 'Bronce',
    total_comprado_historico DECIMAL(12, 2),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS direcciones_cliente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cliente_id BIGINT,
    tipo_direccion VARCHAR(20),
    calle_numero VARCHAR(255),
    comuna_ciudad VARCHAR(50),
    region_estado VARCHAR(50),
    es_principal TINYINT(1) DEFAULT 0 CHECK (es_principal IN (0, 1)),
    PRIMARY KEY (id),
    FOREIGN KEY (cliente_id) REFERENCES perfiles_cliente(id)
);