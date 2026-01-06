-- Limpiar tablas si existen (Orden para respetar FKs)
DROP TABLE IF EXISTS info_adicional_factura;
DROP TABLE IF EXISTS pagos_factura;
DROP TABLE IF EXISTS detalles_factura;
DROP TABLE IF EXISTS facturas;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;

-- 1. Tabla Usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    
    -- Campos SRI
    ruc VARCHAR(13) NOT NULL UNIQUE,
    razon_social VARCHAR(300),
    nombre_comercial VARCHAR(300),
    dir_matriz VARCHAR(300),
    codigo_establecimiento VARCHAR(3),
    codigo_punto_emision VARCHAR(3),
    obligado_contabilidad VARCHAR(2),
    nro_contribuyente_especial VARCHAR(13),
    
    firma_path VARCHAR(255),
    firma_password VARCHAR(255),
    logo_path VARCHAR(255),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla Productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    codigo_principal VARCHAR(255) NOT NULL,
    codigo_auxiliar VARCHAR(255),
    nombre VARCHAR(255) NOT NULL,
    valor_unitario DOUBLE PRECISION NOT NULL,
    
    -- Impuestos SRI
    codigo_impuesto VARCHAR(255) NOT NULL,
    codigo_porcentaje VARCHAR(255) NOT NULL,
    tarifa DOUBLE PRECISION NOT NULL,
    
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_producto_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 3. Tabla Facturas
CREATE TABLE facturas (
    id BIGSERIAL PRIMARY KEY,
    numero_comprobante VARCHAR(255),
    fecha_emision DATE,
    cliente_nombre VARCHAR(255),
    cliente_identificacion VARCHAR(255),
    
    total DOUBLE PRECISION,
    estado VARCHAR(255),
    
    -- Campos SRI
    clave_acceso VARCHAR(49) UNIQUE,
    xml_content TEXT,
    mensaje_sri TEXT,
    fecha_autorizacion TIMESTAMP,
    
    -- Campos Adicionales SRI
    dir_establecimiento VARCHAR(255),
    tipo_identificacion_comprador VARCHAR(2),
    direccion_comprador VARCHAR(255),
    
    -- Totales y Desgloses
    total_sin_impuestos DOUBLE PRECISION,
    total_descuento DOUBLE PRECISION,
    propina DOUBLE PRECISION,
    moneda VARCHAR(15),
    
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_factura_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 4. Tabla Detalles Factura
CREATE TABLE detalles_factura (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER,
    precio_unitario DOUBLE PRECISION,
    descuento DOUBLE PRECISION,
    subtotal DOUBLE PRECISION,
    valor_impuesto DOUBLE PRECISION,
    
    factura_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    CONSTRAINT fk_detalle_factura FOREIGN KEY (factura_id) REFERENCES facturas(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- 5. Tabla Pagos Factura
CREATE TABLE pagos_factura (
    id BIGSERIAL PRIMARY KEY,
    forma_pago VARCHAR(2) NOT NULL,
    total NUMERIC(14, 2) NOT NULL,
    plazo NUMERIC NOT NULL,
    unidad_tiempo VARCHAR(10) NOT NULL,
    
    factura_id BIGINT NOT NULL,
    CONSTRAINT fk_pago_factura FOREIGN KEY (factura_id) REFERENCES facturas(id)
);

-- 6. Tabla Info Adicional Factura
CREATE TABLE info_adicional_factura (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    valor VARCHAR(300) NOT NULL,
    
    factura_id BIGINT NOT NULL,
    CONSTRAINT fk_info_factura FOREIGN KEY (factura_id) REFERENCES facturas(id)
);

--------------------------------------------------------------------------------
-- DATOS DE PRUEBA (SEED DATA)
--------------------------------------------------------------------------------

-- USUARIOS
-- User 1: Admin
INSERT INTO usuarios (nombres, apellidos, correo, contrasena, ruc, razon_social, nombre_comercial, dir_matriz, codigo_establecimiento, codigo_punto_emision, obligado_contabilidad, fecha_registro, logo_path)
VALUES ('Juan', 'Perez', 'admin@facto.com', '1234', '1790000000001', 'JUAN PEREZ SA', 'COMERCIAL PEREZ', 'Av. Amazonas y Naciones Unidas', '001', '001', 'SI', NOW(), 'https://png.pngtree.com/png-vector/20221125/ourmid/pngtree-no-image-available-icon-flatvector-illustration-pic-design-profile-vector-png-image_40966566.jpg');

-- User 2: Maria
INSERT INTO usuarios (nombres, apellidos, correo, contrasena, ruc, razon_social, nombre_comercial, dir_matriz, codigo_establecimiento, codigo_punto_emision, obligado_contabilidad, fecha_registro, logo_path)
VALUES ('Maria', 'Gomez', 'maria@test.com', 'abcd', '1190000000001', 'MARIA GOMEZ LTDA', 'NOVEDADES MARIA', 'Calle Larga y Vistosa', '001', '002', 'NO', NOW(), 'https://png.pngtree.com/png-vector/20221125/ourmid/pngtree-no-image-available-icon-flatvector-illustration-pic-design-profile-vector-png-image_40966566.jpg');

-- PRODUCTOS (5 para cada usuario)
-- User 1
INSERT INTO productos (codigo_principal, nombre, valor_unitario, codigo_impuesto, codigo_porcentaje, tarifa, usuario_id) VALUES
('P1-001', 'Laptop Dell', 1200.00, '2', '2', 12.00, 1),
('P1-002', 'Mouse Logitech', 25.00, '2', '2', 12.00, 1),
('P1-003', 'Teclado Mecanico', 80.00, '2', '2', 12.00, 1),
('P1-004', 'Monitor 24"', 150.00, '2', '2', 12.00, 1),
('P1-005', 'Soporte Laptop', 30.00, '2', '0', 0.00, 1);

-- User 2
INSERT INTO productos (codigo_principal, nombre, valor_unitario, codigo_impuesto, codigo_porcentaje, tarifa, usuario_id) VALUES
('P2-001', 'Camiseta', 15.00, '2', '2', 12.00, 2),
('P2-002', 'Pantalon Jean', 40.00, '2', '2', 12.00, 2),
('P2-003', 'Zapatos', 60.00, '2', '2', 12.00, 2),
('P2-004', 'Gorra', 10.00, '2', '2', 12.00, 2),
('P2-005', 'Calcetines', 3.00, '2', '0', 0.00, 2);

-- FACTURAS (2 para cada usuario)

-- --- User 1 ---

-- Factura 1 (User 1): AUTORIZADA, Pago Tarjeta Credito
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, clave_acceso, usuario_id, fecha_autorizacion, dir_establecimiento, tipo_identificacion_comprador, direccion_comprador, total_sin_impuestos, total_descuento, propina, moneda)
VALUES ('001-001-000000001', '2025-12-01', 'Cliente A', '1700000001', 1344.00, 'AUTORIZADA', '1234567890123456789012345678901234567890123456789', 1, NOW(), 'Av. Amazonas', '05', 'Quito Norte', 1200.00, 0.00, 0.00, 'DOLAR');
-- Detalles F1 -> 1 Laptop (1200 + 144 IVA = 1344)
INSERT INTO detalles_factura (cantidad, precio_unitario, descuento, subtotal, valor_impuesto, factura_id, producto_id) VALUES
(1, 1200.00, 0.00, 1200.00, 144.00, 1, 1);
-- Pagos F1 -> Tarjeta de Credito (19)
INSERT INTO pagos_factura (forma_pago, total, plazo, unidad_tiempo, factura_id) VALUES
('19', 1344.00, 30, 'dias', 1);
-- Info Adicional F1
INSERT INTO info_adicional_factura (nombre, valor, factura_id) VALUES
('Email', 'clienteA@email.com', 1),
('Telefono', '0999999999', 1);


-- Factura 2 (User 1): PENDIENTE, Pago Efectivo (Sin utilizacion sistema financiero 01)
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, clave_acceso, usuario_id, fecha_autorizacion, dir_establecimiento, tipo_identificacion_comprador, direccion_comprador, total_sin_impuestos, total_descuento, propina, moneda)
VALUES ('001-001-000000002', '2025-12-05', 'Cliente B', '1700000002', 28.00, 'PENDIENTE', NULL, 1, NULL, 'Av. Amazonas', '05', 'Quito Sur', 25.00, 0.00, 0.00, 'DOLAR');
-- Detalles F2 -> 1 Mouse (25 + 3 IVA = 28)
INSERT INTO detalles_factura (cantidad, precio_unitario, descuento, subtotal, valor_impuesto, factura_id, producto_id) VALUES
(1, 25.00, 0.00, 25.00, 3.00, 2, 2);
-- Pagos F2 -> Sin utilizacion sistema financiero (01)
INSERT INTO pagos_factura (forma_pago, total, plazo, unidad_tiempo, factura_id) VALUES
('01', 28.00, 0, 'dias', 2);


-- --- User 2 ---

-- Factura 3 (User 2): AUTORIZADA, Dinero Electronico (17)
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, clave_acceso, usuario_id, fecha_autorizacion, dir_establecimiento, tipo_identificacion_comprador, direccion_comprador, total_sin_impuestos, total_descuento, propina, moneda)
VALUES ('001-002-000000001', '2025-12-10', 'Cliente C', '1700000003', 44.80, 'AUTORIZADA', '9999999990123456789012345678901234567890123456789', 2, NOW(), 'Calle Larga', '05', 'Cumbaya', 40.00, 0.00, 0.00, 'DOLAR');
-- Detalles F3 -> 1 Pantalon (40 + 4.8 IVA = 44.80)
INSERT INTO detalles_factura (cantidad, precio_unitario, descuento, subtotal, valor_impuesto, factura_id, producto_id) VALUES
(1, 40.00, 0.00, 40.00, 4.80, 3, 7);
-- Pagos F3 -> Dinero Electronico (17)
INSERT INTO pagos_factura (forma_pago, total, plazo, unidad_tiempo, factura_id) VALUES
('17', 44.80, 0, 'dias', 3);


-- Factura 4 (User 2): ENVIADA, Tarjeta Debito (16)
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, clave_acceso, usuario_id, fecha_autorizacion, dir_establecimiento, tipo_identificacion_comprador, direccion_comprador, total_sin_impuestos, total_descuento, propina, moneda)
VALUES ('001-002-000000002', '2025-12-15', 'Cliente D', '1700000004', 3.00, 'ENVIADA', '8888888880123456789012345678901234567890123456789', 2, NULL, 'Calle Larga', '05', 'Tumbaco', 3.00, 0.00, 0.00, 'DOLAR');
-- Detalles F4 -> 1 Calcetines (3 + 0 IVA = 3)
INSERT INTO detalles_factura (cantidad, precio_unitario, descuento, subtotal, valor_impuesto, factura_id, producto_id) VALUES
(1, 3.00, 0.00, 3.00, 0.00, 4, 10);
-- Pagos F4 -> Tarjeta de Debito (16)
INSERT INTO pagos_factura (forma_pago, total, plazo, unidad_tiempo, factura_id) VALUES
('16', 3.00, 0, 'dias', 4);