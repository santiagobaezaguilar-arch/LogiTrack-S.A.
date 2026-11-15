package LogiTrack.S.A.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(description = "Entidad que representa un usuario del sistema LogiTrack")
@Entity
@Table(name = "usuarios")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @ToString
public class Usuario {

    @Schema(description = "Identificador único del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nombre completo del usuario", example = "Carlos Martínez", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Schema(description = "Número de teléfono del usuario", example = "3101234567", maxLength = 20)
    @Column(length = 20)
    private String telefono;

    @Schema(description = "Correo electrónico del usuario (único)", example = "carlos@logitrack.com", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String correo;
}
