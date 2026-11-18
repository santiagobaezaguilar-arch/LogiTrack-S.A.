package LogiTrack.S.A.demo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales para iniciar sesión")
public record LoginRequest(

        @Schema(description = "Correo electrónico del usuario", example = "carlos@logitrack.com")
        @NotBlank(message = "El correo no puede estar vacío")
        @Email(message = "El correo no tiene un formato válido")
        String correo,

        @Schema(description = "Contraseña del usuario", example = "mi_password123")
        @NotBlank(message = "La contraseña no puede estar vacía")
        String password
) {
}
