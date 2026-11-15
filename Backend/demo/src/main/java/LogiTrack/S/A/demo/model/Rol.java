package LogiTrack.S.A.demo.model;

/**
 * Roles disponibles en el sistema.
 * - ADMIN   : acceso completo a todos los endpoints.
 * - EMPLEADO: acceso restringido (consultas y registro de movimientos).
 *
 * El rol se almacena directamente en la tabla credenciales (columna rol),
 * NO dentro del token JWT. Spring Security lo consulta en cada petición
 * cargando las credenciales desde la base de datos.
 */
public enum Rol {
    ADMIN,
    EMPLEADO
}
