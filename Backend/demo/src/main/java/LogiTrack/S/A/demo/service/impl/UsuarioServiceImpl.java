package LogiTrack.S.A.demo.service.impl;

import LogiTrack.S.A.demo.dto.request.UsuarioDtoRequest;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import LogiTrack.S.A.demo.mapper.UsuarioMapper;
import LogiTrack.S.A.demo.model.TipoOperacion;
import LogiTrack.S.A.demo.model.Usuario;
import LogiTrack.S.A.demo.repository.BodegaRepository;
import LogiTrack.S.A.demo.repository.CredencialRepository;
import LogiTrack.S.A.demo.repository.UsuarioRepository;
import LogiTrack.S.A.demo.exception.BusinessRuleException;
import LogiTrack.S.A.demo.service.AuditoriaService;
import LogiTrack.S.A.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema LogiTrack")
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BodegaRepository bodegaRepository;
    private final CredencialRepository credencialRepository;
    private final AuditoriaService auditoriaService;

    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Correo ya registrado o datos inválidos", content = @Content)
    })
    @Override
    public UsuarioDtoResponse guardar(
            @Parameter(description = "Datos del usuario a registrar", required = true)
            UsuarioDtoRequest dto
    ) {
        if (usuarioRepository.existsByCorreo(dto.correo())) {
            throw new BusinessRuleException("El correo ya está registrado en el sistema");
        }
        Usuario usuario = usuarioMapper.dtoToEntity(dto);
        usuarioRepository.save(usuario);

        auditoriaService.registrar(TipoOperacion.INSERT, "Usuario", usuario.getId(),
                dto.correo(), "Nuevo usuario registrado: " + dto.nombre());

        return usuarioMapper.entityToDto(usuario);
    }

    @Operation(summary = "Actualizar un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @Override
    public UsuarioDtoResponse actualizar(
            @Parameter(description = "Nuevos datos del usuario", required = true) UsuarioDtoRequest dto,
            @Parameter(description = "ID del usuario a actualizar", required = true, example = "1") Long id
    ) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuarioMapper.updateEntityFromDto(dto, usuario);
        usuarioRepository.save(usuario);

        auditoriaService.registrar(TipoOperacion.UPDATE, "Usuario", usuario.getId(),
                dto.correo(), "Usuario actualizado: " + dto.nombre());

        return usuarioMapper.entityToDto(usuario);
    }

    @Operation(summary = "Eliminar un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El usuario tiene dependencias activas", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @Override
    public void eliminar(
            @Parameter(description = "ID del usuario a eliminar", required = true, example = "1") Long id
    ) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (bodegaRepository.existsByEncargadoId(id)) {
            throw new BusinessRuleException("El usuario es encargado de una o más bodegas y no puede eliminarse");
        }
        if (credencialRepository.existsByUsuarioId(id)) {
            throw new BusinessRuleException("El usuario tiene credenciales activas. Elimine primero las credenciales");
        }

        auditoriaService.registrar(TipoOperacion.DELETE, "Usuario", id,
                usuario.getCorreo(), "Usuario eliminado: " + usuario.getNombre());

        usuarioRepository.delete(usuario);
    }

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsuarioDtoResponse.class))))
    })
    @Override
    public List<UsuarioDtoResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::entityToDto)
                .toList();
    }

    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @Override
    public UsuarioDtoResponse buscarId(
            @Parameter(description = "ID del usuario a buscar", required = true, example = "1") Long id
    ) {
        return usuarioMapper.entityToDto(
                usuarioRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id))
        );
    }

    @Operation(summary = "Buscar usuario por correo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @Override
    public UsuarioDtoResponse buscarCorreo(
            @Parameter(description = "Correo del usuario a buscar", required = true) String correo
    ) {
        return usuarioMapper.entityToDto(
                usuarioRepository.findByCorreo(correo)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo))
        );
    }
}
