package LogiTrack.S.A.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import LogiTrack.S.A.demo.model.Rol;

@Schema(description = "DTO de respuesta con las credenciales y rol de un usuario")
public record CredencialDtoResponse(

        @Schema(description = "ID único de la credencial", example = "1")
        Long id,

        /*
         * Se embebe el UsuarioDtoResponse en lugar de solo el ID,
         * igual que DetalleVentaDtoResponse embebe VentaDtoResponse en poscampus.
         * Así la respuesta es completa y no requiere una segunda consulta.
         */
        @Schema(description = "Información del usuario al que pertenece esta credencial")
        UsuarioDtoResponse usuario,

        @Schema(description = "Rol asignado al usuario en el sistema", example = "EMPLEADO")
        Rol rol
) {
        // La contraseña NUNCA se expone en la respuesta.
}
