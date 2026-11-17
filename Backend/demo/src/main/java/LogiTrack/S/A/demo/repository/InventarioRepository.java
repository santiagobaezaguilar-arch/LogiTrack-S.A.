package LogiTrack.S.A.demo.repository;

import LogiTrack.S.A.demo.model.Inventario;
import LogiTrack.S.A.demo.model.InventarioId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, InventarioId> {

    // Todos los productos en una bodega específica
    List<Inventario> findByBodegaId(Long bodegaId);

    // Todas las bodegas donde existe un producto
    List<Inventario> findByProductoId(Long productoId);

    // Buscar el registro exacto bodega+producto (usado en movimientos para ajustar stock)
    Optional<Inventario> findByBodegaIdAndProductoId(Long bodegaId, Long productoId);

    // Productos con stock bajo (menos de 10 unidades) en cualquier bodega
    List<Inventario> findByStockLessThan(Integer stock);

    // Verificar si un producto tiene inventario asociado (antes de eliminar producto)
    boolean existsByProductoId(Long productoId);

    // Verificar si una bodega tiene inventario registrado (antes de eliminar bodega)
    boolean existsByBodegaId(Long bodegaId);
}
