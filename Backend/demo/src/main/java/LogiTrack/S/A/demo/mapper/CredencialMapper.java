package LogiTrack.S.A.demo.mapper;

import LogiTrack.S.A.demo.dto.response.CredencialDtoResponse;
import LogiTrack.S.A.demo.dto.response.UsuarioDtoResponse;
import LogiTrack.S.A.demo.model.Credencial;
import LogiTrack.S.A.demo.model.Rol;
import LogiTrack.S.A.demo.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class CredencialMapper {

    @Schema(description = "Convierte una entidad Credencial y el UsuarioDtoResponse ya mapeado a un CredencialDtoResponse")
    public CredencialDtoResponse entityToDto(Credencial credencial, UsuarioDtoResponse usuarioDto) {
        if (credencial == null || usuarioDto == null) return null;
        // La contraseña NUNCA se expone en la respuesta.
        return new CredencialDtoResponse(
                credencial.getId(),
                usuarioDto,
                credencial.getRol()
        );
    }

    @Schema(description = "Construye una entidad Credencial a partir de un usuario, contraseña ya codificada y rol")
    public Credencial buildEntity(Usuario usuario, String passwordCodificada, Rol rol) {
        if (usuario == null || passwordCodificada == null || rol == null) return null;
        Credencial credencial = new Credencial();
        credencial.setUsuario(usuario);
        credencial.setPassword(passwordCodificada);
        credencial.setRol(rol);
        return credencial;
    }

    @Schema(description = "Actualiza el rol y/o la contraseña de una Credencial existente")
    public void updateEntityFromFields(Credencial credencial, String passwordCodificada, Rol rol) {
        if (credencial == null) return;
        if (passwordCodificada != null) credencial.setPassword(passwordCodificada);
        if (rol != null) credencial.setRol(rol);
    }
}
