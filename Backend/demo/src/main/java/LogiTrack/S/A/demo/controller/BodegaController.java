package LogiTrack.S.A.demo.controller;

import LogiTrack.S.A.demo.dto.request.BodegaDtoRequest;
import LogiTrack.S.A.demo.dto.response.BodegaDtoResponse;
import LogiTrack.S.A.demo.service.BodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bodegas", description = "Gestión de bodegas físicas de LogiTrack")
@Validated
@RestController
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
public class BodegaController {

    private final BodegaService bodegaService;

    // ADMIN: crear, editar, eliminar
    // ADMIN + EMPLEADO: consultar (el empleado necesita ver las bodegas para hacer movimientos)

    @Operation(summary = "Crear bodega (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BodegaDtoResponse> guardar(@Valid @RequestBody BodegaDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.guardar(dto));
    }

    @Operation(summary = "Listar todas las bodegas (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<BodegaDtoResponse>> listar() {
        return ResponseEntity.ok(bodegaService.listar());
    }

    @Operation(summary = "Buscar bodega por ID (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/{id}")
    public ResponseEntity<BodegaDtoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.buscarId(id));
    }

    @Operation(summary = "Actualizar bodega (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BodegaDtoResponse> actualizar(
            @Valid @RequestBody BodegaDtoRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.actualizar(dto, id));
    }

    @Operation(summary = "Eliminar bodega (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bodegaService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
