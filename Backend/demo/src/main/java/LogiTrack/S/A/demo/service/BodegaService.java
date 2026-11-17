package LogiTrack.S.A.demo.service;

import LogiTrack.S.A.demo.dto.request.BodegaDtoRequest;
import LogiTrack.S.A.demo.dto.response.BodegaDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Bodegas", description = "Gestión de las bodegas físicas de LogiTrack")
public interface BodegaService {

    @Operation(summary = "Registrar una nueva bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bodega creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BodegaDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o encargado no existe", content = @Content)
    })
    BodegaDtoResponse guardar(
            @Parameter(description = "Datos de la bodega a registrar", required = true) BodegaDtoRequest dto
    );

    @Operation(summary = "Actualizar una bodega existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BodegaDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    })
    BodegaDtoResponse actualizar(
            @Parameter(description = "Nuevos datos de la bodega", required = true) BodegaDtoRequest dto,
            @Parameter(description = "ID de la bodega a actualizar", required = true, example = "1") Long id
    );

    @Operation(summary = "Eliminar una bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bodega eliminada exitosamente"),
            @ApiResponse(responseCode = "400", description = "La bodega tiene inventario o movimientos asociados", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    })
    void eliminar(
            @Parameter(description = "ID de la bodega a eliminar", required = true, example = "1") Long id
    );

    @Operation(summary = "Listar todas las bodegas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BodegaDtoResponse.class))))
    })
    List<BodegaDtoResponse> listar();

    @Operation(summary = "Buscar bodega por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BodegaDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    })
    BodegaDtoResponse buscarId(
            @Parameter(description = "ID de la bodega a buscar", required = true, example = "1") Long id
    );
}
