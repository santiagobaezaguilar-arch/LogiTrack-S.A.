package LogiTrack.S.A.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(description = "Credenciales de acceso y rol asignado a un usuario del sistema")
@Entity
@Table(name = "credenciales")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @ToString
public class Credencial {

    @Schema(description = "Identificador único de la credencial", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Relación con Usuario.
     * Un usuario tiene exactamente una credencial.
     * Se usa LAZY para no cargar el usuario en cada consulta de credencial.
     */
    @Schema(description = "Usuario al que pertenece esta credencial", requiredMode = Schema.RequiredMode.REQUIRED)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Schema(description = "Contraseña del usuario (almacenada con BCrypt)", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(nullable = false, length = 255)
    private String password;

    /*
     * El rol se guarda como String en la columna usando @Enumerated STRING.
     * Así la base de datos almacena "ADMIN" o "EMPLEADO" legiblemente.
     * Spring Security consulta este campo para verificar los permisos
     * de cada petición — el JWT solo lleva el username (correo).
     */
    @Schema(description = "Rol del usuario en el sistema", example = "EMPLEADO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;
}
