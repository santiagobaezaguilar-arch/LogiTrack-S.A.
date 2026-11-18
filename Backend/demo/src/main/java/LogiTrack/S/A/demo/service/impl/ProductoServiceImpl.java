package LogiTrack.S.A.demo.service.impl;

import LogiTrack.S.A.demo.dto.request.ProductoDtoRequest;
import LogiTrack.S.A.demo.dto.response.ProductoDtoResponse;
import LogiTrack.S.A.demo.mapper.ProductoMapper;
import LogiTrack.S.A.demo.model.Producto;
import LogiTrack.S.A.demo.model.TipoOperacion;
import LogiTrack.S.A.demo.repository.DetalleMovimientoRepository;
import LogiTrack.S.A.demo.repository.InventarioRepository;
import LogiTrack.S.A.demo.repository.ProductoRepository;
import LogiTrack.S.A.demo.exception.BusinessRuleException;
import LogiTrack.S.A.demo.service.AuditoriaService;
import LogiTrack.S.A.demo.service.ProductoService;
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

@Tag(name = "Productos", description = "Gestión del catálogo de productos de LogiTrack")
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final InventarioRepository inventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final AuditoriaService auditoriaService;

    @Operation(summary = "Registrar un nuevo producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @Override
    public ProductoDtoResponse guardar(
            @Parameter(description = "Datos del producto a registrar", required = true)
            ProductoDtoRequest dto
    ) {
        Producto producto = productoMapper.dtoToEntity(dto);
        productoRepository.save(producto);

        auditoriaService.registrar(TipoOperacion.INSERT, "Producto", producto.getId(),
                "sistema", "Producto creado: " + dto.nombre());

        return productoMapper.entityToDto(producto);
    }

    @Operation(summary = "Actualizar un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @Override
    public ProductoDtoResponse actualizar(
            @Parameter(description = "Nuevos datos del producto", required = true) ProductoDtoRequest dto,
            @Parameter(description = "ID del producto a actualizar", required = true, example = "1") Long id
    ) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        productoMapper.updateEntityFromDto(dto, producto);
        productoRepository.save(producto);

        auditoriaService.registrar(TipoOperacion.UPDATE, "Producto", id,
                "sistema", "Producto actualizado: " + dto.nombre());

        return productoMapper.entityToDto(producto);
    }

    @Operation(summary = "Eliminar un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El producto tiene inventario o movimientos asociados", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @Override
    public void eliminar(
            @Parameter(description = "ID del producto a eliminar", required = true, example = "1") Long id
    ) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        if (inventarioRepository.existsByProductoId(id)) {
            throw new BusinessRuleException("El producto tiene registros de inventario en bodegas y no puede eliminarse");
        }
        if (detalleMovimientoRepository.existsByProductoId(id)) {
            throw new BusinessRuleException("El producto está asociado a movimientos de inventario y no puede eliminarse");
        }

        auditoriaService.registrar(TipoOperacion.DELETE, "Producto", id,
                "sistema", "Producto eliminado: " + producto.getNombre());

        productoRepository.delete(producto);
    }

    @Operation(summary = "Listar todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDtoResponse.class))))
    })
    @Override
    public List<ProductoDtoResponse> listar() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    @Operation(summary = "Buscar producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @Override
    public ProductoDtoResponse buscarId(
            @Parameter(description = "ID del producto a buscar", required = true, example = "1") Long id
    ) {
        return productoMapper.entityToDto(
                productoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id))
        );
    }

    @Operation(summary = "Filtrar productos por categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDtoResponse.class))))
    })
    @Override
    public List<ProductoDtoResponse> buscarCategoria(
            @Parameter(description = "Categoría a filtrar", required = true) String categoria
    ) {
        return productoRepository.findByCategoria(categoria)
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }
}
