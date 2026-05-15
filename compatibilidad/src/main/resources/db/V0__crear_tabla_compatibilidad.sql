CREATE DATABASE IF NOT EXISTS compatibilidad_db;
USE compatibilidad_db;

CREATE TABLE compatibilidad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    componente_base VARCHAR(100) NOT NULL,
    componente_compatible VARCHAR(100) NOT NULL,
    tipo VARCHAR(100) NOT NULL
);  