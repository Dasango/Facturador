CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_principal VARCHAR(50) NOT NULL,
    codigo_auxiliar VARCHAR(50),
    nombre VARCHAR(100) NOT NULL,
    valor_unitario DECIMAL(10, 2) NOT NULL,
    iva VARCHAR(20),
    ice VARCHAR(20),
    usuario_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS facturas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_comprobante VARCHAR(50),
    fecha_emision DATE,
    cliente_nombre VARCHAR(100),
    cliente_identificacion VARCHAR(20),
    total DECIMAL(10, 2),
    estado VARCHAR(20),
    usuario_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Datos de prueba (contraseñas no encriptadas para dev simple, en prod usar BCrypt desde app)
-- Para que funcione con BCrypt del backend, idealmente deberíamos insertar hashes o dejar que la app los cree.
-- Dejo un usuario de prueba base. Password: password123 (Hash BCrypt aprox)
INSERT INTO usuarios (cedula, nombres, apellidos, correo, contrasena) VALUES 
('root', 'root', 'root', 'root', 'root'); 

-- Insertar productos de prueba para el usuario root (ID 1)
INSERT INTO productos (codigo_principal, codigo_auxiliar, nombre, valor_unitario, iva, ice, usuario_id) VALUES
('PROD001', 'REF001', 'Consultoría de Software', 150.00, '15%', 'No aplicable', 1),
('PROD002', 'REF002', 'Mantenimiento de Servidores', 80.00, '15%', 'No aplicable', 1),
('PROD003', 'REF003', 'Licencia Antivirus Anual', 45.00, '15%', 'No aplicable', 1),
('PROD004', 'REF004', 'Soporte Remoto (Hora)', 30.00, '15%', 'No aplicable', 1),
('PROD005', 'REF005', 'Cable de Red CAT6', 15.00, '15%', 'No aplicable', 1);

-- Insertar facturas de prueba para el usuario root (ID 1)
INSERT INTO facturas (numero_comprobante, fecha_emision, cliente_nombre, cliente_identificacion, total, estado, usuario_id) VALUES
('001-001-000000001', '2025-12-01', 'Juan Perez', '1712345678', 172.50, 'Autorizado', 1),
('001-001-000000002', '2025-12-05', 'María López', '1718765432', 92.00, 'Autorizado', 1),
('001-001-000000003', '2025-12-10', 'Carlos Sánchez', '1709876543', 51.75, 'Pendiente', 1),
('001-001-000000004', '2025-12-15', 'Empresa XYZ S.A.', '1790011223001', 345.00, 'Rechazado', 1),
('001-001-000000005', '2025-12-20', 'Ana Martínez', '1715556667', 17.25, 'Autorizado', 1); 