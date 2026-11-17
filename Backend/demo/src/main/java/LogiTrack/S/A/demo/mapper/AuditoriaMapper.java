package LogiTrack.S.A.demo.mapper;

import LogiTrack.S.A.demo.dto.response.AuditoriaDtoResponse;
import LogiTrack.S.A.demo.model.Auditoria;
import LogiTrack.S.A.demo.model.TipoOperacion;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditoriaMapper {

    @Schema(description = "Convierte una entidad Auditoria a su AuditoriaDtoResponse")
    public AuditoriaDtoResponse entityToDto(Auditoria auditoria) {
        if (auditoria == null) return null;
        return new AuditoriaDtoResponse(
                auditoria.getId(),
                auditoria.getTipoOperacion(),
                auditoria.getEntidadAfectada(),
                auditoria.getEntidadId(),
                auditoria.getUsuario(),
                auditoria.getDetalles(),
                auditoria.getFechaHora()
        );
    }

    /*
     * El servicio llama a este método para registrar cualquier cambio.
     * El usuario viene del SecurityContext (correo del usuario logueado).
     */
    @Schema(description = "Construye una entidad Auditoria lista para persistir")
    public Auditoria buildEntity(TipoOperacion tipoOperacion, String entidadAfectada,
                                  Long entidadId, String usuario, String detalles) {
        Auditoria auditoria = new Auditoria();
        auditoria.setTipoOperacion(tipoOperacion);
        auditoria.setEntidadAfectada(entidadAfectada);
        auditoria.setEntidadId(entidadId);
        auditoria.setUsuario(usuario);
        auditoria.setDetalles(detalles);
        auditoria.setFechaHora(LocalDateTime.now());
        return auditoria;
    }
}
