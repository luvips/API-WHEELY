package com.wheely.model;

import java.time.LocalDateTime;

/**
 * Modelo de datos para los reportes del sistema Wheely.
 *
 * <p>Representa los reportes generados por usuarios sobre el estado y funcionamiento
 * de las rutas de transporte público. Permite el monitoreo en tiempo real de la
 * calidad del servicio y la identificación de problemas operativos.</p>
 *
 * <p>Tipos de reportes soportados por el sistema:</p>
 * <ul>
 * <li>Reportes de retraso en rutas específicas</li>
 * <li>Problemas de infraestructura en paradas</li>
 * <li>Evaluación de calidad del servicio</li>
 * <li>Incidencias de seguridad y emergencias</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see TipoReporte
 * @see Usuario
 * @see Ruta
 */
public class Reporte {
    private int idReporte;
    private int idRuta;
    private int idTipoReporte;
    private int idUsuario;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaReporte;

    /**
     * Constructor vacío requerido para frameworks de serialización como Jackson.
     * Inicializa una instancia sin valores predefinidos.
     */
    public Reporte() {}

    /**
     * Constructor completo para crear reportes con todos los datos.
     *
     * @param idReporte ID único del reporte (generado por BD)
     * @param idRuta ID de la ruta afectada por el reporte
     * @param idTipoReporte ID del tipo de reporte para categorización
     * @param idUsuario ID del usuario que genera el reporte
     * @param titulo Título descriptivo del problema reportado
     * @param descripcion Descripción detallada del incidente o problema
     * @param fechaReporte Fecha y hora de creación del reporte
     */
    public Reporte(int idReporte, int idRuta, int idTipoReporte, int idUsuario,
                   String titulo, String descripcion, LocalDateTime fechaReporte) {
        this.idReporte = idReporte;
        this.idRuta = idRuta;
        this.idTipoReporte = idTipoReporte;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaReporte = fechaReporte;
    }

    /**
     * Constructor para crear nuevos reportes sin ID ni fecha.
     * La fecha se establece automáticamente al momento de persistir en BD.
     *
     * @param idRuta ID de la ruta afectada, debe existir en el sistema
     * @param idTipoReporte ID del tipo de reporte para categorización
     * @param idUsuario ID del usuario que reporta el problema
     * @param titulo Título descriptivo (máximo 100 caracteres recomendado)
     * @param descripcion Descripción detallada del incidente
     */
    public Reporte(int idRuta, int idTipoReporte, int idUsuario, String titulo, String descripcion) {
        this.idRuta = idRuta;
        this.idTipoReporte = idTipoReporte;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el identificador único del reporte.
     *
     * @return ID único generado por la base de datos
     */
    public int getIdReporte() {
        return idReporte;
    }

    /**
     * Establece el identificador único del reporte.
     *
     * @param idReporte ID único, típicamente asignado por la base de datos
     */
    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    /**
     * Obtiene el ID de la ruta asociada al reporte.
     *
     * @return ID de la ruta del sistema de transporte afectada
     */
    public int getIdRuta() {
        return idRuta;
    }

    /**
     * Establece el ID de la ruta asociada al reporte.
     *
     * @param idRuta ID de ruta existente en el sistema
     */
    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    /**
     * Obtiene el ID del tipo de reporte para categorización.
     *
     * @return ID del tipo de reporte (1=Retraso, 2=Infraestructura, etc.)
     */
    public int getIdTipoReporte() {
        return idTipoReporte;
    }

    /**
     * Establece el ID del tipo de reporte.
     *
     * @param idTipoReporte ID de tipo de reporte existente en el sistema
     */
    public void setIdTipoReporte(int idTipoReporte) {
        this.idTipoReporte = idTipoReporte;
    }

    /**
     * Obtiene el ID del usuario que generó el reporte.
     *
     * @return ID del usuario reportante
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el ID del usuario que genera el reporte.
     *
     * @param idUsuario ID de usuario existente y activo en el sistema
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el título descriptivo del reporte.
     *
     * @return Título que resume el problema reportado
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título descriptivo del reporte.
     *
     * @param titulo Título conciso y descriptivo del problema
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene la descripción detallada del problema reportado.
     *
     * @return Descripción completa del incidente o problema
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción detallada del problema.
     *
     * @param descripcion Texto explicativo detallado del incidente
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la fecha y hora de creación del reporte.
     *
     * @return Timestamp de cuándo se generó el reporte
     */
    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    /**
     * Establece la fecha y hora del reporte.
     * Típicamente usado por la base de datos al insertar nuevos registros.
     *
     * @param fechaReporte Fecha y hora de creación del reporte
     */
    public void setFechaReporte(LocalDateTime fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    /**
     * Representación en cadena del reporte.
     * Incluye todos los atributos principales para debugging y logging.
     *
     * @return Cadena con formato "Reporte{idReporte=X, idRuta=Y, titulo='Z', ...}"
     */
    @Override
    public String toString() {
        return "Reporte{" +
                "idReporte=" + idReporte +
                ", idRuta=" + idRuta +
                ", idTipoReporte=" + idTipoReporte +
                ", idUsuario=" + idUsuario +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaReporte=" + fechaReporte +
                '}';
    }
}