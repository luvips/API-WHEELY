package com.wheely.model;

/**
 * Modelo de datos para los tipos de reporte del sistema Wheely.
 *
 * <p>Define las categorías disponibles para clasificar los reportes de usuarios.
 * Esta clasificación permite el análisis estadístico de problemas frecuentes
 * y la priorización de respuestas según el tipo de incidencia reportada.</p>
 *
 * <p>Categorías comunes del sistema incluyen:</p>
 * <ul>
 * <li>Retrasos - Problemas de puntualidad en rutas específicas</li>
 * <li>Infraestructura - Daños en paradas, señalización o vías</li>
 * <li>Seguridad - Incidentes de seguridad personal o del vehículo</li>
 * <li>Calidad - Problemas con vehículos, limpieza o servicio</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see Reporte
 */
public class TipoReporte {
    private int idTipoReporte;
    private String nombreTipo;
    private String descripcion;

    /**
     * Constructor vacío requerido para Jackson y frameworks ORM.
     * Inicializa una instancia sin valores predefinidos.
     */
    public TipoReporte() {}

    /**
     * Constructor completo para crear tipos de reporte con todos los datos.
     *
     * @param idTipoReporte ID único del tipo de reporte (generado por BD)
     * @param nombreTipo Nombre único del tipo de reporte
     * @param descripcion Descripción detallada del tipo de problema
     */
    public TipoReporte(int idTipoReporte, String nombreTipo, String descripcion) {
        this.idTipoReporte = idTipoReporte;
        this.nombreTipo = nombreTipo;
        this.descripcion = descripcion;
    }

    /**
     * Constructor para crear nuevos tipos de reporte sin ID asignado.
     * Utilizado al insertar nuevos tipos en la base de datos.
     *
     * @param nombreTipo Nombre único del tipo (ej: "Retraso", "Infraestructura")
     * @param descripcion Descripción detallada del tipo de problema cubierto
     */
    public TipoReporte(String nombreTipo, String descripcion) {
        this.nombreTipo = nombreTipo;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el identificador único del tipo de reporte.
     *
     * @return ID único generado por la base de datos
     */
    public int getIdTipoReporte() {
        return idTipoReporte;
    }

    /**
     * Establece el identificador único del tipo de reporte.
     *
     * @param idTipoReporte ID único, típicamente asignado por la base de datos
     */
    public void setIdTipoReporte(int idTipoReporte) {
        this.idTipoReporte = idTipoReporte;
    }

    /**
     * Obtiene el nombre del tipo de reporte.
     *
     * @return Nombre único identificador del tipo (ej: "Retraso", "Seguridad")
     */
    public String getNombreTipo() {
        return nombreTipo;
    }

    /**
     * Establece el nombre del tipo de reporte.
     *
     * @param nombreTipo Nombre único e identificativo, no puede ser null o vacío
     */
    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    /**
     * Obtiene la descripción detallada del tipo de reporte.
     *
     * @return Descripción explicativa de qué problemas abarca este tipo
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción detallada del tipo de reporte.
     *
     * @param descripcion Texto descriptivo del alcance y características del tipo
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Representación en cadena del tipo de reporte.
     * Incluye todos los atributos principales para debugging y logging.
     *
     * @return Cadena con formato "TipoReporte{idTipoReporte=X, nombreTipo='Y', ...}"
     */
    @Override
    public String toString() {
        return "TipoReporte{" +
                "idTipoReporte=" + idTipoReporte +
                ", nombreTipo='" + nombreTipo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}