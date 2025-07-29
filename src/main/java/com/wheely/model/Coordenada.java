package com.wheely.model;

import java.math.BigDecimal;

/**
 * Modelo de datos para representar una coordenada geográfica específica de un recorrido en el sistema Wheely.
 *
 * <p>Esta clase encapsula la información geográfica precisa de un punto específico que forma parte
 * del trazado de un recorrido de transporte público. Cada coordenada representa un punto GPS
 * con latitud y longitud de alta precisión, junto con su orden secuencial dentro del recorrido,
 * permitiendo la reconstrucción completa del trayecto que sigue el vehículo.</p>
 *
 * <p>La clase utiliza <code>BigDecimal</code> para las coordenadas geográficas con el fin de
 * mantener la máxima precisión en los cálculos geográficos y evitar errores de redondeo
 * inherentes a los tipos de datos de punto flotante. Esta precisión es crucial para aplicaciones
 * de mapeo y navegación donde diferencias mínimas pueden afectar significativamente la exactitud
 * de la representación geográfica.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Almacenamiento de coordenadas GPS con precisión decimal alta</li>
 * <li>Mantenimiento del orden secuencial de puntos en el trazado</li>
 * <li>Soporte para reconstrucción de trayectorias completas</li>
 * <li>Base para cálculos geográficos y de distancias</li>
 * <li>Integración con sistemas de mapas y navegación GPS</li>
 * </ul>
 *
 * <p><strong>Relaciones del modelo:</strong></p>
 * <ul>
 * <li>Pertenece a un Recorrido específico (relación N:1)</li>
 * <li>Se relaciona indirectamente con Ruta a través de Recorrido</li>
 * <li>Forma secuencias ordenadas de puntos geográficos</li>
 * </ul>
 *
 * <p><strong>Casos de uso principales:</strong></p>
 * <ul>
 * <li>Visualización de trayectorias en mapas interactivos</li>
 * <li>Cálculo de distancias y tiempos de recorrido</li>
 * <li>Seguimiento en tiempo real de vehículos</li>
 * <li>Análisis de desviaciones de ruta y optimización</li>
 * <li>Generación de archivos GPX o KML para navegación</li>
 * </ul>
 *
 * <p><strong>Consideraciones técnicas:</strong></p>
 * <ul>
 * <li>Coordenadas almacenadas en formato decimal (WGS84)</li>
 * <li>Precisión recomendada: 6-8 decimales para aplicaciones urbanas</li>
 * <li>Orden secuencial crítico para reconstrucción correcta del trazado</li>
 * </ul>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Recorrido
 * @see com.wheely.model.CoordenadaParada
 * @see com.wheely.service.CoordenadaService
 * @see com.wheely.controller.CoordenadaController
 * @see java.math.BigDecimal
 */
public class Coordenada {

    /**
     * Identificador único de la coordenada en la base de datos.
     *
     * <p>Este campo representa la clave primaria auto-generada por MySQL.
     * Se utiliza para referenciar de manera única cada punto geográfico
     * en el sistema, permitiendo operaciones CRUD específicas y
     * mantenimiento de la integridad relacional.</p>
     */
    private int idCoordenada;

    /**
     * Identificador del recorrido al cual pertenece esta coordenada.
     *
     * <p>Este campo establece la relación foreign key con la tabla Recorrido.
     * Permite agrupar múltiples coordenadas bajo un mismo recorrido,
     * facilitando la consulta y reconstrucción del trazado completo
     * del trayecto que sigue el vehículo de transporte.</p>
     */
    private int idRecorrido;

    /**
     * Coordenada de latitud geográfica con precisión decimal alta.
     *
     * <p>Representa la posición geográfica norte-sur del punto en el sistema
     * de coordenadas WGS84. Utiliza <code>BigDecimal</code> para mantener
     * la máxima precisión y evitar errores de redondeo en cálculos geográficos.
     * Los valores típicos van desde -90.0 (polo sur) hasta +90.0 (polo norte).</p>
     *
     * <p><strong>Precisión recomendada:</strong></p>
     * <ul>
     * <li>6 decimales: precisión de aproximadamente 0.11 metros</li>
     * <li>7 decimales: precisión de aproximadamente 0.011 metros</li>
     * <li>8 decimales: precisión de aproximadamente 0.0011 metros</li>
     * </ul>
     */
    private BigDecimal latitud;

    /**
     * Coordenada de longitud geográfica con precisión decimal alta.
     *
     * <p>Representa la posición geográfica este-oeste del punto en el sistema
     * de coordenadas WGS84. Utiliza <code>BigDecimal</code> para mantener
     * la máxima precisión en aplicaciones de navegación y mapeo.
     * Los valores típicos van desde -180.0 (oeste) hasta +180.0 (este).</p>
     *
     * <p><strong>Nota técnica:</strong> La precisión de la longitud varía
     * según la latitud. En el ecuador, 1 grado de longitud equivale a
     * aproximadamente 111 km, mientras que en latitudes altas la distancia
     * se reduce considerablemente.</p>
     */
    private BigDecimal longitud;

    /**
     * Orden secuencial del punto dentro del recorrido.
     *
     * <p>Este campo define la posición ordinal de la coordenada dentro de la
     * secuencia de puntos que componen el trazado del recorrido. Es fundamental
     * para la reconstrucción correcta del trayecto, ya que permite determinar
     * la dirección y el flujo del recorrido del vehículo de transporte.</p>
     *
     * <p>El orden debe ser único dentro de cada recorrido y típicamente
     * comienza desde 1 para el primer punto del trazado.</p>
     */
    private int ordenPunto;

    /**
     * Constructor por defecto requerido para la deserialización JSON.
     *
     * <p>Este constructor vacío es esencial para que Jackson y otros frameworks
     * de serialización puedan crear instancias de la clase durante el proceso
     * de deserialización de objetos JSON enviados desde el frontend o APIs externas.</p>
     *
     * <p>Inicializa una instancia de Coordenada con valores por defecto:
     * idCoordenada = 0, idRecorrido = 0, latitud = null, longitud = null, ordenPunto = 0</p>
     */
    public Coordenada() {}

    /**
     * Constructor completo para crear una instancia de Coordenada con todos los campos.
     *
     * <p>Este constructor es utilizado principalmente cuando se recuperan datos
     * desde la base de datos, donde todos los campos incluyendo el ID ya están
     * definidos. Es ideal para operaciones de consulta y visualización de trazados.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * BigDecimal lat = new BigDecimal("16.7569123");
     * BigDecimal lng = new BigDecimal("-93.1292456");
     * Coordenada coordenadaExistente = new Coordenada(101, 5, lat, lng, 15);
     * System.out.println("Coordenada cargada: " + coordenadaExistente.getLatitud() +
     *                    ", " + coordenadaExistente.getLongitud());
     * </pre>
     *
     * @param idCoordenada Identificador único de la coordenada (debe ser mayor a 0)
     * @param idRecorrido Identificador del recorrido padre (debe ser mayor a 0)
     * @param latitud Coordenada de latitud en formato decimal (rango válido: -90 a +90)
     * @param longitud Coordenada de longitud en formato decimal (rango válido: -180 a +180)
     * @param ordenPunto Orden secuencial dentro del recorrido (debe ser mayor a 0)
     */
    public Coordenada(int idCoordenada, int idRecorrido, BigDecimal latitud, BigDecimal longitud, int ordenPunto) {
        this.idCoordenada = idCoordenada;
        this.idRecorrido = idRecorrido;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ordenPunto = ordenPunto;
    }

    /**
     * Constructor para crear una nueva coordenada sin ID específico (para creación).
     *
     * <p>Este constructor es ideal para crear nuevas coordenadas que serán insertadas
     * en la base de datos, donde el ID será auto-generado por MySQL. Es el
     * constructor más utilizado cuando se importan trazados desde dispositivos GPS
     * o se crean recorridos desde interfaces de mapeo.</p>
     *
     * <p>Establece automáticamente el idCoordenada en 0, que será reemplazado por
     * el valor auto-generado cuando la entidad sea persistida en la base de datos.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * // Crear coordenada para un punto del recorrido
     * BigDecimal latitud = new BigDecimal("16.7569123");
     * BigDecimal longitud = new BigDecimal("-93.1292456");
     * Coordenada nuevaCoordenada = new Coordenada(5, latitud, longitud, 15);
     * // El ID será asignado automáticamente por la base de datos
     * coordenadaService.createCoordenada(nuevaCoordenada);
     * </pre>
     *
     * @param idRecorrido Identificador del recorrido padre (debe ser mayor a 0)
     * @param latitud Coordenada de latitud en formato decimal (rango válido: -90 a +90)
     * @param longitud Coordenada de longitud en formato decimal (rango válido: -180 a +180)
     * @param ordenPunto Orden secuencial dentro del recorrido (debe ser mayor a 0)
     */
    public Coordenada(int idRecorrido, BigDecimal latitud, BigDecimal longitud, int ordenPunto) {
        this.idRecorrido = idRecorrido;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ordenPunto = ordenPunto;
    }

    /**
     * Obtiene el identificador único de la coordenada.
     *
     * <p>Retorna el ID auto-generado por la base de datos que identifica
     * de manera única esta coordenada en el sistema. Este valor es inmutable
     * una vez que la coordenada ha sido persistida en la base de datos.</p>
     *
     * @return Identificador único de la coordenada, 0 si no ha sido persistida
     */
    public int getIdCoordenada() {
        return idCoordenada;
    }

    /**
     * Establece el identificador único de la coordenada.
     *
     * <p>Este método generalmente es utilizado por el framework ORM o
     * durante la deserialización de datos desde JSON. En condiciones
     * normales de uso de la aplicación, este valor no debería modificarse
     * manualmente una vez que la coordenada ha sido creada.</p>
     *
     * <p><strong>Nota de implementación:</strong> Se recomienda usar este método
     * únicamente durante la carga inicial de datos o en procesos de migración.</p>
     *
     * @param idCoordenada Nuevo identificador para la coordenada (debe ser mayor a 0 para coordenadas persistidas)
     */
    public void setIdCoordenada(int idCoordenada) {
        this.idCoordenada = idCoordenada;
    }

    /**
     * Obtiene el identificador del recorrido al cual pertenece la coordenada.
     *
     * <p>Retorna el ID del recorrido padre que contiene esta coordenada.
     * Esta relación permite agrupar múltiples coordenadas para formar
     * el trazado completo de un recorrido de transporte público.</p>
     *
     * @return Identificador del recorrido padre
     */
    public int getIdRecorrido() {
        return idRecorrido;
    }

    /**
     * Establece el identificador del recorrido al cual pertenece la coordenada.
     *
     * <p>Define la relación de pertenencia entre la coordenada y su recorrido padre.
     * Este valor debe corresponder a un ID válido existente en la tabla Recorrido
     * para mantener la integridad referencial de la base de datos.</p>
     *
     * <p><strong>Validación requerida:</strong> El idRecorrido debe existir en la tabla Recorrido.</p>
     *
     * @param idRecorrido Identificador del recorrido padre (debe ser mayor a 0 y existir en la BD)
     */
    public void setIdRecorrido(int idRecorrido) {
        this.idRecorrido = idRecorrido;
    }

    /**
     * Obtiene la coordenada de latitud geográfica.
     *
     * <p>Retorna la posición norte-sur del punto geográfico en formato
     * BigDecimal para mantener la precisión máxima. La latitud se expresa
     * en grados decimales según el sistema de coordenadas WGS84.</p>
     *
     * @return Coordenada de latitud en grados decimales, puede ser null si no ha sido establecida
     */
    public BigDecimal getLatitud() {
        return latitud;
    }

    /**
     * Establece la coordenada de latitud geográfica.
     *
     * <p>Define la posición norte-sur del punto geográfico. El valor debe
     * estar dentro del rango válido de latitudes (-90.0 a +90.0 grados)
     * y se recomienda usar una precisión de 6-8 decimales para aplicaciones
     * de transporte urbano.</p>
     *
     * <p>Ejemplos de valores válidos para Chiapas, México:</p>
     * <ul>
     * <li>Tuxtla Gutiérrez: aproximadamente 16.7569</li>
     * <li>San Cristóbal: aproximadamente 16.7323</li>
     * <li>Tapachula: aproximadamente 14.9067</li>
     * </ul>
     *
     * @param latitud Coordenada de latitud en grados decimales (rango válido: -90.0 a +90.0)
     */
    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    /**
     * Obtiene la coordenada de longitud geográfica.
     *
     * <p>Retorna la posición este-oeste del punto geográfico en formato
     * BigDecimal para mantener la precisión máxima. La longitud se expresa
     * en grados decimales según el sistema de coordenadas WGS84.</p>
     *
     * @return Coordenada de longitud en grados decimales, puede ser null si no ha sido establecida
     */
    public BigDecimal getLongitud() {
        return longitud;
    }

    /**
     * Establece la coordenada de longitud geográfica.
     *
     * <p>Define la posición este-oeste del punto geográfico. El valor debe
     * estar dentro del rango válido de longitudes (-180.0 a +180.0 grados)
     * y se recomienda usar una precisión de 6-8 decimales para aplicaciones
     * de navegación precisas.</p>
     *
     * <p>Ejemplos de valores válidos para Chiapas, México:</p>
     * <ul>
     * <li>Tuxtla Gutiérrez: aproximadamente -93.1292</li>
     * <li>San Cristóbal: aproximadamente -92.6377</li>
     * <li>Tapachula: aproximadamente -92.2605</li>
     * </ul>
     *
     * @param longitud Coordenada de longitud en grados decimales (rango válido: -180.0 a +180.0)
     */
    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    /**
     * Obtiene el orden secuencial del punto dentro del recorrido.
     *
     * <p>Retorna la posición ordinal que ocupa esta coordenada dentro
     * de la secuencia de puntos que conforman el trazado del recorrido.
     * Este valor es fundamental para reconstruir correctamente el trayecto.</p>
     *
     * @return Orden secuencial del punto (generalmente comienza desde 1)
     */
    public int getOrdenPunto() {
        return ordenPunto;
    }

    /**
     * Establece el orden secuencial del punto dentro del recorrido.
     *
     * <p>Define la posición que ocupa esta coordenada en la secuencia
     * ordenada de puntos del trazado. Este valor debe ser único dentro
     * de cada recorrido y típicamente se asigna incrementalmente durante
     * la importación o creación del trazado.</p>
     *
     * <p><strong>Consideraciones importantes:</strong></p>
     * <ul>
     * <li>El orden debe ser único dentro del recorrido</li>
     * <li>Se recomienda comenzar desde 1 para el primer punto</li>
     * <li>La secuencia debe ser continua sin saltos</li>
     * <li>Determina la dirección del flujo del recorrido</li>
     * </ul>
     *
     * @param ordenPunto Orden secuencial del punto (debe ser mayor a 0 y único en el recorrido)
     */
    public void setOrdenPunto(int ordenPunto) {
        this.ordenPunto = ordenPunto;
    }

    /**
     * Genera una representación en cadena de texto de la instancia de Coordenada.
     *
     * <p>Este método es útil para depuración, logging y visualización de datos
     * durante el desarrollo. Proporciona una vista completa de todos los
     * campos de la coordenada en un formato legible y estructurado.</p>
     *
     * <p>Formato de salida:</p>
     * <pre>
     * Coordenada{idCoordenada=101, idRecorrido=5, latitud=16.7569123, longitud=-93.1292456, ordenPunto=15}
     * </pre>
     *
     * @return Representación en cadena de la coordenada con todos sus campos
     */
    @Override
    public String toString() {
        return "Coordenada{" +
                "idCoordenada=" + idCoordenada +
                ", idRecorrido=" + idRecorrido +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", ordenPunto=" + ordenPunto +
                '}';
    }
}