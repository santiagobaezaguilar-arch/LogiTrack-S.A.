package LogiTrack.S.A.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO de respuesta con la información completa de un producto")
public record ProductoDtoResponse(

        @Schema(description = "ID único del producto", example = "1")
        Long id,

        @Schema(description = "Nombre del producto", example = "Cemento Argos 50kg")
        String nombre,

        @Schema(description = "Categoría del producto", example = "Materiales de construcción")
        String categoria,

        @Schema(description = "Precio unitario del producto", example = "35000.00")
        BigDecimal precio
) {
}
