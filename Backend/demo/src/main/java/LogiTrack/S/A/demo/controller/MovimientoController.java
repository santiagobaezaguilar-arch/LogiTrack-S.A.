package LogiTrack.S.A.demo.controller;

import LogiTrack.S.A.demo.dto.request.MovimientoDtoRequest;
import LogiTrack.S.A.demo.dto.response.MovimientoDtoResponse;
import LogiTrack.S.A.demo.model.TipoMovimiento;
import LogiTrack.S.A.demo.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Movimientos", description = "Entradas, salidas y transferencias de inventario entre bodegas")
@Validated
@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

        private final MovimientoService movimientoService;

        /*
         * @AuthenticationPrincipal extrae el correo del usuario logueado
         * que el JwtFilter dejó en el SecurityContext.
         * Así el empleado no puede manipular quién figura como responsable del movimiento.
         */

        @Operation(
                summary = "Registrar un movimiento de inventario (ADMIN y EMPLEADO)",
                description = "El empleado registra ENTRADA, SALIDA o TRANSFERENCIA. " +
                        "Para TRANSFERENCIA: se descuenta stock de la bodega origen y se suma a la destino. " +
                        "Para SALIDA: se descuenta de la bodega origen. " +
                        "Para ENTRADA: se suma a la bodega destino. " +
                        "El usuario responsable se toma del token JWT, no del body."
        )
        @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
        @PostMapping
        public ResponseEntity<MovimientoDtoResponse> guardar(
                @Valid @RequestBody MovimientoDtoRequest dto,
                @AuthenticationPrincipal String correoUsuario) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(movimientoService.guardar(dto, correoUsuario));
        }

        @Operation(summary = "Listar todos los movimientos (ADMIN y EMPLEADO)")
        @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
        @GetMapping
        public ResponseEntity<List<MovimientoDtoResponse>> listar() {
            return ResponseEntity.ok(movimientoService.listar());
        }

        @Operation(summary = "Buscar movimiento por ID (ADMIN y EMPLEADO)")
        @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
        @GetMapping("/{id}")
        public ResponseEntity<MovimientoDtoResponse> buscar(@PathVariable Long id) {
            return ResponseEntity.ok(movimientoService.buscarId(id));
        }

        @Operation(summary = "Filtrar movimientos por tipo (ADMIN y EMPLEADO)")
        @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
        @GetMapping("/filtroTipo")
        public ResponseEntity<List<MovimientoDtoResponse>> buscarPorTipo(@RequestParam TipoMovimiento tipo) {
            return ResponseEntity.ok(movimientoService.buscarPorTipo(tipo));
        }

        @Operation(
                summary = "Filtrar movimientos por rango de fechas (ADMIN y EMPLEADO)",
                description = "Formato de fecha: yyyy-MM-dd'T'HH:mm:ss  ejemplo: 2026-11-01T00:00:00"
        )
        @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
        @GetMapping("/filtroFechas")
        public ResponseEntity<List<MovimientoDtoResponse>> buscarPorFechas(
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
            return ResponseEntity.ok(movimientoService.buscarPorFechas(desde, hasta));
        }

        @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
        @GetMapping("/movimientos/recientes")
        public ResponseEntity<List<MovimientoDtoResponse>> listarRecientes(){
            List<MovimientoDtoResponse> recientes = movimientoService.listarRecientes();
            return ResponseEntity.ok(recientes);
        }
}
