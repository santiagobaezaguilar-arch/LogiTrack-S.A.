package LogiTrack.S.A.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(description = "Entidad que representa una bodega física de la empresa LogiTrack")
@Entity
@Table(name = "bodega")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @ToString
public class Bodega {

    @Schema(description = "Identificador único de la bodega", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nombre de la bodega", example = "Bodega Norte", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Schema(description = "Ubicación o dirección de la bodega", example = "Calle 15 #34-20, Bogotá", maxLength = 150)
    @Column(length = 150)
    private String ubicacion;

    @Schema(description = "Capacidad máxima de almacenamiento (unidades)", example = "5000")
    @Column
    private Integer capacidad;

    /*
     * Encargado de la bodega. Puede ser null si aún no se asigna.
     * ON DELETE SET NULL está manejado en el SQL — aquí simplemente
     * permitimos que el campo sea nullable.
     */
    @Schema(description = "Usuario encargado de administrar la bodega")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encargado_id")
    private Usuario encargado;
}
