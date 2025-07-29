package com.wheely.model;

import java.math.BigDecimal;

/**
 * Modelo de datos para la tabla CoordenadaParada del sistema Wheely.
 *
 * <p>Representa las coordenadas geográficas exactas de las paradas del sistema
 * de transporte público, permitiendo el posicionamiento preciso en mapas y
 * cálculos de distancia para optimización de rutas.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Almacenamiento de coordenadas GPS precisas (latitud/longitud)</li>
 * <li>Mantenimiento del orden secuencial de paradas en una ruta</li>
 * <li>Facilitación de cálculos geoespaciales para el sistema</li>
 * </ul>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see Parada
 * @see Recorrido
 */
public class CoordenadaParada {
    private int idCoordenadaParada;
    private int idParada;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private int ordenParada;

    /**
     * Constructor vacío requerido para Jackson y frameworks ORM.
     * Inicializa una instancia sin valores predefinidos.
     */
    public CoordenadaParada() {}

    /**
     * Constructor completo para crear coordenadas de parada con todos los datos.
     *
     * @param idCoordenadaParada ID único de la coordenada (generado por BD)
     * @param idParada ID de la parada asociada existente en el sistema
     * @param latitud Coordenada latitud en formato decimal (-90.0 a 90.0)
     * @param longitud Coordenada longitud en formato decimal (-180.0 a 180.0)
     * @param ordenParada Posición secuencial en la ruta (1, 2, 3...)
     */
    public CoordenadaParada(int idCoordenadaParada, int idParada, BigDecimal latitud, BigDecimal longitud, int ordenParada) {
        this.idCoordenadaParada = idCoordenadaParada;
        this.idParada = idParada;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ordenParada = ordenParada;
    }

    /**
     * Constructor para nuevas coordenadas sin ID asignado.
     * Utilizado al crear registros que serán insertados en la base de datos.
     *
     * @param idParada ID de la parada existente en el sistema
     * @param latitud Coordenada latitud válida en formato BigDecimal
     * @param longitud Coordenada longitud válida en formato BigDecimal
     * @param ordenParada Orden en secuencia de paradas (debe ser positivo)
     */
    public CoordenadaParada(int idParada, BigDecimal latitud, BigDecimal longitud, int ordenParada) {
        this.idParada = idParada;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ordenParada = ordenParada;
    }

    /**
     * Obtiene el identificador único de la coordenada de parada.
     *
     * @return ID único generado por la base de datos, 0 si no está asignado
     */
    public int getIdCoordenadaParada() {
        return idCoordenadaParada;
    }

    /**
     * Establece el identificador único de la coordenada de parada.
     * Típicamente usado por el ORM al recuperar datos de la base de datos.
     *
     * @param idCoordenadaParada ID único, debe ser positivo para registros persistidos
     */
    public void setIdCoordenadaParada(int idCoordenadaParada) {
        this.idCoordenadaParada = idCoordenadaParada;
    }

    /**
     * Obtiene el ID de la parada asociada a esta coordenada.
     *
     * @return ID de la parada a la que pertenece esta coordenada
     */
    public int getIdParada() {
        return idParada;
    }

    /**
     * Establece el ID de la parada asociada.
     *
     * @param idParada ID de parada existente en el sistema, debe ser positivo
     */
    public void setIdParada(int idParada) {
        this.idParada = idParada;
    }

    /**
     * Obtiene la coordenada de latitud geográfica.
     *
     * @return Latitud en grados decimales, rango válido: -90.0 a 90.0
     */
    public BigDecimal getLatitud() {
        return latitud;
    }

    /**
     * Establece la coordenada de latitud.
     *
     * @param latitud Valor de latitud en grados decimales (BigDecimal para precisión)
     */
    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    /**
     * Obtiene la coordenada de longitud geográfica.
     *
     * @return Longitud en grados decimales, rango válido: -180.0 a 180.0
     */
    public BigDecimal getLongitud() {
        return longitud;
    }

    /**
     * Establece la coordenada de longitud.
     *
     * @param longitud Valor de longitud en grados decimales (BigDecimal para precisión)
     */
    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    /**
     * Obtiene el orden secuencial de esta parada en la ruta.
     *
     * @return Número de orden (1, 2, 3...) que indica la secuencia de paradas
     */
    public int getOrdenParada() {
        return ordenParada;
    }

    /**
     * Establece el orden secuencial de la parada en la ruta.
     *
     * @param ordenParada Número de orden positivo (1=primera parada, 2=segunda, etc.)
     */
    public void setOrdenParada(int ordenParada) {
        this.ordenParada = ordenParada;
    }

    /**
     * Representación en cadena de la coordenada de parada.
     * Incluye todos los atributos principales para debugging y logging.
     *
     * @return Cadena con formato "CoordenadaParada{idCoordenadaParada=X, idParada=Y, ...}"
     */
    @Override
    public String toString() {
        return "CoordenadaParada{" +
                "idCoordenadaParada=" + idCoordenadaParada +
                ", idParada=" + idParada +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", ordenParada=" + ordenParada +
                '}';
    }
}