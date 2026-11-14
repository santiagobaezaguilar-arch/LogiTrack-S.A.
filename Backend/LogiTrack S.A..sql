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
