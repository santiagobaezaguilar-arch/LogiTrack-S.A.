package LogiTrack.S.A.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Clave primaria compuesta de la tabla inventario.
 * JPA requiere que las @EmbeddedId implementen Serializable
 * y que tengan equals() y hashCode() correctos.
 * Lombok @EqualsAndHashCode los genera automáticamente.
 */
@Embeddable
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
public class InventarioId implements Serializable {

    @Column(name = "bodega_id")
    private Long bodegaId;

    @Column(name = "producto_id")
    private Long productoId;
}
