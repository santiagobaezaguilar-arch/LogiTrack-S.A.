package LogiTrack.S.A.demo.service.impl;

import LogiTrack.S.A.demo.dto.request.DetalleMovimientoDtoRequest;
import LogiTrack.S.A.demo.dto.response.DetalleMovimientoDtoResponse;
import LogiTrack.S.A.demo.dto.response.MovimientoDtoResponse;
import LogiTrack.S.A.demo.dto.response.ProductoDtoResponse;
import LogiTrack.S.A.demo.mapper.BodegaMapper;
import LogiTrack.S.A.demo.mapper.DetalleMovimientoMapper;
import LogiTrack.S.A.demo.mapper.MovimientoMapper;
import LogiTrack.S.A.demo.mapper.ProductoMapper;
import LogiTrack.S.A.demo.mapper.UsuarioMapper;
import LogiTrack.S.A.demo.model.DetalleMovimiento;
import LogiTrack.S.A.demo.model.Movimiento;
import LogiTrack.S.A.demo.model.Producto;
import LogiTrack.S.A.demo.model.TipoOperacion;
import LogiTrack.S.A.demo.repository.DetalleMovimientoRepository;
import LogiTrack.S.A.demo.repository.MovimientoRepository;
import LogiTrack.S.A.demo.repository.ProductoRepository;
import LogiTrack.S.A.demo.exception.BusinessRuleException;
import LogiTrack.S.A.demo.service.AuditoriaService;
import LogiTrack.S.A.demo.service.DetalleMovimientoService;
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

@Tag(name = "Detalles de Movimiento", description = "Gestión de los productos incluidos en cada movimiento de inventario")
@Service
@RequiredArgsConstructor
public class DetalleMovimientoServiceImpl implements DetalleMovimientoService {

    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final DetalleMovimientoMapper detalleMovimientoMapper;
    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final UsuarioMapper usuarioMapper;
    private final BodegaMapper bodegaMapper;
    private final AuditoriaService auditoriaService;

    @Operation(summary = "Registrar un detalle de movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "IDs no existen", content = @Content)
    })
    @Override
    public DetalleMovimientoDtoResponse guardar(
            @Parameter(description = "Datos del detalle a registrar", required = true)
            DetalleMovimientoDtoRequest dto
    ) {
        Movimiento movimiento = movimientoRepository.findById(dto.movimientoId())
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + dto.movimientoId()));
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.productoId()));

        DetalleMovimiento detalle = detalleMovimientoMapper.buildEntity(movimiento, producto, dto.cantidad());
        detalleMovimientoRepository.save(detalle);

        auditoriaService.registrar(TipoOperacion.INSERT, "DetalleMovimiento", detalle.getId(),
                "sistema", dto.cantidad() + " unidades de producto " + dto.productoId()
                        + " en movimiento " + dto.movimientoId());

        return buildResponse(detalle, movimiento, producto);
    }

    @Operation(summary = "Actualizar un detalle de movimiento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado", content = @Content)
    })
    @Override
    public DetalleMovimientoDtoResponse actualizar(
            @Parameter(description = "Nuevos datos del detalle", required = true) DetalleMovimientoDtoRequest dto,
            @Parameter(description = "ID del detalle a actualizar", required = true, example = "1") Long id
    ) {
        DetalleMovimiento detalle = detalleMovimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de movimiento no encontrado con ID: " + id));
        Movimiento movimiento = movimientoRepository.findById(dto.movimientoId())
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + dto.movimientoId()));
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.productoId()));

        detalleMovimientoMapper.updateEntityFromFields(detalle, movimiento, producto, dto.cantidad());
        detalleMovimientoRepository.save(detalle);

        auditoriaService.registrar(TipoOperacion.UPDATE, "DetalleMovimiento", id,
                "sistema", "Detalle actualizado: " + dto.cantidad() + " unidades de producto " + dto.productoId());

        return buildResponse(detalle, movimiento, producto);
    }

    @Operation(summary = "Eliminar un detalle de movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado", content = @Content)
    })
    @Override
    public void eliminar(
            @Parameter(description = "ID del detalle a eliminar", required = true, example = "1") Long id
    ) {
        DetalleMovimiento detalle = detalleMovimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de movimiento no encontrado con ID: " + id));

        auditoriaService.registrar(TipoOperacion.DELETE, "DetalleMovimiento", id,
                "sistema", "Detalle eliminado del movimiento " + detalle.getMovimiento().getId());

        detalleMovimientoRepository.delete(detalle);
    }

    @Operation(summary = "Listar todos los detalles de movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))))
    })
    @Override
    public List<DetalleMovimientoDtoResponse> listar() {
        return detalleMovimientoRepository.findAll().stream().map(d ->
                buildResponse(d, d.getMovimiento(), d.getProducto())
        ).toList();
    }

    @Operation(summary = "Buscar detalle por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado", content = @Content)
    })
    @Override
    public DetalleMovimientoDtoResponse buscarId(
            @Parameter(description = "ID del detalle a buscar", required = true, example = "1") Long id
    ) {
        DetalleMovimiento d = detalleMovimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de movimiento no encontrado con ID: " + id));
        return buildResponse(d, d.getMovimiento(), d.getProducto());
    }

    @Operation(summary = "Listar detalles de un movimiento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles del movimiento obtenidos exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DetalleMovimientoDtoResponse.class))))
    })
    @Override
    public List<DetalleMovimientoDtoResponse> buscarPorMovimiento(
            @Parameter(description = "ID del movimiento", required = true, example = "1") Long movimientoId
    ) {
        return detalleMovimientoRepository.findByMovimientoId(movimientoId).stream().map(d ->
                buildResponse(d, d.getMovimiento(), d.getProducto())
        ).toList();
    }

    // ── Método auxiliar ──────────────────────────────────────────────────────

    /*
     * Ensambla los sub-DTOs antes de llamar al mapper,
     * exactamente igual que DetalleVentaServiceImpl en poscampus.
     */
    private DetalleMovimientoDtoResponse buildResponse(DetalleMovimiento detalle,
                                                        Movimiento movimiento,
                                                        Producto producto) {
        MovimientoDtoResponse movimientoDto = movimientoMapper.entityToDto(
                movimiento,
                usuarioMapper.entityToDto(movimiento.getUsuario()),
                movimiento.getBodegaOrigen() != null
                        ? bodegaMapper.entityToDto(movimiento.getBodegaOrigen(),
                        usuarioMapper.entityToDto(movimiento.getBodegaOrigen().getEncargado()))
                        : null,
                movimiento.getBodegaDestino() != null
                        ? bodegaMapper.entityToDto(movimiento.getBodegaDestino(),
                        usuarioMapper.entityToDto(movimiento.getBodegaDestino().getEncargado()))
                        : null
        );
        ProductoDtoResponse productoDto = productoMapper.entityToDto(producto);
        return detalleMovimientoMapper.entityToDto(detalle, movimientoDto, productoDto);
    }
}
