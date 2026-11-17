package LogiTrack.S.A.demo.mapper;

import LogiTrack.S.A.demo.dto.request.UsuarioDtoRequest;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import LogiTrack.S.A.demo.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    @Schema(description = "Convierte una entidad Usuario a su UsuarioDtoResponse")
    public UsuarioDtoResponse entityToDto(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioDtoResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getTelefono(),
                usuario.getCorreo()
        );
    }

    @Schema(description = "Convierte un UsuarioDtoRequest a la entidad Usuario")
    public Usuario dtoToEntity(UsuarioDtoRequest dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setTelefono(dto.telefono());
        usuario.setCorreo(dto.correo());
        return usuario;
    }

    @Schema(description = "Actualiza los campos de una entidad Usuario a partir de un UsuarioDtoRequest")
    public void updateEntityFromDto(UsuarioDtoRequest dto, Usuario usuario) {
        if (dto == null || usuario == null) return;
        usuario.setNombre(dto.nombre());
        usuario.setTelefono(dto.telefono());
        usuario.setCorreo(dto.correo());
    }
}
