package LogiTrack.S.A.demo.service;

import LogiTrack.S.A.demo.dto.request.UsuarioDtoRequest;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema LogiTrack")
public interface UsuarioService {

    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya registrado", content = @Content)
    })
    UsuarioDtoResponse guardar(
            @Parameter(description = "Datos del usuario a registrar", required = true)
            UsuarioDtoRequest dto
    );

    @Operation(summary = "Actualizar un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    UsuarioDtoResponse actualizar(
            @Parameter(description = "Nuevos datos del usuario", required = true) UsuarioDtoRequest dto,
            @Parameter(description = "ID del usuario a actualizar", required = true, example = "1") Long id
    );

    @Operation(summary = "Eliminar un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El usuario tiene dependencias activas", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    void eliminar(
            @Parameter(description = "ID del usuario a eliminar", required = true, example = "1") Long id
    );

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsuarioDtoResponse.class))))
    })
    List<UsuarioDtoResponse> listar();

    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    UsuarioDtoResponse buscarId(
            @Parameter(description = "ID del usuario a buscar", required = true, example = "1") Long id
    );

    @Operation(summary = "Buscar usuario por correo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    UsuarioDtoResponse buscarCorreo(
            @Parameter(description = "Correo del usuario a buscar", required = true, example = "carlos@logitrack.com") String correo
    );
}
