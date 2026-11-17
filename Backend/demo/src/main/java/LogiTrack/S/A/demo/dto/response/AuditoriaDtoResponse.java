package LogiTrack.S.A.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import LogiTrack.S.A.demo.model.TipoOperacion;

import java.time.LocalDateTime;

@Schema(description = "DTO de respuesta con el registro de un evento de auditoría")
public record AuditoriaDtoResponse(

        @Schema(description = "ID único del registro de auditoría", example = "1")
        Long id,

        @Schema(description = "Tipo de operación realizada sobre la entidad", example = "UPDATE")
        TipoOperacion tipoOperacion,

        @Schema(description = "Nombre de la entidad que fue afectada", example = "Producto")
        String entidadAfectada,

        @Schema(description = "ID del registro afectado dentro de su entidad", example = "5")
        Long entidadId,

        @Schema(description = "Correo o nombre del usuario responsable del cambio", example = "carlos@logitrack.com")
        String usuario,

        @Schema(description = "Descripción del cambio: valores anteriores/nuevos o descripción del evento")
        String detalles,

        @Schema(description = "Fecha y hora exacta del cambio", example = "2026-11-15T10:30:00")
        LocalDateTime fechaHora
) {
}
