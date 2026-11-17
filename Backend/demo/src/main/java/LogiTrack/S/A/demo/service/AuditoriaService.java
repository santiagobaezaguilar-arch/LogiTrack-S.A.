package LogiTrack.S.A.demo.service;

import LogiTrack.S.A.demo.dto.response.AuditoriaDtoResponse;
import LogiTrack.S.A.demo.model.TipoOperacion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Auditoría", description = "Consulta del historial de cambios registrados automáticamente en el sistema")
public interface AuditoriaService {

    /*
     * registrar() es llamado internamente por los otros servicios — no es un endpoint REST.
     * Se declara en la interfaz para que cualquier servicio pueda inyectar AuditoriaService
     * y registrar cambios de forma uniforme.
     */
    void registrar(TipoOperacion tipoOperacion, String entidadAfectada,
                   Long entidadId, String usuario, String detalles);

    @Operation(summary = "Listar toda la auditoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial completo obtenido exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuditoriaDtoResponse.class))))
    })
    List<AuditoriaDtoResponse> listar();

    @Operation(summary = "Filtrar auditoría por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros del usuario obtenidos exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuditoriaDtoResponse.class))))
    })
    List<AuditoriaDtoResponse> buscarPorUsuario(
            @Parameter(description = "Correo o nombre del usuario", required = true, example = "carlos@logitrack.com") String usuario
    );

    @Operation(summary = "Filtrar auditoría por tipo de operación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros filtrados exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuditoriaDtoResponse.class))))
    })
    List<AuditoriaDtoResponse> buscarPorTipo(
            @Parameter(description = "Tipo de operación: INSERT, UPDATE o DELETE", required = true) TipoOperacion tipoOperacion
    );

    @Operation(summary = "Filtrar auditoría por entidad afectada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros de la entidad obtenidos exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuditoriaDtoResponse.class))))
    })
    List<AuditoriaDtoResponse> buscarPorEntidad(
            @Parameter(description = "Nombre de la entidad afectada", required = true, example = "Producto") String entidad
    );
}
