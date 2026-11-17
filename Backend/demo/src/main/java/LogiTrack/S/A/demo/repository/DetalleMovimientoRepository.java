package LogiTrack.S.A.demo.repository;

import LogiTrack.S.A.demo.model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {

    // Todos los detalles de un movimiento (patrón idéntico a findByVentaId en poscampus)
    List<DetalleMovimiento> findByMovimientoId(Long movimientoId);

    // Todos los movimientos en que aparece un producto
    List<DetalleMovimiento> findByProductoId(Long productoId);

    // Verificar si un movimiento tiene detalles (antes de eliminar movimiento)
    boolean existsByMovimientoId(Long movimientoId);

    // Verificar si un producto está en algún detalle (antes de eliminar producto)
    boolean existsByProductoId(Long productoId);
}
