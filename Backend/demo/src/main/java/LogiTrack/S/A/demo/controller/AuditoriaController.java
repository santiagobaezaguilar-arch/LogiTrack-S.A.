package LogiTrack.S.A.demo.controller;

import LogiTrack.S.A.demo.dto.response.AuditoriaDtoResponse;
import LogiTrack.S.A.demo.model.TipoOperacion;
import LogiTrack.S.A.demo.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Auditoría", description = "Historial de cambios del sistema — solo ADMIN")
@Validated
@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    /*
     * Las auditorías son solo lectura desde la API.
     * Se registran automáticamente desde los servicios.
     * Solo el ADMIN puede consultarlas.
     */

    @Operation(summary = "Listar toda la auditoría (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AuditoriaDtoResponse>> listar() {
        return ResponseEntity.ok(auditoriaService.listar());
    }

    @Operation(summary = "Filtrar auditoría por usuario (ADMIN)",
            description = "Pasa el correo del usuario para ver todos sus cambios")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuario")
    public ResponseEntity<List<AuditoriaDtoResponse>> buscarPorUsuario(@RequestParam String usuario) {
        return ResponseEntity.ok(auditoriaService.buscarPorUsuario(usuario));
    }

    @Operation(summary = "Filtrar auditoría por tipo de operación (ADMIN)",
            description = "Valores posibles: INSERT, UPDATE, DELETE")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tipo")
    public ResponseEntity<List<AuditoriaDtoResponse>> buscarPorTipo(@RequestParam TipoOperacion tipoOperacion) {
        return ResponseEntity.ok(auditoriaService.buscarPorTipo(tipoOperacion));
    }

    @Operation(summary = "Filtrar auditoría por entidad afectada (ADMIN)",
            description = "Ejemplos: Producto, Bodega, Movimiento, Usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/entidad")
    public ResponseEntity<List<AuditoriaDtoResponse>> buscarPorEntidad(@RequestParam String entidad) {
        return ResponseEntity.ok(auditoriaService.buscarPorEntidad(entidad));
    }
}
