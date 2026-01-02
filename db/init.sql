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
('root', 'root', 'root', 'root', '$2a$12$HkTTBg0OR0BaMMZigBggLOGb82b1QV.78l.NaPDPxfn8bmY9cRi/K'); 