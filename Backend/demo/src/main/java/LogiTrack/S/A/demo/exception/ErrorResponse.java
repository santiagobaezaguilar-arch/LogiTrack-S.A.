package LogiTrack.S.A.demo.exception;

import java.time.LocalDateTime;

/**
 * Estructura estándar de respuesta para todos los errores de la API:
 * {
 *   "timestamp": "2026-11-15T10:30:00",
 *   "status": 400,
 *   "message": "Descripción del error",
 *   "errorCode": "BUSINESS_RULE_VIOLATION"
 * }
 */
public record ErrorResponse(LocalDateTime timestamp, int status, String message, String errorCode) {
}
