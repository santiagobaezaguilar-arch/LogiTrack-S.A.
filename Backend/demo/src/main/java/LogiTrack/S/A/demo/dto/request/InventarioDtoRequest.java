package LogiTrack.S.A.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO de solicitud para registrar o actualizar el stock de un producto en una bodega")
public record InventarioDtoRequest(

        @Schema(description = "ID de la bodega", example = "1")
        @NotNull(message = "El ID de la bodega no puede estar nulo")
        @Positive(message = "El ID de la bodega debe ser un valor positivo")
        Long bodegaId,

        @Schema(description = "ID del producto", example = "3")
        @NotNull(message = "El ID del producto no puede estar nulo")
        @Positive(message = "El ID del producto debe ser un valor positivo")
        Long productoId,

        @Schema(description = "Cantidad en stock para este producto en esta bodega", example = "250")
        @NotNull(message = "El stock no puede estar nulo")
        @PositiveOrZero(message = "El stock debe ser un valor positivo o cero")
        Integer stock
) {
}
