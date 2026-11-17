package LogiTrack.S.A.demo.mapper;

import LogiTrack.S.A.demo.dto.request.ProductoDtoRequest;
import LogiTrack.S.A.demo.dto.response.ProductoDtoResponse;
import LogiTrack.S.A.demo.model.Producto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    @Schema(description = "Convierte una entidad Producto a su ProductoDtoResponse")
    public ProductoDtoResponse entityToDto(Producto producto) {
        if (producto == null) return null;
        return new ProductoDtoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio()
        );
    }

    @Schema(description = "Convierte un ProductoDtoRequest a la entidad Producto")
    public Producto dtoToEntity(ProductoDtoRequest dto) {
        if (dto == null) return null;
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setCategoria(dto.categoria());
        producto.setPrecio(dto.precio());
        return producto;
    }

    @Schema(description = "Actualiza los campos de una entidad Producto a partir de un ProductoDtoRequest")
    public void updateEntityFromDto(ProductoDtoRequest dto, Producto producto) {
        if (dto == null || producto == null) return;
        producto.setNombre(dto.nombre());
        producto.setCategoria(dto.categoria());
        producto.setPrecio(dto.precio());
    }
}
