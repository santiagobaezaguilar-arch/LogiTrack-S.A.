package LogiTrack.S.A.demo.service.impl;

import LogiTrack.S.A.demo.dto.response.AuditoriaDtoResponse;
import LogiTrack.S.A.demo.mapper.AuditoriaMapper;
import LogiTrack.S.A.demo.model.Auditoria;
import LogiTrack.S.A.demo.model.TipoOperacion;
import LogiTrack.S.A.demo.repository.AuditoriaRepository;
import LogiTrack.S.A.demo.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;

    /*
     * Este método es llamado por los otros servicios cada vez que
     * se realiza un INSERT, UPDATE o DELETE en cualquier entidad.
     * No es un endpoint — es uso interno.
     */
    @Override
    public void registrar(TipoOperacion tipoOperacion, String entidadAfectada,
                          Long entidadId, String usuario, String detalles) {
        Auditoria auditoria = auditoriaMapper.buildEntity(
                tipoOperacion, entidadAfectada, entidadId, usuario, detalles
        );
        auditoriaRepository.save(auditoria);
    }

    @Override
    public List<AuditoriaDtoResponse> listar() {
        return auditoriaRepository.findAll()
                .stream()
                .map(auditoriaMapper::entityToDto)
                .toList();
    }

    @Override
    public List<AuditoriaDtoResponse> buscarPorUsuario(String usuario) {
        return auditoriaRepository.findByUsuario(usuario)
                .stream()
                .map(auditoriaMapper::entityToDto)
                .toList();
    }

    @Override
    public List<AuditoriaDtoResponse> buscarPorTipo(TipoOperacion tipoOperacion) {
        return auditoriaRepository.findByTipoOperacion(tipoOperacion)
                .stream()
                .map(auditoriaMapper::entityToDto)
                .toList();
    }

    @Override
    public List<AuditoriaDtoResponse> buscarPorEntidad(String entidad) {
        return auditoriaRepository.findByEntidadAfectada(entidad)
                .stream()
                .map(auditoriaMapper::entityToDto)
                .toList();
    }
}
