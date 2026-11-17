package LogiTrack.S.A.demo.service;

import LogiTrack.S.A.demo.dto.request.InventarioDtoRequest;
import LogiTrack.S.A.demo.dto.response.InventarioDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Inventario", description = "Gestión del stock de productos por bodega")
public interface InventarioService {

    @Operation(summary = "Registrar stock inicial de un producto en una bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o combinación bodega+producto ya existe", content = @Content)
    })
    InventarioDtoResponse guardar(
            @Parameter(description = "Datos del inventario a registrar", required = true) InventarioDtoRequest dto
    );

    @Operation(summary = "Actualizar el stock de un producto en una bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado", content = @Content)
    })
    InventarioDtoResponse actualizar(
            @Parameter(description = "Nuevos datos del inventario", required = true) InventarioDtoRequest dto
    );

    @Operation(summary = "Listar todo el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InventarioDtoResponse.class))))
    })
    List<InventarioDtoResponse> listar();

    @Operation(summary = "Listar inventario de una bodega específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario de la bodega obtenido exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InventarioDtoResponse.class))))
    })
    List<InventarioDtoResponse> buscarPorBodega(
            @Parameter(description = "ID de la bodega a consultar", required = true, example = "1") Long bodegaId
    );

    @Operation(summary = "Listar productos con stock bajo (menos de 10 unidades)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos con stock bajo obtenida",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InventarioDtoResponse.class))))
    })
    List<InventarioDtoResponse> stockBajo();
}
