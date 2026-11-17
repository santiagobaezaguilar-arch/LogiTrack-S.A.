package LogiTrack.S.A.demo.service;

import LogiTrack.S.A.demo.dto.request.ProductoDtoRequest;
import LogiTrack.S.A.demo.dto.response.ProductoDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Productos", description = "Gestión del catálogo de productos de LogiTrack")
public interface ProductoService {

    @Operation(summary = "Registrar un nuevo producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    ProductoDtoResponse guardar(
            @Parameter(description = "Datos del producto a registrar", required = true) ProductoDtoRequest dto
    );

    @Operation(summary = "Actualizar un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    ProductoDtoResponse actualizar(
            @Parameter(description = "Nuevos datos del producto", required = true) ProductoDtoRequest dto,
            @Parameter(description = "ID del producto a actualizar", required = true, example = "1") Long id
    );

    @Operation(summary = "Eliminar un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El producto tiene inventario o movimientos asociados", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    void eliminar(
            @Parameter(description = "ID del producto a eliminar", required = true, example = "1") Long id
    );

    @Operation(summary = "Listar todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDtoResponse.class))))
    })
    List<ProductoDtoResponse> listar();

    @Operation(summary = "Buscar producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    ProductoDtoResponse buscarId(
            @Parameter(description = "ID del producto a buscar", required = true, example = "1") Long id
    );

    @Operation(summary = "Filtrar productos por categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDtoResponse.class))))
    })
    List<ProductoDtoResponse> buscarCategoria(
            @Parameter(description = "Categoría a filtrar", required = true, example = "Materiales de construcción") String categoria
    );
}
