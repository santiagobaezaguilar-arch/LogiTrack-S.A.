DROP DATABASE IF EXISTS LogiTrackSA;
CREATE DATABASE LogiTrackSA;
USE LogiTrackSA;

-- 1. Tabla de Usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(100) UNIQUE NOT NULL
);

-- 2. Tabla de Credenciales y Roles (para Spring Security + JWT)
CREATE TABLE credenciales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
    CONSTRAINT fk_credencial_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. Tabla de Productos
CREATE TABLE producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50),
    precio DECIMAL(10, 2) NOT NULL
);

-- 4. Tabla de Bodegas
CREATE TABLE bodega (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(150),
    capacidad INT,
    encargado_id INT,
    CONSTRAINT fk_bodega_usuario FOREIGN KEY (encargado_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- 5. Tabla de Inventario (Relación Bodega-Producto con su stock específico)
CREATE TABLE inventario (
    bodega_id INT,
    producto_id INT,
    stock INT NOT NULL DEFAULT 0,
    PRIMARY KEY (bodega_id, producto_id),
    CONSTRAINT fk_inventario_bodega FOREIGN KEY (bodega_id) REFERENCES bodega(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE
);

-- 6. Tabla de Movimientos de Inventario (Entradas, Salidas y Transferencias entre bodegas)
CREATE TABLE movimiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento ENUM('ENTRADA', 'SALIDA', 'TRANSFERENCIA') NOT NULL,
    usuario_id INT NOT NULL,
    bodega_origen_id INT NULL,  -- Nulo si es una entrada directa
    bodega_destino_id INT NULL, -- Nulo si es una salida externa
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_movimiento_origen FOREIGN KEY (bodega_origen_id) REFERENCES bodega(id),
    CONSTRAINT fk_movimiento_destino FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id)
);

-- 7. Detalle de Productos por Movimiento (Qué productos y cuántas cantidades se movieron)
CREATE TABLE detalle_movimiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    CONSTRAINT fk_detalle_movimiento FOREIGN KEY (movimiento_id) REFERENCES movimiento(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- 8. Tabla de Auditoría (Registro automático de inserciones, actualizaciones, eliminaciones y transferencias)
CREATE TABLE auditoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion VARCHAR(50) NOT NULL,     -- INSERT, UPDATE, DELETE, TRANSFERENCIA, etc.
    entidad_afectada VARCHAR(100) NOT NULL, -- PRODUCTO, BODEGA, MOVIMIENTO, etc.
    entidad_id INT,                         -- ID del registro afectado
    usuario VARCHAR(100) NOT NULL,          -- Correo o nombre del usuario que realizó la acción
    detalles TEXT,                          -- Partes implicadas, valores anteriores/nuevos o descripción del evento
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- ==========================================
-- INSERCIÓN DE DATOS DE PRUEBA
-- ==========================================

-- 1. Insertar 5 Empleados/Usuarios
INSERT INTO usuarios (nombre, telefono, correo) VALUES 
('Carlos Administrador', '3001234567', 'admin@logitrack.com'),
('Ana Gómez', '3109876543', 'ana.gomez@logitrack.com'),
('Luis Pérez', '3204567891', 'luis.perez@logitrack.com'),
('Sofía Torres', '3157891234', 'sofia.torres@logitrack.com'),
('Miguel Ángel Ruiz', '3186543210', 'miguel.ruiz@logitrack.com');

-- 2. Credenciales (Todos con contraseña '1234' en texto plano o simulando BCrypt por simplicidad de pruebas. 
-- Nota: Si usas Spring Security con BCrypt real, recuerda encriptarla en tu backend, aquí se deja el texto de ejemplo o un hash compatible si prefieres).
-- El usuario 1 ('admin@logitrack.com') es rol ADMIN. Los demás son EMPLEADO.
INSERT INTO credenciales (usuario_id, password, rol) VALUES 
(1, '1234', 'ADMIN'),
(2, '1234', 'EMPLEADO'),
(3, '1234', 'EMPLEADO'),
(4, '1234', 'EMPLEADO'),
(5, '1234', 'EMPLEADO');

-- 3. Insertar 3 Bodegas (asignando encargados de la tabla usuarios)
INSERT INTO bodega (nombre, ubicacion, capacidad, encargado_id) VALUES 
('Bodega Central Norte', 'Zona Industrial Calle 80, Bogotá', 5000, 2),
('Bodega Metropolitana Sur', 'Autopista Sur #45-20, Medellín', 3000, 3),
('Bodega Costa Caribe', 'Vía 40 No. 70-12, Barranquilla', 4000, 4);

-- 4. Insertar 7 Productos repartidos en 3 Categorías (Tecnología, Hogar, Oficina)
INSERT INTO producto (nombre, categoria, precio) VALUES 
('Laptop Gamer 15.6"', 'Tecnología', 3500.00),
('Mouse Inalámbrico Ergonómico', 'Tecnología', 85.50),
('Teclado Mecánico RGB', 'Tecnología', 220.00),
('Silla Escritoria Ejecutiva', 'Oficina', 450.00),
('Escritorio en L de Madera', 'Oficina', 780.00),
('Lámpara LED de Escritorio', 'Hogar', 65.00),
('Estantería Metálica Modular', 'Hogar', 310.00);

-- 5. Insertar inventario inicial en las bodegas (Stock de prueba)
INSERT INTO inventario (bodega_id, producto_id, stock) VALUES 
(1, 1, 45), -- Bodega 1 tiene Laptops
(1, 2, 120),-- Bodega 1 tiene Mouses
(1, 3, 80), -- Bodega 1 tiene Teclados
(2, 4, 30), -- Bodega 2 tiene Sillas
(2, 5, 15), -- Bodega 2 tiene Escritorios
(3, 6, 200),-- Bodega 3 tiene Lámparas
(3, 7, 50); -- Bodega 3 tiene Estanterías

-- Nota: Las tablas `movimiento`, `detalle_movimiento` y `auditoria` se mantienen completamente limpias (sin registros), tal como lo solicitaste.