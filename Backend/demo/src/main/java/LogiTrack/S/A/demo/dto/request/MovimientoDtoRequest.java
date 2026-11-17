package LogiTrack.S.A.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import LogiTrack.S.A.demo.model.TipoMovimiento;

@Schema(description = "DTO de solicitud para registrar un movimiento de inventario")
public record MovimientoDtoRequest(

        @Schema(description = "Tipo de movimiento a registrar", example = "ENTRADA")
        @NotNull(message = "El tipo de movimiento no puede estar nulo")
        TipoMovimiento tipoMovimiento,

        /*
         * Bodega origen: obligatoria para SALIDA y TRANSFERENCIA.
         * Puede ser null para ENTRADA.
         * La validación de coherencia (ej: TRANSFERENCIA necesita ambas bodegas)
         * se hace en el servicio, no aquí.
         */
        @Schema(description = "ID de la bodega origen (requerida para SALIDA y TRANSFERENCIA)", example = "1")
        @Positive(message = "El ID de la bodega origen debe ser un valor positivo")
        Long bodegaOrigenId,

        /*
         * Bodega destino: obligatoria para ENTRADA y TRANSFERENCIA.
         * Puede ser null para SALIDA.
         */
        @Schema(description = "ID de la bodega destino (requerida para ENTRADA y TRANSFERENCIA)", example = "2")
        @Positive(message = "El ID de la bodega destino debe ser un valor positivo")
        Long bodegaDestinoId
) {
}
