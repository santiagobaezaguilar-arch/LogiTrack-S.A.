package LogiTrack.S.A.demo.mapper;

import LogiTrack.S.A.demo.dto.response.DetalleMovimientoDtoResponse;
import LogiTrack.S.A.demo.dto.response.MovimientoDtoResponse;
import LogiTrack.S.A.demo.dto.response.ProductoDtoResponse;
import LogiTrack.S.A.demo.model.DetalleMovimiento;
import LogiTrack.S.A.demo.model.Movimiento;
import LogiTrack.S.A.demo.model.Producto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class DetalleMovimientoMapper {

    @Schema(description = "Convierte una entidad DetalleMovimiento con sus sub-DTOs ya armados a un DetalleMovimientoDtoResponse")
    public DetalleMovimientoDtoResponse entityToDto(DetalleMovimiento detalle,
                                                     MovimientoDtoResponse movimientoDto,
                                                     ProductoDtoResponse productoDto) {
        if (detalle == null || movimientoDto == null || productoDto == null) return null;
        return new DetalleMovimientoDtoResponse(
                detalle.getId(),
                movimientoDto,
                productoDto,
                detalle.getCantidad()
        );
    }

    @Schema(description = "Construye una entidad DetalleMovimiento a partir del Movimiento, Producto y cantidad")
    public DetalleMovimiento buildEntity(Movimiento movimiento, Producto producto, Integer cantidad) {
        if (movimiento == null || producto == null || cantidad == null) return null;
        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        return detalle;
    }

    @Schema(description = "Actualiza los campos de un DetalleMovimiento existente")
    public void updateEntityFromFields(DetalleMovimiento detalle, Movimiento movimiento,
                                        Producto producto, Integer cantidad) {
        if (detalle == null || movimiento == null || producto == null || cantidad == null) return;
        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
    }
}
