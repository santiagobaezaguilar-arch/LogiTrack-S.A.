package LogiTrack.S.A.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta con la información de un usuario")
public record UsuarioDtoResponse(

        @Schema(description = "ID único del usuario", example = "1")
        Long id,

        @Schema(description = "Nombre completo del usuario", example = "Carlos Martínez")
        String nombre,

        @Schema(description = "Número de teléfono del usuario", example = "3101234567")
        String telefono,

        @Schema(description = "Correo electrónico del usuario", example = "carlos@logitrack.com")
        String correo
) {
}
