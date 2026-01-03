-- Eliminar tablas si existen
DROP TABLE IF EXISTS detalles_factura;
DROP TABLE IF EXISTS facturas;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;

-- Crear tabla Usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Campos SRI
    ruc VARCHAR(13) UNIQUE,
    razon_social VARCHAR(255),
    firma_path VARCHAR(255),
    firma_password VARCHAR(255),
    
    -- Logo
    logo_path VARCHAR(255) DEFAULT 'https://png.pngtree.com/png-vector/20221125/ourmid/pngtree-no-image-available-icon-flatvector-illustration-pic-design-profile-vector-png-image_40966566.jpg'
);

-- Crear tabla Productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    codigo_principal VARCHAR(255) NOT NULL,
    codigo_auxiliar VARCHAR(255),
    nombre VARCHAR(255) NOT NULL,
    valor_unitario DOUBLE PRECISION NOT NULL,
    iva VARCHAR(50), 
    ice VARCHAR(50),
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_producto_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Crear tabla Facturas
CREATE TABLE facturas (
    id BIGSERIAL PRIMARY KEY,
    numero_comprobante VARCHAR(255),
    fecha_emision DATE,
    cliente_nombre VARCHAR(255),
    cliente_identificacion VARCHAR(255),
    total DOUBLE PRECISION,
    estado VARCHAR(50), -- GENERADA, FIRMADA, ENVIADA, AUTORIZADA, RECHAZADA
    
    -- Campos SRI
    clave_acceso VARCHAR(49) UNIQUE,
    xml_content TEXT,
    mensaje_sri TEXT,
    fecha_autorizacion TIMESTAMP,
    
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_factura_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Crear tabla Detalles Factura
CREATE TABLE detalles_factura (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER,
    precio_unitario DOUBLE PRECISION,
    descuento DOUBLE PRECISION,
    subtotal DOUBLE PRECISION,
    factura_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    CONSTRAINT fk_detalle_factura FOREIGN KEY (factura_id) REFERENCES facturas(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- INSERTS DE PRUEBA (Passwords en texto plano)

-- Usuario 1: Admin (ID 1)
INSERT INTO usuarios (cedula, nombres, apellidos, correo, contrasena, ruc, razon_social, fecha_registro, logo_path)
VALUES ('1717171717', 'Juan', 'Perez', 'admin@facto.com', '1234', '1790000000001', 'EMPRESA DE PRUEBA', NOW(), 'https://png.pngtree.com/png-vector/20221125/ourmid/pngtree-no-image-available-icon-flatvector-illustration-pic-design-profile-vector-png-image_40966566.jpg')
ON CONFLICT DO NOTHING;

-- Usuario 2: Test User (ID 2)
INSERT INTO usuarios (cedula, nombres, apellidos, correo, contrasena, ruc, razon_social, fecha_registro, logo_path)
VALUES ('1100110011', 'Maria', 'Gomez', 'maria@test.com', 'abcd', '1190000000001', 'COMERCIAL GOMEZ', NOW(), 'https://png.pngtree.com/png-vector/20221125/ourmid/pngtree-no-image-available-icon-flatvector-illustration-pic-design-profile-vector-png-image_40966566.jpg')
ON CONFLICT DO NOTHING;

-- PRODUCTOS - Usuario 1
INSERT INTO productos (codigo_principal, nombre, valor_unitario, iva, usuario_id) VALUES
('PROD001', 'Consultoria Software', 100.00, '15%', 1),
('PROD002', 'Mantenimiento PC', 50.00, '15%', 1),
('PROD003', 'Licencia Antivirus', 35.00, '15%', 1);

-- PRODUCTOS - Usuario 2
INSERT INTO productos (codigo_principal, nombre, valor_unitario, iva, usuario_id) VALUES
('ITEM-A', 'Zapatos Deportivos', 85.00, '15%', 2),
('ITEM-B', 'Camiseta Polo', 25.00, '15%', 2);

-- FACTURAS y DETALLES - Usuario 1
-- Factura 1 (Autorizada)
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, clave_acceso, usuario_id, fecha_autorizacion) VALUES
('001-001-000000001', '2025-12-01', 'Cliente A', '1700000001', 115.00, 'AUTORIZADO', '1234567890123456789012345678901234567890123456789', 1, NOW());
INSERT INTO detalles_factura (cantidad, precio_unitario, descuento, subtotal, factura_id, producto_id) VALUES
(1, 100.00, 0.00, 100.00, 1, 1); -- 1x Consultoria (ID 1)

-- Factura 2 (Enviada)
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, clave_acceso, usuario_id, fecha_autorizacion) VALUES
('001-001-000000002', '2025-12-05', 'Cliente B', '1700000002', 57.50, 'ENVIADA', '9876543210123456789012345678901234567890123456789', 1, NULL);
INSERT INTO detalles_factura (cantidad, precio_unitario, descuento, subtotal, factura_id, producto_id) VALUES
(1, 50.00, 0.00, 50.00, 2, 2); -- 1x Mantenimiento (ID 2)

-- Factura 3 (Rechazada / User 1)
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, clave_acceso, usuario_id, fecha_autorizacion) VALUES
('001-001-000000003', '2025-12-10', 'Cliente C', '1700000003', 200.00, 'RECHAZADA', '1111111111111111111111111111111111111111111111111', 1, NULL);
INSERT INTO detalles_factura (cantidad, precio_unitario, descuento, subtotal, factura_id, producto_id) VALUES
(2, 100.00, 0.00, 200.00, 3, 1); -- 2x Consultoria

-- Factura 4 (Autorizada / User 2)
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, clave_acceso, usuario_id, fecha_autorizacion) VALUES
('001-001-000000055', '2025-12-20', 'Comprador X', '1100000001', 97.75, 'AUTORIZADO', '2222222222222222222222222222222222222222222222222', 2, NOW());
INSERT INTO detalles_factura (cantidad, precio_unitario, descuento, subtotal, factura_id, producto_id) VALUES
(1, 85.00, 0.00, 85.00, 4, 4); -- 1x Zapatos (ID 4 - verify ID sequence!)
-- Note: Product IDs are SERIAL. User 1 inserted 3 rows (ID 1,2,3). User 2 inserted 2 rows (ID 4,5).
