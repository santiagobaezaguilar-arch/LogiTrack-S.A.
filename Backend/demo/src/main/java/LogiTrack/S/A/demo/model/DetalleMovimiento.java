package LogiTrack.S.A.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(description = "Entidad que representa el detalle de productos de un movimiento de inventario")
@Entity
@Table(name = "detalle_movimiento")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @ToString
public class DetalleMovimiento {

    @Schema(description = "Identificador único del detalle", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Movimiento al que pertenece este detalle.
     * LAZY igual que en DetalleVenta de poscampus.
     */
    @Schema(description = "Movimiento al que pertenece este detalle", requiredMode = Schema.RequiredMode.REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    private Movimiento movimiento;

    @Schema(description = "Producto involucrado en el movimiento", requiredMode = Schema.RequiredMode.REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Schema(description = "Cantidad de unidades movidas", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(nullable = false)
    private Integer cantidad;
}
