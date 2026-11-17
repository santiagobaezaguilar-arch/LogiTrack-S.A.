package LogiTrack.S.A.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta con el detalle de productos de un movimiento de inventario")
public record DetalleMovimientoDtoResponse(

        @Schema(description = "ID único del detalle", example = "1")
        Long id,

        /*
         * Se embebe el MovimientoDtoResponse completo,
         * igual que DetalleVentaDtoResponse embebe VentaDtoResponse en poscampus.
         */
        @Schema(description = "Información del movimiento al que pertenece este detalle")
        MovimientoDtoResponse movimiento,

        @Schema(description = "Información del producto involucrado en el movimiento")
        ProductoDtoResponse producto,

        @Schema(description = "Cantidad de unidades del producto movidas", example = "50")
        Integer cantidad
) {
}
