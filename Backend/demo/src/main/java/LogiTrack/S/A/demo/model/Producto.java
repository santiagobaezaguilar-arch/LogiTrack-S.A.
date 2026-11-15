package LogiTrack.S.A.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Schema(description = "Entidad que representa un producto registrado en el sistema")
@Entity
@Table(name = "producto")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @ToString
public class Producto {

    @Schema(description = "Identificador único del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nombre del producto", example = "Cemento Argos 50kg", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Schema(description = "Categoría del producto", example = "Materiales de construcción", maxLength = 50)
    @Column(length = 50)
    private String categoria;

    @Schema(description = "Precio unitario del producto", example = "35000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
}
