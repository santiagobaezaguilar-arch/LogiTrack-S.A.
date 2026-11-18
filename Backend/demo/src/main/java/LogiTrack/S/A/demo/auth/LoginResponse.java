package LogiTrack.S.A.demo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import LogiTrack.S.A.demo.model.Rol;

@Schema(description = "Respuesta al iniciar sesión: token JWT y datos básicos del usuario")
public record LoginResponse(

        @Schema(description = "Token JWT para autenticar las siguientes peticiones")
        String token,

        @Schema(description = "Correo del usuario autenticado", example = "carlos@logitrack.com")
        String correo,

        @Schema(description = "Nombre del usuario autenticado", example = "Carlos Martínez")
        String nombre,

        /*
         * El rol se devuelve en el login para que el frontend pueda
         * mostrar u ocultar secciones sin necesidad de otra llamada a la API.
         * La autorización real siempre la hace el backend con @PreAuthorize.
         */
        @Schema(description = "Rol del usuario: ADMIN o EMPLEADO", example = "ADMIN")
        Rol rol
) {
}
