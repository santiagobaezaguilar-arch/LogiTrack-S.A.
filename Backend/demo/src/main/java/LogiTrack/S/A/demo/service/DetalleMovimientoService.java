package LogiTrack.S.A.demo.service;

import LogiTrack.S.A.demo.dto.request.DetalleMovimientoDtoRequest;
import LogiTrack.S.A.demo.dto.response.DetalleMovimientoDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Detalles de Movimiento", description = "Gestión de los productos incluidos en cada movimiento de inventario")
public interface DetalleMovimientoService {

    @Operation(summary = "Registrar un detalle de movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o IDs no existen", content = @Content)
    })
    DetalleMovimientoDtoResponse guardar(
            @Parameter(description = "Datos del detalle a registrar", required = true) DetalleMovimientoDtoRequest dto
    );

    @Operation(summary = "Actualizar un detalle de movimiento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado", content = @Content)
    })
    DetalleMovimientoDtoResponse actualizar(
            @Parameter(description = "Nuevos datos del detalle", required = true) DetalleMovimientoDtoRequest dto,
            @Parameter(description = "ID del detalle a actualizar", required = true, example = "1") Long id
    );

    @Operation(summary = "Eliminar un detalle de movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado", content = @Content)
    })
    void eliminar(
            @Parameter(description = "ID del detalle a eliminar", required = true, example = "1") Long id
    );

    @Operation(summary = "Listar todos los detalles de movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))))
    })
    List<DetalleMovimientoDtoResponse> listar();

    @Operation(summary = "Buscar detalle por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado", content = @Content)
    })
    DetalleMovimientoDtoResponse buscarId(
            @Parameter(description = "ID del detalle a buscar", required = true, example = "1") Long id
    );

    @Operation(summary = "Listar detalles de un movimiento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles del movimiento obtenidos exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))))
    })
    List<DetalleMovimientoDtoResponse> buscarPorMovimiento(
            @Parameter(description = "ID del movimiento", required = true, example = "1") Long movimientoId
    );
}
