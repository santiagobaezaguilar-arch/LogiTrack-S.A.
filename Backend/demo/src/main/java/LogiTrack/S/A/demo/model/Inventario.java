package LogiTrack.S.A.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(description = "Entidad que representa el stock de un producto en una bodega específica")
@Entity
@Table(name = "inventario")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @ToString
public class Inventario {

    /*
     * La tabla inventario tiene PK compuesta (bodega_id, producto_id).
     * Para simplificar el mapeo con JPA y mantener consistencia con
     * el estilo del proyecto (id Long autoincremental), usamos @IdClass
     * representado aquí con EmbeddedId a través de InventarioId.
     *
     * Se mapea directamente la clave compuesta como @EmbeddedId.
     */
    @Schema(description = "Clave compuesta: bodega + producto", requiredMode = Schema.RequiredMode.REQUIRED)
    @EmbeddedId
    private InventarioId id;

    @Schema(description = "Bodega donde se almacena el producto", requiredMode = Schema.RequiredMode.REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bodegaId")
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @Schema(description = "Producto almacenado en la bodega", requiredMode = Schema.RequiredMode.REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Schema(description = "Cantidad en stock de este producto en esta bodega", example = "250", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(nullable = false)
    private Integer stock;
}
