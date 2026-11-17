package LogiTrack.S.A.demo.mapper;

import LogiTrack.S.A.demo.dto.request.BodegaDtoRequest;
import LogiTrack.S.A.demo.dto.response.BodegaDtoResponse;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import LogiTrack.S.A.demo.model.Bodega;
import LogiTrack.S.A.demo.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    /*
     * El encargado llega ya mapeado como UsuarioDtoResponse (puede ser null)
     * igual que DetalleVentaMapper recibe los sub-DTOs ya armados en poscampus.
     */
    @Schema(description = "Convierte una entidad Bodega y el UsuarioDtoResponse del encargado a un BodegaDtoResponse")
    public BodegaDtoResponse entityToDto(Bodega bodega, UsuarioDtoResponse encargadoDto) {
        if (bodega == null) return null;
        return new BodegaDtoResponse(
                bodega.getId(),
                bodega.getNombre(),
                bodega.getUbicacion(),
                bodega.getCapacidad(),
                encargadoDto   // puede ser null si la bodega no tiene encargado
        );
    }

    @Schema(description = "Convierte un BodegaDtoRequest y el Usuario encargado (ya resuelto) a la entidad Bodega")
    public Bodega dtoToEntity(BodegaDtoRequest dto, Usuario encargado) {
        if (dto == null) return null;
        Bodega bodega = new Bodega();
        bodega.setNombre(dto.nombre());
        bodega.setUbicacion(dto.ubicacion());
        bodega.setCapacidad(dto.capacidad());
        bodega.setEncargado(encargado);  // puede ser null
        return bodega;
    }

    @Schema(description = "Actualiza los campos de una entidad Bodega a partir de un BodegaDtoRequest y el Usuario encargado resuelto")
    public void updateEntityFromDto(BodegaDtoRequest dto, Bodega bodega, Usuario encargado) {
        if (dto == null || bodega == null) return;
        bodega.setNombre(dto.nombre());
        bodega.setUbicacion(dto.ubicacion());
        bodega.setCapacidad(dto.capacidad());
        bodega.setEncargado(encargado);
    }
}
