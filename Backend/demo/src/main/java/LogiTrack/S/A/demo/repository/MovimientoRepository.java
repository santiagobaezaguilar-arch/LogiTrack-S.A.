package LogiTrack.S.A.demo.repository;

import LogiTrack.S.A.demo.model.Movimiento;
import LogiTrack.S.A.demo.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Filtrar movimientos por tipo (ENTRADA, SALIDA, TRANSFERENCIA)
    List<Movimiento> findByTipoMovimiento(TipoMovimiento tipoMovimiento);

    // Filtrar por rango de fechas (requerimiento de reportes)
    List<Movimiento> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    // Movimientos realizados por un usuario específico
    List<Movimiento> findByUsuarioId(Long usuarioId);

    // Verificar si una bodega tiene movimientos antes de eliminarla
    boolean existsByBodegaOrigenId(Long bodegaId);
    boolean existsByBodegaDestinoId(Long bodegaId);
}
