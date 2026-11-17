package LogiTrack.S.A.demo.service;

import LogiTrack.S.A.demo.dto.request.MovimientoDtoRequest;
import LogiTrack.S.A.demo.dto.response.MovimientoDtoResponse;
import LogiTrack.S.A.demo.model.TipoMovimiento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Movimientos", description = "Registro y consulta de movimientos de inventario entre bodegas")
public interface MovimientoService {

    @Operation(summary = "Registrar un nuevo movimiento de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o reglas de negocio violadas", content = @Content)
    })
    MovimientoDtoResponse guardar(
            @Parameter(description = "Datos del movimiento a registrar", required = true) MovimientoDtoRequest dto,
            @Parameter(description = "Correo del usuario logueado (extraído del JWT)", required = true) String correoUsuario
    );

    @Operation(summary = "Listar todos los movimientos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovimientoDtoResponse.class))))
    })
    List<MovimientoDtoResponse> listar();

    @Operation(summary = "Buscar movimiento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content)
    })
    MovimientoDtoResponse buscarId(
            @Parameter(description = "ID del movimiento a buscar", required = true, example = "1") Long id
    );

    @Operation(summary = "Filtrar movimientos por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos filtrados exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovimientoDtoResponse.class))))
    })
    List<MovimientoDtoResponse> buscarPorTipo(
            @Parameter(description = "Tipo de movimiento: ENTRADA, SALIDA o TRANSFERENCIA", required = true) TipoMovimiento tipo
    );

    @Operation(summary = "Filtrar movimientos por rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos en el rango indicado",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovimientoDtoResponse.class))))
    })
    List<MovimientoDtoResponse> buscarPorFechas(
            @Parameter(description = "Fecha de inicio del rango", required = true) LocalDateTime desde,
            @Parameter(description = "Fecha de fin del rango", required = true) LocalDateTime hasta
    );
}
