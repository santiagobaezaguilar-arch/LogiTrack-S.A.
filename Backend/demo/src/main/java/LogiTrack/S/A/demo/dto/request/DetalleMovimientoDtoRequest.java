package LogiTrack.S.A.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO de solicitud para registrar el detalle de productos de un movimiento")
public record DetalleMovimientoDtoRequest(

        @Schema(description = "ID del movimiento al que pertenece este detalle", example = "1")
        @NotNull(message = "El ID del movimiento no puede estar nulo")
        @Positive(message = "El ID del movimiento debe ser un valor positivo")
        Long movimientoId,

        @Schema(description = "ID del producto involucrado en el movimiento", example = "3")
        @NotNull(message = "El ID del producto no puede estar nulo")
        @Positive(message = "El ID del producto debe ser un valor positivo")
        Long productoId,

        @Schema(description = "Cantidad de unidades del producto a mover", example = "50")
        @NotNull(message = "La cantidad no puede estar nula")
        @Positive(message = "La cantidad debe ser un valor positivo")
        Integer cantidad
) {
}
