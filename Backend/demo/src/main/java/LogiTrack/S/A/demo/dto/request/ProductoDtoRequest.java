package LogiTrack.S.A.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "DTO de solicitud para crear o actualizar un producto")
public record ProductoDtoRequest(

        @Schema(description = "Nombre del producto", example = "Cemento Argos 50kg")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Categoría del producto", example = "Materiales de construcción")
        @Size(max = 50, message = "La categoría no puede superar los 50 caracteres")
        String categoria,

        @Schema(description = "Precio unitario del producto", example = "35000.00")
        @NotNull(message = "El precio no puede estar nulo")
        @Positive(message = "El precio debe ser un valor positivo")
        BigDecimal precio
) {
}
