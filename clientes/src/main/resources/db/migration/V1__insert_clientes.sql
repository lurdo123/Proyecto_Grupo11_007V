INSERT INTO perfiles_cliente (usuario_id, nombre, apellido, telefono, nivel_fidelidad, total_comprado_historico) VALUES
(1, 'Juan', 'Pérez', '+56912345678', 'Oro', 1500000.00),
(2, 'María', 'González', '+56987654321', 'Plata', 750000.00),
(3, 'Carlos', 'López', '+56911111111', 'Bronce', 200000.00),
(4, 'Ana', 'Martínez', '+56922222222', 'Oro', 3200000.00),
(5, 'Pedro', 'Soto', '+56933333333', 'Bronce', 50000.00);

INSERT INTO direcciones_cliente (cliente_id, tipo_direccion, calle_numero, comuna_ciudad, region_estado, es_principal) VALUES
(1, 'Casa', 'Av. Providencia 1234', 'Providencia', 'Metropolitana', 1),
(1, 'Trabajo', 'Calle Huérfanos 890', 'Santiago', 'Metropolitana', 0),
(2, 'Casa', 'Los Aromos 456', 'Las Condes', 'Metropolitana', 1),
(3, 'Casa', 'Av. Maipú 789', 'Maipú', 'Metropolitana', 1),
(4, 'Casa', 'Calle Larga 321', 'Vitacura', 'Metropolitana', 1);