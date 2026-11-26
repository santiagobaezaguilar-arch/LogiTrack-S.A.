package LogiTrack.S.A.demo.service.impl;

import LogiTrack.S.A.demo.dto.request.MovimientoDtoRequest;
import LogiTrack.S.A.demo.dto.response.BodegaDtoResponse;
import LogiTrack.S.A.demo.dto.response.MovimientoDtoResponse;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import LogiTrack.S.A.demo.mapper.BodegaMapper;
import LogiTrack.S.A.demo.mapper.MovimientoMapper;
import LogiTrack.S.A.demo.mapper.UsuarioMapper;
import LogiTrack.S.A.demo.model.*;
import LogiTrack.S.A.demo.repository.BodegaRepository;
import LogiTrack.S.A.demo.repository.MovimientoRepository;
import LogiTrack.S.A.demo.repository.UsuarioRepository;
import LogiTrack.S.A.demo.exception.BusinessRuleException;
import LogiTrack.S.A.demo.service.AuditoriaService;
import LogiTrack.S.A.demo.service.MovimientoService;
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

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Movimientos", description = "Registro y consulta de movimientos de inventario entre bodegas")
@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final AuditoriaService auditoriaService;

    @Operation(summary = "Registrar un nuevo movimiento de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Reglas de negocio violadas", content = @Content)
    })
    @Override
    public MovimientoDtoResponse guardar(
            @Parameter(description = "Datos del movimiento", required = true) MovimientoDtoRequest dto,
            @Parameter(description = "Correo del usuario logueado", required = true) String correoUsuario
    ) {
        // Resuelve el usuario desde el correo (viene del JWT en el controlador)
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correoUsuario));

        // Valida y resuelve bodegas según el tipo de movimiento
        Bodega bodegaOrigen = resolverBodegaOrigen(dto);
        Bodega bodegaDestino = resolverBodegaDestino(dto);

        Movimiento movimiento = movimientoMapper.buildEntity(
                dto.tipoMovimiento(), usuario, bodegaOrigen, bodegaDestino
        );
        movimientoRepository.save(movimiento);

        auditoriaService.registrar(TipoOperacion.INSERT, "Movimiento", movimiento.getId(),
                correoUsuario, "Movimiento " + dto.tipoMovimiento() + " registrado");

        return buildResponse(movimiento, usuario, bodegaOrigen, bodegaDestino);
    }

    @Operation(summary = "Listar todos los movimientos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovimientoDtoResponse.class))))
    })
    @Override
    public List<MovimientoDtoResponse> listar() {
        return movimientoRepository.findAll().stream().map(m ->
                buildResponse(m, m.getUsuario(), m.getBodegaOrigen(), m.getBodegaDestino())
        ).toList();
    }

    @Operation(summary = "Buscar movimiento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content)
    })
    @Override
    public MovimientoDtoResponse buscarId(
            @Parameter(description = "ID del movimiento a buscar", required = true, example = "1") Long id
    ) {
        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + id));
        return buildResponse(m, m.getUsuario(), m.getBodegaOrigen(), m.getBodegaDestino());
    }

    @Operation(summary = "Filtrar movimientos por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos filtrados exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovimientoDtoResponse.class))))
    })
    @Override
    public List<MovimientoDtoResponse> buscarPorTipo(
            @Parameter(description = "Tipo de movimiento", required = true) TipoMovimiento tipo
    ) {
        return movimientoRepository.findByTipoMovimiento(tipo).stream().map(m ->
                buildResponse(m, m.getUsuario(), m.getBodegaOrigen(), m.getBodegaDestino())
        ).toList();
    }

    @Operation(summary = "Filtrar movimientos por rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos en el rango indicado",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovimientoDtoResponse.class))))
    })
    @Override
    public List<MovimientoDtoResponse> buscarPorFechas(
            @Parameter(description = "Fecha inicio", required = true) LocalDateTime desde,
            @Parameter(description = "Fecha fin", required = true) LocalDateTime hasta
    ) {
        return movimientoRepository.findByFechaBetween(desde, hasta).stream().map(m ->
                buildResponse(m, m.getUsuario(), m.getBodegaOrigen(), m.getBodegaDestino())
        ).toList();
    }

    @Override
    public List<MovimientoDtoResponse> listarRecientes() {
        return List.of();
    }

    // ── Métodos auxiliares ───────────────────────────────────────────────────

    /*
     * Construye el MovimientoDtoResponse ensamblando sub-DTOs.
     * Las bodegas pueden ser null — el mapper las acepta así.
     */
    private MovimientoDtoResponse buildResponse(Movimiento movimiento, Usuario usuario,
                                                  Bodega bodegaOrigen, Bodega bodegaDestino) {
        UsuarioDtoResponse usuarioDto = usuarioMapper.entityToDto(usuario);
        BodegaDtoResponse origenDto = bodegaOrigen != null
                ? bodegaMapper.entityToDto(bodegaOrigen, usuarioMapper.entityToDto(bodegaOrigen.getEncargado()))
                : null;
        BodegaDtoResponse destinoDto = bodegaDestino != null
                ? bodegaMapper.entityToDto(bodegaDestino, usuarioMapper.entityToDto(bodegaDestino.getEncargado()))
                : null;
        return movimientoMapper.entityToDto(movimiento, usuarioDto, origenDto, destinoDto);
    }

    private Bodega resolverBodegaOrigen(MovimientoDtoRequest dto) {
        if (dto.tipoMovimiento() == TipoMovimiento.ENTRADA) return null;
        if (dto.bodegaOrigenId() == null)
            throw new BusinessRuleException("La bodega origen es obligatoria para movimientos de tipo " + dto.tipoMovimiento());
        return bodegaRepository.findById(dto.bodegaOrigenId())
                .orElseThrow(() -> new RuntimeException("Bodega origen no encontrada con ID: " + dto.bodegaOrigenId()));
    }

    private Bodega resolverBodegaDestino(MovimientoDtoRequest dto) {
        if (dto.tipoMovimiento() == TipoMovimiento.SALIDA) return null;
        if (dto.bodegaDestinoId() == null)
            throw new BusinessRuleException("La bodega destino es obligatoria para movimientos de tipo " + dto.tipoMovimiento());
        return bodegaRepository.findById(dto.bodegaDestinoId())
                .orElseThrow(() -> new RuntimeException("Bodega destino no encontrada con ID: " + dto.bodegaDestinoId()));
    }
}
