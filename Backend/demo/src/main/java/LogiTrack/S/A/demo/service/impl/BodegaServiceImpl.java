package LogiTrack.S.A.demo.service.impl;

import LogiTrack.S.A.demo.dto.request.BodegaDtoRequest;
import LogiTrack.S.A.demo.dto.response.BodegaDtoResponse;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import LogiTrack.S.A.demo.mapper.BodegaMapper;
import LogiTrack.S.A.demo.mapper.UsuarioMapper;
import LogiTrack.S.A.demo.model.Bodega;
import LogiTrack.S.A.demo.model.TipoOperacion;
import LogiTrack.S.A.demo.model.Usuario;
import LogiTrack.S.A.demo.repository.BodegaRepository;
import LogiTrack.S.A.demo.repository.InventarioRepository;
import LogiTrack.S.A.demo.repository.MovimientoRepository;
import LogiTrack.S.A.demo.repository.UsuarioRepository;
import LogiTrack.S.A.demo.exception.BusinessRuleException;
import LogiTrack.S.A.demo.service.AuditoriaService;
import LogiTrack.S.A.demo.service.BodegaService;
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

@Tag(name = "Bodegas", description = "Gestión de las bodegas físicas de LogiTrack")
@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final InventarioRepository inventarioRepository;
    private final MovimientoRepository movimientoRepository;
    private final AuditoriaService auditoriaService;

    @Operation(summary = "Registrar una nueva bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bodega creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BodegaDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Encargado no existe", content = @Content)
    })
    @Override
    public BodegaDtoResponse guardar(
            @Parameter(description = "Datos de la bodega a registrar", required = true)
            BodegaDtoRequest dto
    ) {
        Usuario encargado = resolverEncargado(dto.encargadoId());
        Bodega bodega = bodegaMapper.dtoToEntity(dto, encargado);
        bodegaRepository.save(bodega);

        auditoriaService.registrar(TipoOperacion.INSERT, "Bodega", bodega.getId(),
                "sistema", "Bodega creada: " + dto.nombre());

        UsuarioDtoResponse encargadoDto = usuarioMapper.entityToDto(encargado);
        return bodegaMapper.entityToDto(bodega, encargadoDto);
    }

    @Operation(summary = "Actualizar una bodega existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BodegaDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    })
    @Override
    public BodegaDtoResponse actualizar(
            @Parameter(description = "Nuevos datos de la bodega", required = true) BodegaDtoRequest dto,
            @Parameter(description = "ID de la bodega a actualizar", required = true, example = "1") Long id
    ) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada con ID: " + id));

        Usuario encargado = resolverEncargado(dto.encargadoId());
        bodegaMapper.updateEntityFromDto(dto, bodega, encargado);
        bodegaRepository.save(bodega);

        auditoriaService.registrar(TipoOperacion.UPDATE, "Bodega", id,
                "sistema", "Bodega actualizada: " + dto.nombre());

        UsuarioDtoResponse encargadoDto = usuarioMapper.entityToDto(encargado);
        return bodegaMapper.entityToDto(bodega, encargadoDto);
    }

    @Operation(summary = "Eliminar una bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bodega eliminada exitosamente"),
            @ApiResponse(responseCode = "400", description = "La bodega tiene inventario o movimientos asociados", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    })
    @Override
    public void eliminar(
            @Parameter(description = "ID de la bodega a eliminar", required = true, example = "1") Long id
    ) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada con ID: " + id));

        if (inventarioRepository.existsByBodegaId(id)) {
            throw new BusinessRuleException("La bodega tiene registros de inventario y no puede eliminarse");
        }
        if (movimientoRepository.existsByBodegaOrigenId(id) || movimientoRepository.existsByBodegaDestinoId(id)) {
            throw new BusinessRuleException("La bodega tiene movimientos de inventario registrados y no puede eliminarse");
        }

        auditoriaService.registrar(TipoOperacion.DELETE, "Bodega", id,
                "sistema", "Bodega eliminada: " + bodega.getNombre());

        bodegaRepository.delete(bodega);
    }

    @Operation(summary = "Listar todas las bodegas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BodegaDtoResponse.class))))
    })
    @Override
    public List<BodegaDtoResponse> listar() {
        return bodegaRepository.findAll().stream().map(b ->
                bodegaMapper.entityToDto(b, usuarioMapper.entityToDto(b.getEncargado()))
        ).toList();
    }

    @Operation(summary = "Buscar bodega por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BodegaDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    })
    @Override
    public BodegaDtoResponse buscarId(
            @Parameter(description = "ID de la bodega a buscar", required = true, example = "1") Long id
    ) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada con ID: " + id));
        return bodegaMapper.entityToDto(bodega, usuarioMapper.entityToDto(bodega.getEncargado()));
    }

    /*
     * Método auxiliar: resuelve el encargado por ID.
     * Si encargadoId es null devuelve null (la bodega puede no tener encargado).
     */
    private Usuario resolverEncargado(Long encargadoId) {
        if (encargadoId == null) return null;
        return usuarioRepository.findById(encargadoId)
                .orElseThrow(() -> new RuntimeException("Encargado no encontrado con ID: " + encargadoId));
    }
}
