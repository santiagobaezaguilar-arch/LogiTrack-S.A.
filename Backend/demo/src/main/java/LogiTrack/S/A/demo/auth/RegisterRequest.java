package LogiTrack.S.A.demo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para registrar un nuevo usuario. El rol se asigna automáticamente como EMPLEADO.")
public record RegisterRequest(

        @Schema(description = "Nombre completo del nuevo usuario", example = "Laura Gómez")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Teléfono de contacto (opcional)", example = "3209876543")
        @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
        String telefono,

        @Schema(description = "Correo electrónico (será el usuario de acceso)", example = "laura@logitrack.com")
        @NotBlank(message = "El correo no puede estar vacío")
        @Email(message = "El correo no tiene un formato válido")
        String correo,

        @Schema(description = "Contraseña de acceso (mínimo 6 caracteres)", example = "pass_segura")
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
        String password
) {
}
