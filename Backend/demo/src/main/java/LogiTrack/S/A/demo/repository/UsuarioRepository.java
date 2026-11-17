package LogiTrack.S.A.demo.repository;

import LogiTrack.S.A.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por correo (usado en login para localizar al usuario)
    Optional<Usuario> findByCorreo(String correo);

    // Verificar si ya existe un correo antes de registrar (evitar duplicados)
    boolean existsByCorreo(String correo);
}
