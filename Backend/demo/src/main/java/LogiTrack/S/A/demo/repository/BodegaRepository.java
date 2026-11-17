package LogiTrack.S.A.demo.repository;

import LogiTrack.S.A.demo.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    // Buscar bodegas por encargado (útil para saber qué bodegas administra un usuario)
    List<Bodega> findByEncargadoId(Long encargadoId);

    // Verificar si un usuario es encargado de alguna bodega (antes de eliminar usuario)
    boolean existsByEncargadoId(Long encargadoId);
}
