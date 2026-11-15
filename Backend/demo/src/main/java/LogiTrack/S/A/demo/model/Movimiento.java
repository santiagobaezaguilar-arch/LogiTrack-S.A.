package LogiTrack.S.A.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Entidad que representa un movimiento de inventario (entrada, salida o transferencia)")
@Entity
@Table(name = "movimiento")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @ToString
public class Movimiento {

    @Schema(description = "Identificador único del movimiento", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Fecha y hora en que se registró el movimiento", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(nullable = false)
    private LocalDateTime fecha;

    /*
     * Tipo de movimiento almacenado como String en la BD (EnumType.STRING),
     * igual que el rol en Credencial — legible y sin dependencia de orden.
     */
    @Schema(description = "Tipo de movimiento realizado", example = "ENTRADA", requiredMode = Schema.RequiredMode.REQUIRED)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    /*
     * Usuario responsable del movimiento.
     * Se obtiene del SecurityContext (usuario logueado), no del body.
     */
    @Schema(description = "Usuario que registró el movimiento", requiredMode = Schema.RequiredMode.REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /*
     * Bodega origen: null cuando el tipo es ENTRADA (no hay origen).
     */
    @Schema(description = "Bodega de origen (null para ENTRADA)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_origen_id")
    private Bodega bodegaOrigen;

    /*
     * Bodega destino: null cuando el tipo es SALIDA (no hay destino).
     */
    @Schema(description = "Bodega de destino (null para SALIDA)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_destino_id")
    private Bodega bodegaDestino;
}
