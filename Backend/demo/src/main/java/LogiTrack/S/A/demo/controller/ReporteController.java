package LogiTrack.S.A.demo.controller;

import LogiTrack.S.A.demo.model.TipoMovimiento;
import LogiTrack.S.A.demo.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {
    private final MovimientoRepository movimientoRepository;

    public record ReporteMovimientosJSON(
            long totalMovimientos,
            Map<String, Long> porTipo
    ){}

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/movimientos")
    public ResponseEntity<ReporteMovimientosJSON> reporteMovimientos (){
        long totalMovimientos = movimientoRepository.count();

        Map<String, Long> porTipo = new LinkedHashMap<>();
        porTipo.put("ENTRADA", movimientoRepository.countByTipoMovimiento(TipoMovimiento.ENTRADA));
        porTipo.put("SALIDA", movimientoRepository.countByTipoMovimiento(TipoMovimiento.SALIDA));
        porTipo.put ("TRANSFERENCIA", movimientoRepository.countByTipoMovimiento(TipoMovimiento.TRANSFERENCIA));

        return ResponseEntity.ok(new ReporteMovimientosJSON(totalMovimientos, porTipo));
    }

}
