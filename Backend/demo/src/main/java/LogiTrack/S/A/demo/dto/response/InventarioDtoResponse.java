package LogiTrack.S.A.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta con el stock de un producto en una bodega específica")
public record InventarioDtoResponse(

        /*
         * Se embeben los DTOs de Bodega y Producto en lugar de solo sus IDs,
         * siguiendo el mismo patrón de DetalleVentaDtoResponse en poscampus.
         */
        @Schema(description = "Información de la bodega")
        BodegaDtoResponse bodega,

        @Schema(description = "Información del producto")
        ProductoDtoResponse producto,

        @Schema(description = "Cantidad en stock de este producto en la bodega indicada", example = "250")
        Integer stock
) {
}
