package LogiTrack.S.A.demo.controller;

import LogiTrack.S.A.demo.dto.request.InventarioDtoRequest;
import LogiTrack.S.A.demo.dto.response.InventarioDtoResponse;
import LogiTrack.S.A.demo.service.InventarioService;
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

@Tag(name = "Inventario", description = "Stock de productos por bodega")
@Validated
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @Operation(summary = "Registrar stock inicial de un producto en una bodega (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InventarioDtoResponse> guardar(@Valid @RequestBody InventarioDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.guardar(dto));
    }

    @Operation(summary = "Actualizar stock manualmente (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<InventarioDtoResponse> actualizar(@Valid @RequestBody InventarioDtoRequest dto) {
        return ResponseEntity.ok(inventarioService.actualizar(dto));
    }

    @Operation(summary = "Ver inventario completo global (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<InventarioDtoResponse>> listar() {
        return ResponseEntity.ok(inventarioService.listar());
    }

    @Operation(summary = "Ver inventario de una bodega específica (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<List<InventarioDtoResponse>> buscarPorBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(inventarioService.buscarPorBodega(bodegaId));
    }

    @Operation(summary = "Listar productos con stock bajo — menos de 10 unidades (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InventarioDtoResponse>> stockBajo() {
        return ResponseEntity.ok(inventarioService.stockBajo());
    }
}
