package LogiTrack.S.A.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import LogiTrack.S.A.demo.model.Rol;

@Schema(description = "DTO de solicitud para registrar las credenciales de acceso de un usuario")
public record CredencialDtoRequest(

        @Schema(description = "ID del usuario al que pertenecen estas credenciales", example = "1")
        @NotNull(message = "Error, el ID del usuario no puede estar nulo")
        @Positive(message = "Error, el ID del usuario debe ser positivo")
        Long usuarioId,

        @Schema(description = "Contraseña del usuario (mínimo 6 caracteres)", example = "admin123")
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
        String password,

        @Schema(description = "Rol asignado al usuario", example = "EMPLEADO")
        @NotNull(message = "Error, el rol no puede estar nulo")
        Rol rol
) {
}
