package LogiTrack.S.A.demo.controller;

import LogiTrack.S.A.demo.dto.request.ProductoDtoRequest;
import LogiTrack.S.A.demo.dto.response.ProductoDtoResponse;
import LogiTrack.S.A.demo.service.ProductoService;
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

@Tag(name = "Productos", description = "Gestión del catálogo de productos")
@Validated
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // ADMIN: crear, editar, eliminar
    // ADMIN + EMPLEADO: consultar

    @Operation(summary = "Crear producto (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductoDtoResponse> guardar(@Valid @RequestBody ProductoDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(dto));
    }

    @Operation(summary = "Listar todos los productos (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<ProductoDtoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @Operation(summary = "Buscar producto por ID (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarId(id));
    }

    @Operation(summary = "Filtrar productos por categoría (ADMIN y EMPLEADO)")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/filtroCategoria")
    public ResponseEntity<List<ProductoDtoResponse>> buscarCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(productoService.buscarCategoria(categoria));
    }

    @Operation(summary = "Actualizar producto (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse> actualizar(
            @Valid @RequestBody ProductoDtoRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(productoService.actualizar(dto, id));
    }

    @Operation(summary = "Eliminar producto (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
