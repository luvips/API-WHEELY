package com.wheely.model;

import java.time.LocalTime;

/**
 * Modelo de datos para los periodos temporales del sistema Wheely.
 *
 * <p>Define los diferentes segmentos del día utilizados para análisis de tráfico
 * y cálculo de tiempos promedio. Cada periodo tiene horarios específicos que
 * permiten categorizar los viajes y optimizar las predicciones de tiempo.</p>
 *
 * <p>Periodos típicos del sistema:</p>
 * <ul>
 * <li>Mañana: 06:00 - 12:00 (hora pico matutina)</li>
 * <li>Tarde: 12:00 - 18:00 (tráfico moderado)</li>
 * <li>Noche: 18:00 - 06:00 (hora pico vespertina + nocturno)</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see TiempoRutaPeriodo
 */
public class Periodo {
    private int idPeriodo;
    private String nombrePeriodo;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String descripcion;
    private String nombre;

    /**
     * Constructor vacío requerido para Jackson y frameworks ORM.
     * Inicializa una instancia sin valores predefinidos.
     */
    public Periodo() {}

    /**
     * Constructor completo para crear periodos con todos los datos.
     *
     * @param idPeriodo ID único del periodo (generado por BD)
     * @param nombrePeriodo Nombre descriptivo del periodo
     * @param horaInicio Hora de inicio del periodo en formato LocalTime
     * @param horaFin Hora de finalización del periodo
     * @param descripcion Descripción detallada del periodo y características
     */
    public Periodo(int idPeriodo, String nombrePeriodo, LocalTime horaInicio,
                   LocalTime horaFin, String descripcion) {
        this.idPeriodo = idPeriodo;
        this.nombrePeriodo = nombrePeriodo;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.descripcion = descripcion;
    }

    /**
     * Constructor para crear nuevos periodos sin ID asignado.
     * Utilizado al insertar nuevos registros en la base de datos.
     *
     * @param nombrePeriodo Nombre único del periodo (ej: "Mañana", "Tarde")
     * @param horaInicio Hora de inicio en formato HH:mm
     * @param horaFin Hora de finalización en formato HH:mm
     * @param descripcion Descripción detallada de las características del periodo
     */
    public Periodo(String nombrePeriodo, LocalTime horaInicio, LocalTime horaFin, String descripcion) {
        this.nombrePeriodo = nombrePeriodo;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el identificador único del periodo.
     *
     * @return ID único generado por la base de datos
     */
    public int getIdPeriodo() {
        return idPeriodo;
    }

    /**
     * Establece el identificador único del periodo.
     *
     * @param idPeriodo ID único, típicamente asignado por la base de datos
     */
    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    /**
     * Obtiene el nombre descriptivo del periodo.
     *
     * @return Nombre del periodo (ej: "Mañana", "Tarde", "Noche")
     */
    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    /**
     * Establece el nombre descriptivo del periodo.
     *
     * @param nombrePeriodo Nombre único e identificativo del periodo temporal
     */
    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    /**
     * Obtiene la hora de inicio del periodo.
     *
     * @return Hora de inicio en formato LocalTime (HH:mm)
     */
    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    /**
     * Establece la hora de inicio del periodo.
     *
     * @param horaInicio Hora de inicio válida en formato LocalTime
     */
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    /**
     * Obtiene la hora de finalización del periodo.
     *
     * @return Hora de fin en formato LocalTime (HH:mm)
     */
    public LocalTime getHoraFin() {
        return horaFin;
    }

    /**
     * Establece la hora de finalización del periodo.
     *
     * @param horaFin Hora de fin válida, debe ser posterior a horaInicio
     */
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * Obtiene la descripción detallada del periodo.
     *
     * @return Descripción explicativa de las características del periodo
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción detallada del periodo.
     *
     * @param descripcion Texto descriptivo de las características del periodo
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Representación en cadena del periodo temporal.
     * Incluye todos los atributos principales para debugging y logging.
     *
     * @return Cadena con formato "Periodo{idPeriodo=X, nombrePeriodo='Y', ...}"
     */
    @Override
    public String toString() {
        return "Periodo{" +
                "idPeriodo=" + idPeriodo +
                ", nombrePeriodo='" + nombrePeriodo + '\'' +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    public String getNombre() {
        return nombre;
    }
}