package LogiTrack.S.A.demo.repository;

import LogiTrack.S.A.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Filtrar por categoría
    List<Producto> findByCategoria(String categoria);

    // Buscar por nombre (búsqueda exacta)
    List<Producto> findByNombre(String nombre);
}
