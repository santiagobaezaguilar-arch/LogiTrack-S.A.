package LogiTrack.S.A.demo.mapper;

import LogiTrack.S.A.demo.dto.response.BodegaDtoResponse;
import LogiTrack.S.A.demo.dto.response.MovimientoDtoResponse;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import LogiTrack.S.A.demo.model.Bodega;
import LogiTrack.S.A.demo.model.Movimiento;
import LogiTrack.S.A.demo.model.TipoMovimiento;
import LogiTrack.S.A.demo.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MovimientoMapper {

    /*
     * Los sub-DTOs llegan ya construidos desde el servicio (igual que en poscampus).
     * bodegaOrigenDto y bodegaDestinoDto pueden ser null según el tipo de movimiento.
     */
    @Schema(description = "Convierte una entidad Movimiento con sus sub-DTOs ya armados a un MovimientoDtoResponse")
    public MovimientoDtoResponse entityToDto(Movimiento movimiento,
                                              UsuarioDtoResponse usuarioDto,
                                              BodegaDtoResponse bodegaOrigenDto,
                                              BodegaDtoResponse bodegaDestinoDto) {
        if (movimiento == null || usuarioDto == null) return null;
        return new MovimientoDtoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                usuarioDto,
                bodegaOrigenDto,    // null para ENTRADA
                bodegaDestinoDto    // null para SALIDA
        );
    }

    @Schema(description = "Construye una entidad Movimiento a partir del tipo, usuario y bodegas ya resueltas")
    public Movimiento buildEntity(TipoMovimiento tipo, Usuario usuario,
                                   Bodega bodegaOrigen, Bodega bodegaDestino) {
        if (tipo == null || usuario == null) return null;
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(tipo);
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(bodegaOrigen);    // puede ser null
        movimiento.setBodegaDestino(bodegaDestino);  // puede ser null
        return movimiento;
    }
}
