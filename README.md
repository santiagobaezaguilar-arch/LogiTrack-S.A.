```markdown
# 📦 LogiTrack S.A. — Sistema de Gestión y Auditoría de Bodegas

Backend centralizado desarrollado en **Spring Boot** para la administración de bodegas, control de inventarios, gestión de movimientos (entradas, salidas y transferencias entre sedes) y trazabilidad completa mediante **auditoría automática** y seguridad basada en **JWT**.

---

## 📋 Tabla de Contenidos

1. [Características Principales](#-características-principales)
2. [Tecnologías Utilizadas](#-tecnologías-utilizadas)
3. [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
4. [Requisitos Previos](#-requisitos-previos)
5. [Instalación y Ejecución](#-instalación-y-ejecución)
6. [Documentación de la API (Swagger)](#-documentación-de-la-api-swagger)
7. [Autenticación y Roles](#-autenticación-y-roles)
8. [Ejemplos de Endpoints](#-ejemplos-de-endpoints)
9. [Manejo de Errores](#-manejo-de-errores)

---

## ✨ Características Principales

* **Gestión de Bodegas y Productos:** Operaciones CRUD completas con validación de integridad referencial (impide eliminar registros con stock o movimientos activos).
* **Control de Inventario por Bodega:** Registro y consulta de stock específico por ubicación (`bodega_id` + `producto_id`), incluyendo alerta de **stock bajo (< 10 unidades)**.
* **Movimientos de Inventario:** Soporte para `ENTRADA`, `SALIDA` y `TRANSFERENCIA` entre bodegas, vinculando automáticamente al usuario responsable desde el token JWT.
* **Auditoría Inmutable:** Registro automático de operaciones (`INSERT`, `UPDATE`, `DELETE`) con fecha, hora, entidad afectada, usuario responsable y detalles de la acción.
* **Seguridad Stateless con JWT:** Autenticación sin estado con expiración de 8 horas y control de acceso granular por roles (`ADMIN` y `EMPLEADO`) mediante `@PreAuthorize`.
* **Documentación Interactiva:** Especificación OpenAPI 3 / Swagger UI con soporte integrado para pruebas con Bearer Token.

---

## 🛠️ Tecnologías Utilizadas

* **Java 17+**
* **Spring Boot 3.x**
  * Spring Web
  * Spring Data JPA (Hibernate)
  * Spring Security
  * Spring Validation (Bean Validation)
* **Base de Datos:** MySQL 8.x
* **Seguridad:** JSON Web Tokens (`jjwt`) + cifrado `BCrypt`
* **Documentación:** SpringDoc OpenAPI 3 (Swagger UI)
* **Utilidades:** Lombok

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue una arquitectura en capas desacopladas utilizando **Java Records** como DTOs inmutables y mappers dedicados:

```text
src/main/java/LogiTrack/S/A/demo/
├── auth/           # Controlador y DTOs de Login y Registro (/auth/login, /auth/register)
├── config/         # Configuración de Spring Security, Filtro JWT, Servicio JWT y Swagger
├── controller/     # Controladores REST con control de acceso por roles (@PreAuthorize)
├── dto/
│   ├── request/    # Records de entrada con validaciones (@NotBlank, @Positive, @Size)
│   └── response/   # Records de salida enriquecidos con sub-DTOs
├── exception/      # Manejo global de excepciones (@RestControllerAdvice)
├── mapper/         # Conversión entre Entidades JPA y DTOs
├── model/          # Entidades JPA, claves compuestas (@EmbeddedId) y Enums
├── repository/     # Interfaces JpaRepository con consultas derivadas
├── service/        # Interfaces de servicios documentadas con @Tag y @Operation
│   └── impl/       # Lógica de negocio, validaciones y registro en auditoría
└── DemoApplication.java

```

---

## ⚙️ Requisitos Previos

* **JDK 17** o superior instalado.
* **Maven 3.8+** instalado.
* **MySQL Server 8.0+** en ejecución.

---

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone [https://github.com/tu-usuario/LogiTrackSA.git](https://github.com/tu-usuario/LogiTrackSA.git)
cd LogiTrackSA

```

### 2. Configurar la base de datos MySQL

Ejecuta el script SQL en tu gestor de MySQL para crear la base de datos `LogiTrackSA` y sus tablas:

```sql
DROP DATABASE IF EXISTS LogiTrackSA;
CREATE DATABASE LogiTrackSA;
USE LogiTrackSA;
-- Ejecutar el script schema.sql incluido en el proyecto

```

### 3. Configurar `application.properties`

Edita el archivo `src/main/resources/application.properties` con tus credenciales locales de MySQL:

```properties
spring.application.name=LogiTrackSA

# Configuración de Base de Datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/LogiTrackSA?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=tu_contraseña_mysql
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Manejo de rutas 404 personalizadas
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false

```

### 4. Compilar y ejecutar la aplicación

```bash
mvn clean install
mvn spring-boot:run

```

El servidor iniciará por defecto en `http://localhost:8080`.

---

## 📖 Documentación de la API (Swagger)

Una vez levantado el servidor, accede a la interfaz interactiva de Swagger UI en tu navegador:

* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
* **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### ¿Cómo probar endpoints protegidos en Swagger?

1. Ejecuta `POST /auth/login` o `POST /auth/register`.
2. Copia el valor del campo `token` de la respuesta.
3. Haz clic en el botón **Authorize** (candado verde arriba a la derecha en Swagger UI).
4. Pega el token en el campo `Value` y haz clic en **Authorize**.

---

## 🔐 Autenticación y Roles

El sistema implementa dos roles principales almacenados en la tabla `credenciales`:

| Rol | Permisos |
| --- | --- |
| **`ADMIN`** | Acceso total: crear, actualizar y eliminar bodegas, productos, inventario y usuarios; consultar el historial completo de auditoría. |
| **`EMPLEADO`** | Acceso operativo: consultar bodegas, productos e inventario; registrar y consultar movimientos de inventario y sus detalles. |

> **Nota:** Todo usuario registrado desde el endpoint público `POST /auth/register` obtiene automáticamente el rol `EMPLEADO`.

---

## 📡 Ejemplos de Endpoints

### 1. Registro de Usuario (`POST /auth/register`) — *Público*

**Request:**

```json
{
  "nombre": "Laura Gómez",
  "telefono": "3209876543",
  "correo": "laura@logitrack.com",
  "password": "pass_segura"
}

```

**Response (`201 Created`):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsYXVyYUBsb2dpdHJhY2suY29tIiwiaWF0IjoxNzMxNjg...",
  "correo": "laura@logitrack.com",
  "nombre": "Laura Gómez",
  "rol": "EMPLEADO"
}

```

---

### 2. Iniciar Sesión (`POST /auth/login`) — *Público*

**Request:**

```json
{
  "correo": "carlos@logitrack.com",
  "password": "mi_password123"
}

```

**Response (`200 OK`):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXJsb3NAbG9naXRyYWNrLmNvbSIsImlhdCI6MTczMTY4...",
  "correo": "carlos@logitrack.com",
  "nombre": "Carlos Martínez",
  "rol": "ADMIN"
}

```

---

### 3. Crear Producto (`POST /api/productos`) — *Solo `ADMIN*`

**Headers:** `Authorization: Bearer <TOKEN>`

**Request:**

```json
{
  "nombre": "Cemento Argos 50kg",
  "categoria": "Materiales de construcción",
  "precio": 35000.00
}

```

**Response (`201 Created`):**

```json
{
  "id": 1,
  "nombre": "Cemento Argos 50kg",
  "categoria": "Materiales de construcción",
  "precio": 35000.00
}

```

---

### 4. Registrar Movimiento de Inventario (`POST /api/movimientos`) — *`ADMIN` y `EMPLEADO*`

**Headers:** `Authorization: Bearer <TOKEN>`

> El usuario responsable se extrae automáticamente del token JWT.
> * `ENTRADA`: requiere `bodegaDestinoId`.
> * `SALIDA`: requiere `bodegaOrigenId`.
> * `TRANSFERENCIA`: requiere `bodegaOrigenId` y `bodegaDestinoId`.
> 
> 

**Request:**

```json
{
  "tipoMovimiento": "TRANSFERENCIA",
  "bodegaOrigenId": 1,
  "bodegaDestinoId": 2
}

```

**Response (`201 Created`):**

```json
{
  "id": 10,
  "fecha": "2026-11-18T18:20:00",
  "tipoMovimiento": "TRANSFERENCIA",
  "usuario": {
    "id": 2,
    "nombre": "Laura Gómez",
    "telefono": "3209876543",
    "correo": "laura@logitrack.com"
  },
  "bodegaOrigen": {
    "id": 1,
    "nombre": "Bodega Norte",
    "ubicacion": "Calle 15 #34-20, Bogotá",
    "capacidad": 5000,
    "encargado": null
  },
  "bodegaDestino": {
    "id": 2,
    "nombre": "Bodega Sur",
    "ubicacion": "Av. Boyacá #12-40, Bogotá",
    "capacidad": 3000,
    "encargado": null
  }
}

```

---

### 5. Consultar Productos con Stock Bajo (`GET /api/inventario/stock-bajo`) — *`ADMIN` y `EMPLEADO*`

Devuelve todos los registros de inventario cuyo stock sea menor a **10 unidades**.

---

### 6. Filtrar Auditoría por Tipo de Operación (`GET /api/auditorias/tipo?tipoOperacion=INSERT`) — *Solo `ADMIN*`

**Response (`200 OK`):**

```json
[
  {
    "id": 15,
    "tipoOperacion": "INSERT",
    "entidadAfectada": "Movimiento",
    "entidadId": 10,
    "usuario": "laura@logitrack.com",
    "detalles": "Movimiento TRANSFERENCIA registrado",
    "fechaHora": "2026-11-18T18:20:00"
  }
]

```

---

## 🚨 Manejo de Errores

Todas las excepciones son interceptadas por `GlobalExceptionHandler` (`@RestControllerAdvice`), devolviendo respuestas JSON estandarizadas:

| Código HTTP | Código Interno | Causa |
| --- | --- | --- |
| **`400 Bad Request`** | `VALIDATION_FAILED` | Errores de validación en los campos del JSON (`@NotBlank`, `@Size`, `@Positive`, `@Email`). |
| **`400 Bad Request`** | `BUSINESS_RULE_VIOLATION` | Regla de negocio incumplida (correo duplicado, credenciales inválidas, eliminación con stock activo, falta de bodega origen/destino). |
| **`403 Forbidden`** | `ACCESS_DENIED` | El usuario está autenticado pero no posee el rol requerido (`ADMIN`). |
| **`404 Not Found`** | `RESOURCE_NOT_FOUND` | El ID o recurso solicitado no existe en la base de datos. |
| **`500 Internal Server Error`** | `INTERNAL_SERVER_ERROR` | Error inesperado del servidor. |

**Ejemplo de respuesta de error:**

```json
{
  "timestamp": "2026-11-18T18:25:10",
  "status": 400,
  "message": "La bodega tiene registros de inventario y no puede eliminarse",
  "errorCode": "BUSINESS_RULE_VIOLATION"
}

```

```

```
http://localhost:8080/swagger-ui/index.html
