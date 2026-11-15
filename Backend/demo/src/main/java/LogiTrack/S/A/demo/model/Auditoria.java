package LogiTrack.S.A.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Registro automático de cada cambio (INSERT, UPDATE, DELETE) realizado en el sistema")
@Entity
@Table(name = "auditoria")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @ToString
public class Auditoria {

    @Schema(description = "Identificador único del registro de auditoría", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Tipo de operación realizada: INSERT, UPDATE o DELETE.
     * Almacenado como String para ser legible en la BD.
     */
    @Schema(description = "Tipo de operación realizada sobre la entidad", example = "UPDATE", requiredMode = Schema.RequiredMode.REQUIRED)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacion", nullable = false, length = 50)
    private TipoOperacion tipoOperacion;

    @Schema(description = "Nombre de la entidad afectada", example = "Producto", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "entidad_afectada", nullable = false, length = 100)
    private String entidadAfectada;

    @Schema(description = "ID del registro afectado dentro de su entidad", example = "5")
    @Column(name = "entidad_id")
    private Long entidadId;

    /*
     * Correo o nombre del usuario que realizó la acción.
     * Se guarda como String simple porque la auditoría debe
     * ser inmutable — aunque el usuario sea eliminado después,
     * el historial se mantiene legible.
     */
    @Schema(description = "Correo o nombre del usuario responsable del cambio", example = "carlos@logitrack.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(nullable = false, length = 100)
    private String usuario;

    @Schema(description = "Descripción del cambio: valores anteriores/nuevos o descripción del evento")
    @Column(columnDefinition = "TEXT")
    private String detalles;

    @Schema(description = "Fecha y hora exacta en que se registró el cambio", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
}
