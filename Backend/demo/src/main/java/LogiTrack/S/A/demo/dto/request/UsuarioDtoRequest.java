package LogiTrack.S.A.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de solicitud para crear o actualizar un usuario")
public record UsuarioDtoRequest(

        @Schema(description = "Nombre completo del usuario", example = "Carlos Martínez")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Número de teléfono del usuario", example = "3101234567")
        @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
        String telefono,

        @Schema(description = "Correo electrónico del usuario", example = "carlos@logitrack.com")
        @NotBlank(message = "El correo no puede estar vacío")
        @Email(message = "El correo no tiene un formato válido")
        String correo
) {
}
