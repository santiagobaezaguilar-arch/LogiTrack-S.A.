package LogiTrack.S.A.demo.mapper;

import LogiTrack.S.A.demo.dto.response.BodegaDtoResponse;
import LogiTrack.S.A.demo.dto.response.InventarioDtoResponse;
import LogiTrack.S.A.demo.dto.response.ProductoDtoResponse;
import LogiTrack.S.A.demo.model.Bodega;
import LogiTrack.S.A.demo.model.Inventario;
import LogiTrack.S.A.demo.model.InventarioId;
import LogiTrack.S.A.demo.model.Producto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {

    @Schema(description = "Convierte una entidad Inventario con sus sub-DTOs ya armados a un InventarioDtoResponse")
    public InventarioDtoResponse entityToDto(Inventario inventario,
                                              BodegaDtoResponse bodegaDto,
                                              ProductoDtoResponse productoDto) {
        if (inventario == null || bodegaDto == null || productoDto == null) return null;
        return new InventarioDtoResponse(bodegaDto, productoDto, inventario.getStock());
    }

    @Schema(description = "Construye una entidad Inventario a partir de las entidades Bodega, Producto y el stock inicial")
    public Inventario buildEntity(Bodega bodega, Producto producto, Integer stock) {
        if (bodega == null || producto == null || stock == null) return null;
        Inventario inventario = new Inventario();
        inventario.setId(new InventarioId(bodega.getId(), producto.getId()));
        inventario.setBodega(bodega);
        inventario.setProducto(producto);
        inventario.setStock(stock);
        return inventario;
    }

    @Schema(description = "Actualiza el stock de una entidad Inventario existente")
    public void updateStock(Inventario inventario, Integer nuevoStock) {
        if (inventario == null || nuevoStock == null) return;
        inventario.setStock(nuevoStock);
    }
}
