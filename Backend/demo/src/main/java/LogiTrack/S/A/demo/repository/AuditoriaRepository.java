package LogiTrack.S.A.demo.repository;

import LogiTrack.S.A.demo.model.Auditoria;
import LogiTrack.S.A.demo.model.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // Filtrar auditorías por usuario (correo o nombre)
    List<Auditoria> findByUsuario(String usuario);

    // Filtrar por tipo de operación (INSERT, UPDATE, DELETE)
    List<Auditoria> findByTipoOperacion(TipoOperacion tipoOperacion);

    // Filtrar por entidad afectada (ej: "Producto", "Bodega")
    List<Auditoria> findByEntidadAfectada(String entidadAfectada);

    // Filtrar por rango de fechas
    List<Auditoria> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);
}
