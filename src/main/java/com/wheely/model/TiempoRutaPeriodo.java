package com.wheely.model;

/**
 * Modelo de datos para la relación tiempo-ruta-periodo del sistema Wheely.
 *
 * <p>Almacena los tiempos promedio de viaje para cada ruta en diferentes periodos
 * del día (mañana, tarde, noche). Permite al sistema calcular ETAs precisos y
 * optimizar horarios según patrones de tráfico históricos.</p>
 *
 * <p>Esta clase es fundamental para:</p>
 * <ul>
 * <li>Registro de tiempos promedio por periodo temporal</li>
 * <li>Análisis de patrones de tráfico en diferentes horarios</li>
 * <li>Base para cálculos de ETA dinámicos y predicciones</li>
 * <li>Optimización de rutas según condiciones históricas</li>
 * </ul>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see Ruta
 * @see Periodo
 */
public class TiempoRutaPeriodo {
    private int idTiempoRutaPeriodo;
    private int idRuta;
    private int idPeriodo;
    private int tiempoPromedio;

    /**
     * Constructor vacío requerido para frameworks de serialización como Jackson.
     * Inicializa una instancia sin valores predefinidos.
     */
    public TiempoRutaPeriodo() {}

    /**
     * Constructor para crear nuevos registros de tiempo ruta-periodo.
     *
     * @param idRuta ID de la ruta existente en el sistema
     * @param idPeriodo ID del periodo temporal (1=Mañana, 2=Tarde, 3=Noche)
     * @param tiempoPromedio Tiempo promedio en minutos (rango válido: 1-300)
     */
    public TiempoRutaPeriodo(int idRuta, int idPeriodo, int tiempoPromedio) {
        this.idRuta = idRuta;
        this.idPeriodo = idPeriodo;
        this.tiempoPromedio = tiempoPromedio;
    }

    /**
     * Constructor completo con ID para registros ya persistidos.
     *
     * @param idTiempoRutaPeriodo ID único del registro (generado por BD)
     * @param idRuta ID de la ruta asociada
     * @param idPeriodo ID del periodo temporal
     * @param tiempoPromedio Tiempo promedio calculado en minutos
     */
    public TiempoRutaPeriodo(int idTiempoRutaPeriodo, int idRuta, int idPeriodo, int tiempoPromedio) {
        this.idTiempoRutaPeriodo = idTiempoRutaPeriodo;
        this.idRuta = idRuta;
        this.idPeriodo = idPeriodo;
        this.tiempoPromedio = tiempoPromedio;
    }

    /**
     * Obtiene el identificador único del registro tiempo-ruta-periodo.
     *
     * @return ID único generado por la base de datos
     */
    public int getIdTiempoRutaPeriodo() {
        return idTiempoRutaPeriodo;
    }

    /**
     * Establece el identificador único del registro.
     *
     * @param idTiempoRutaPeriodo ID único, típicamente asignado por la base de datos
     */
    public void setIdTiempoRutaPeriodo(int idTiempoRutaPeriodo) {
        this.idTiempoRutaPeriodo = idTiempoRutaPeriodo;
    }

    /**
     * Obtiene el ID de la ruta asociada a este tiempo.
     *
     * @return ID de la ruta del sistema de transporte
     */
    public int getIdRuta() {
        return idRuta;
    }

    /**
     * Establece el ID de la ruta asociada.
     *
     * @param idRuta ID de ruta existente en el sistema, debe ser positivo
     */
    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    /**
     * Obtiene el ID del periodo temporal asociado.
     *
     * @return ID del periodo (1=Mañana, 2=Tarde, 3=Noche)
     */
    public int getIdPeriodo() {
        return idPeriodo;
    }

    /**
     * Establece el ID del periodo temporal.
     *
     * @param idPeriodo ID de periodo existente en el sistema
     */
    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    /**
     * Obtiene el tiempo promedio de viaje en minutos.
     *
     * <p>Este tiempo representa el promedio histórico calculado para la ruta
     * específica durante el periodo temporal indicado.</p>
     *
     * @return Tiempo promedio en minutos, rango típico: 5-300 minutos
     */
    public int getTiempoPromedio() {
        return tiempoPromedio;
    }

    /**
     * Establece el tiempo promedio de viaje.
     *
     * @param tiempoPromedio Tiempo en minutos (debe ser positivo y menor a 300)
     */
    public void setTiempoPromedio(int tiempoPromedio) {
        this.tiempoPromedio = tiempoPromedio;
    }

    /**
     * Representación en cadena del tiempo ruta-periodo.
     * Útil para debugging y logging de operaciones del sistema.
     *
     * @return Cadena con formato "TiempoRutaPeriodo{idTiempoRutaPeriodo=X, ...}"
     */
    @Override
    public String toString() {
        return "TiempoRutaPeriodo{" +
                "idTiempoRutaPeriodo=" + idTiempoRutaPeriodo +
                ", idRuta=" + idRuta +
                ", idPeriodo=" + idPeriodo +
                ", tiempoPromedio=" + tiempoPromedio +
                '}';
    }
}