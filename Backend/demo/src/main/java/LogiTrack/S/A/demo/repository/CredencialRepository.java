package LogiTrack.S.A.demo.repository;

import LogiTrack.S.A.demo.model.Credencial;
import LogiTrack.S.A.demo.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {

    // Buscar credencial por el ID del usuario (para login y validaciones)
    Optional<Credencial> findByUsuarioId(Long usuarioId);

    // Buscar credencial por el correo del usuario (acceso directo desde el correo)
    Optional<Credencial> findByUsuarioCorreo(String correo);

    // Verificar si ya existe credencial para un usuario (evitar duplicados)
    boolean existsByUsuarioId(Long usuarioId);

    // Listar todas las credenciales de un rol específico (útil para reportes de ADMIN)
    List<Credencial> findByRol(Rol rol);
}
