package LogiTrack.S.A.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta con la información completa de una bodega")
public record BodegaDtoResponse(

        @Schema(description = "ID único de la bodega", example = "1")
        Long id,

        @Schema(description = "Nombre de la bodega", example = "Bodega Norte")
        String nombre,

        @Schema(description = "Ubicación o dirección de la bodega", example = "Calle 15 #34-20, Bogotá")
        String ubicacion,

        @Schema(description = "Capacidad máxima de almacenamiento en unidades", example = "5000")
        Integer capacidad,

        /*
         * Se embebe el UsuarioDtoResponse del encargado.
         * Puede ser null si la bodega aún no tiene encargado asignado.
         */
        @Schema(description = "Usuario encargado de la bodega (puede ser null)")
        UsuarioDtoResponse encargado
) {
}
