CREATE TABLE IF NOT EXISTS roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL
);

-- Insertar los roles obligatorios
INSERT IGNORE INTO roles (id_rol, nombre_rol) VALUES 
(1, 'Administrador'), 
(2, 'Funcionario'), 
(3, 'Ciudadano');

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    DNI CHAR(8) NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    id_rol INT,
    estado BOOLEAN DEFAULT TRUE, -- True = Activo, False = Inactivo
    CONSTRAINT fk_rol_usuario FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);