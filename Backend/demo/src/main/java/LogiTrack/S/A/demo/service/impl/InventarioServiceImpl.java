package LogiTrack.S.A.demo.service.impl;

import LogiTrack.S.A.demo.dto.request.InventarioDtoRequest;
import LogiTrack.S.A.demo.dto.response.BodegaDtoResponse;
import LogiTrack.S.A.demo.dto.response.InventarioDtoResponse;
import LogiTrack.S.A.demo.dto.response.ProductoDtoResponse;
import LogiTrack.S.A.demo.mapper.BodegaMapper;
import LogiTrack.S.A.demo.mapper.InventarioMapper;
import LogiTrack.S.A.demo.mapper.ProductoMapper;
import LogiTrack.S.A.demo.mapper.UsuarioMapper;
import LogiTrack.S.A.demo.model.Bodega;
import LogiTrack.S.A.demo.model.Inventario;
import LogiTrack.S.A.demo.model.Producto;
import LogiTrack.S.A.demo.model.TipoOperacion;
import LogiTrack.S.A.demo.repository.BodegaRepository;
import LogiTrack.S.A.demo.repository.InventarioRepository;
import LogiTrack.S.A.demo.repository.ProductoRepository;
import LogiTrack.S.A.demo.exception.BusinessRuleException;
import LogiTrack.S.A.demo.service.AuditoriaService;
import LogiTrack.S.A.demo.service.InventarioService;
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

@Tag(name = "Inventario", description = "Gestión del stock de productos por bodega")
@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final InventarioMapper inventarioMapper;
    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final UsuarioMapper usuarioMapper;
    private final AuditoriaService auditoriaService;

    @Operation(summary = "Registrar stock inicial de un producto en una bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Combinación bodega+producto ya existe", content = @Content)
    })
    @Override
    public InventarioDtoResponse guardar(
            @Parameter(description = "Datos del inventario a registrar", required = true)
            InventarioDtoRequest dto
    ) {
        if (inventarioRepository.findByBodegaIdAndProductoId(dto.bodegaId(), dto.productoId()).isPresent()) {
            throw new BusinessRuleException("Ya existe un registro de inventario para esa bodega y producto");
        }

        Bodega bodega = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada con ID: " + dto.bodegaId()));
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.productoId()));

        Inventario inventario = inventarioMapper.buildEntity(bodega, producto, dto.stock());
        inventarioRepository.save(inventario);

        auditoriaService.registrar(TipoOperacion.INSERT, "Inventario", null,
                "sistema", "Stock inicial registrado: " + dto.stock() + " unidades de producto "
                        + dto.productoId() + " en bodega " + dto.bodegaId());

        return buildResponse(inventario, bodega, producto);
    }

    @Operation(summary = "Actualizar el stock de un producto en una bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado", content = @Content)
    })
    @Override
    public InventarioDtoResponse actualizar(
            @Parameter(description = "Datos actualizados del inventario", required = true)
            InventarioDtoRequest dto
    ) {
        Inventario inventario = inventarioRepository.findByBodegaIdAndProductoId(dto.bodegaId(), dto.productoId())
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para bodega "
                        + dto.bodegaId() + " y producto " + dto.productoId()));

        inventarioMapper.updateStock(inventario, dto.stock());
        inventarioRepository.save(inventario);

        auditoriaService.registrar(TipoOperacion.UPDATE, "Inventario", null,
                "sistema", "Stock actualizado a " + dto.stock() + " unidades de producto "
                        + dto.productoId() + " en bodega " + dto.bodegaId());

        return buildResponse(inventario, inventario.getBodega(), inventario.getProducto());
    }

    @Operation(summary = "Listar todo el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InventarioDtoResponse.class))))
    })
    @Override
    public List<InventarioDtoResponse> listar() {
        return inventarioRepository.findAll().stream().map(inv ->
                buildResponse(inv, inv.getBodega(), inv.getProducto())
        ).toList();
    }

    @Operation(summary = "Listar inventario de una bodega específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario de la bodega obtenido exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InventarioDtoResponse.class))))
    })
    @Override
    public List<InventarioDtoResponse> buscarPorBodega(
            @Parameter(description = "ID de la bodega", required = true, example = "1") Long bodegaId
    ) {
        return inventarioRepository.findByBodegaId(bodegaId).stream().map(inv ->
                buildResponse(inv, inv.getBodega(), inv.getProducto())
        ).toList();
    }

    @Operation(summary = "Listar productos con stock bajo (menos de 10 unidades)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos con stock bajo",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InventarioDtoResponse.class))))
    })
    @Override
    public List<InventarioDtoResponse> stockBajo() {
        return inventarioRepository.findByStockLessThan(10).stream().map(inv ->
                buildResponse(inv, inv.getBodega(), inv.getProducto())
        ).toList();
    }

    /*
     * Método auxiliar: construye el InventarioDtoResponse ensamblando los sub-DTOs.
     * El mismo patrón que usa DetalleVentaServiceImpl en poscampus.
     */
    private InventarioDtoResponse buildResponse(Inventario inventario, Bodega bodega, Producto producto) {
        BodegaDtoResponse bodegaDto = bodegaMapper.entityToDto(bodega, usuarioMapper.entityToDto(bodega.getEncargado()));
        ProductoDtoResponse productoDto = productoMapper.entityToDto(producto);
        return inventarioMapper.entityToDto(inventario, bodegaDto, productoDto);
    }
}
