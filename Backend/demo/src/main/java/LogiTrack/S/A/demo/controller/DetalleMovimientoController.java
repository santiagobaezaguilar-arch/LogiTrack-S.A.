package LogiTrack.S.A.demo.controller;

import LogiTrack.S.A.demo.dto.request.DetalleMovimientoDtoRequest;
import LogiTrack.S.A.demo.dto.response.DetalleMovimientoDtoResponse;
import LogiTrack.S.A.demo.service.DetalleMovimientoService;
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

@Tag(name = "Detalles de Movimiento", description = "Productos incluidos en cada movimiento de inventario")
@Validated
@RestController
@RequestMapping("/api/detalles-movimiento")
@RequiredArgsConstructor
public class DetalleMovimientoController {

    private final DetalleMovimientoService detalleMovimientoService;

    @Operation(summary = "Registrar detalle de movimiento (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PostMapping
    public ResponseEntity<DetalleMovimientoDtoResponse> guardar(
            @Valid @RequestBody DetalleMovimientoDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleMovimientoService.guardar(dto));
    }

    @Operation(summary = "Listar todos los detalles de movimiento (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<DetalleMovimientoDtoResponse>> listar() {
        return ResponseEntity.ok(detalleMovimientoService.listar());
    }

    @Operation(summary = "Buscar detalle por ID (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/{id}")
    public ResponseEntity<DetalleMovimientoDtoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(detalleMovimientoService.buscarId(id));
    }

    @Operation(summary = "Listar detalles de un movimiento específico (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/movimiento/{movimientoId}")
    public ResponseEntity<List<DetalleMovimientoDtoResponse>> buscarPorMovimiento(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(detalleMovimientoService.buscarPorMovimiento(movimientoId));
    }

    @Operation(summary = "Actualizar detalle de movimiento (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DetalleMovimientoDtoResponse> actualizar(
            @Valid @RequestBody DetalleMovimientoDtoRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(detalleMovimientoService.actualizar(dto, id));
    }

    @Operation(summary = "Eliminar detalle de movimiento (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleMovimientoService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
