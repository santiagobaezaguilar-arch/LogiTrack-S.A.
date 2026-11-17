package LogiTrack.S.A.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import LogiTrack.S.A.demo.model.TipoMovimiento;

import java.time.LocalDateTime;

@Schema(description = "DTO de respuesta con la información de un movimiento de inventario")
public record MovimientoDtoResponse(

        @Schema(description = "ID único del movimiento", example = "1")
        Long id,

        @Schema(description = "Fecha y hora en que se registró el movimiento", example = "2026-11-15T10:30:00")
        LocalDateTime fecha,

        @Schema(description = "Tipo de movimiento realizado", example = "TRANSFERENCIA")
        TipoMovimiento tipoMovimiento,

        /*
         * Se embebe el UsuarioDtoResponse del responsable.
         * El usuario se obtiene del token JWT en el servicio — no del body.
         */
        @Schema(description = "Usuario que registró el movimiento")
        UsuarioDtoResponse usuario,

        /*
         * Puede ser null cuando el tipo es ENTRADA.
         */
        @Schema(description = "Bodega de origen (null para ENTRADA)")
        BodegaDtoResponse bodegaOrigen,

        /*
         * Puede ser null cuando el tipo es SALIDA.
         */
        @Schema(description = "Bodega de destino (null para SALIDA)")
        BodegaDtoResponse bodegaDestino
) {
}
