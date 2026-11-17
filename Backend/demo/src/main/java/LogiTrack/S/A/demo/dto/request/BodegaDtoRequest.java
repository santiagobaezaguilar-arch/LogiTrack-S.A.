package LogiTrack.S.A.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO de solicitud para crear o actualizar una bodega")
public record BodegaDtoRequest(

        @Schema(description = "Nombre de la bodega", example = "Bodega Norte")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Ubicación o dirección de la bodega", example = "Calle 15 #34-20, Bogotá")
        @Size(max = 150, message = "La ubicación no puede superar los 150 caracteres")
        String ubicacion,

        @Schema(description = "Capacidad máxima de almacenamiento en unidades", example = "5000")
        @PositiveOrZero(message = "La capacidad debe ser un valor positivo o cero")
        Integer capacidad,

        /*
         * El encargado es opcional al crear la bodega.
         * Se envía el ID del usuario que será asignado como encargado.
         */
        @Schema(description = "ID del usuario encargado de la bodega (opcional)", example = "2")
        @Positive(message = "El ID del encargado debe ser un valor positivo")
        Long encargadoId
) {
}
