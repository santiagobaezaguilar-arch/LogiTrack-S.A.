package LogiTrack.S.A.demo.model;

/**
 * Tipos de movimiento de inventario posibles en el sistema.
 *
 * - ENTRADA      : ingreso de productos a una bodega destino.
 * - SALIDA       : egreso de productos desde una bodega origen.
 * - TRANSFERENCIA: traslado de productos entre dos bodegas
 *                  (requiere bodega_origen y bodega_destino).
 */
public enum TipoMovimiento {
    ENTRADA,
    SALIDA,
    TRANSFERENCIA
}
