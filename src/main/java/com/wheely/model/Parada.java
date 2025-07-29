package com.wheely.model;

/**
 * Modelo de datos para representar una parada de autobús específica en un recorrido del sistema Wheely.
 *
 * <p>Esta clase encapsula la información de una parada individual de transporte público que forma
 * parte de un recorrido específico. Cada parada representa un punto físico donde los usuarios
 * pueden abordar o descender del vehículo de transporte, y está asociada a un archivo GeoJSON
 * que contiene su ubicación geográfica precisa y características específicas.</p>
 *
 * <p>La clase está diseñada para integrarse con sistemas de información geográfica (GIS)
 * mediante archivos GeoJSON que definen la posición exacta de la parada en el mapa.
 * Forma parte del modelo jerárquico: Ruta → Recorrido → Parada, donde múltiples paradas
 * componen un recorrido completo de transporte público.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Almacenamiento de información geográfica de paradas mediante archivos GeoJSON</li>
 * <li>Gestión del estado activo/inactivo de paradas individuales</li>
 * <li>Relación con recorridos padre para organización jerárquica</li>
 * <li>Soporte para serialización JSON en comunicaciones REST</li>
 * <li>Base para sistema de coordenadas detalladas de paradas</li>
 * </ul>
 *
 * <p><strong>Relaciones del modelo:</strong></p>
 * <ul>
 * <li>Pertenece a un Recorrido específico (relación N:1)</li>
 * <li>Puede tener múltiples CoordenadaParada asociadas (relación 1:N)</li>
 * <li>Se relaciona indirectamente con Ruta a través de Recorrido</li>
 * </ul>
 *
 * <p><strong>Casos de uso:</strong></p>
 * <ul>
 * <li>Visualización de paradas en mapas interactivos</li>
 * <li>Búsqueda de paradas cercanas a ubicaciones específicas</li>
 * <li>Gestión operativa de paradas activas/inactivas</li>
 * <li>Planificación de rutas y horarios de transporte</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Recorrido
 * @see com.wheely.model.CoordenadaParada
 * @see com.wheely.service.ParadaService
 * @see com.wheely.controller.ParadaController
 */
public class Parada {

    /**
     * Identificador único de la parada en la base de datos.
     *
     * <p>Este campo representa la clave primaria auto-generada por MySQL.
     * Se utiliza para referenciar de manera única cada parada en el sistema
     * y establecer relaciones con otras entidades como coordenadas de parada
     * y registros de uso.</p>
     */
    private int idParada;

    /**
     * Identificador del recorrido al cual pertenece esta parada.
     *
     * <p>Este campo establece la relación foreign key con la tabla Recorrido.
     * Permite organizar múltiples paradas a lo largo de un recorrido específico,
     * manteniendo la secuencia lógica del trayecto del transporte público.</p>
     */
    private int idRecorrido;

    /**
     * Nombre del archivo GeoJSON que contiene la información geográfica de la parada.
     *
     * <p>Este campo almacena la referencia al archivo que contiene las coordenadas
     * geográficas precisas de la parada, así como metadatos adicionales como nombre,
     * descripción, y características específicas. El archivo GeoJSON debe estar
     * almacenado en el sistema de archivos del servidor y contener un Point o
     * Feature con las coordenadas exactas de la parada.</p>
     *
     * <p>Formato esperado del archivo: "parada_centro_universidad_01.geojson"</p>
     */
    private String nombreArchivoGeojson;

    /**
     * Estado de activación de la parada en el sistema.
     *
     * <p>Indica si la parada está actualmente en operación y disponible
     * para los usuarios. Las paradas inactivas no se muestran en la interfaz
     * de usuario pero se mantienen en la base de datos para fines históricos,
     * mantenimiento temporal, o reactivación posterior.</p>
     *
     * <p>Valores posibles:</p>
     * <ul>
     * <li><code>true</code> - Parada activa y disponible para usuarios</li>
     * <li><code>false</code> - Parada inactiva, en mantenimiento u oculta</li>
     * </ul>
     */
    private boolean activo;

    /**
     * Constructor por defecto requerido para la deserialización JSON.
     *
     * <p>Este constructor vacío es esencial para que Jackson y otros frameworks
     * de serialización puedan crear instancias de la clase durante el proceso
     * de deserialización de objetos JSON enviados desde el frontend o APIs externas.</p>
     *
     * <p>Inicializa una instancia de Parada con valores por defecto:
     * idParada = 0, idRecorrido = 0, nombreArchivoGeojson = null, activo = false</p>
     */
    public Parada() {}

    /**
     * Constructor completo para crear una instancia de Parada con todos los campos.
     *
     * <p>Este constructor es utilizado principalmente cuando se recuperan datos
     * desde la base de datos, donde todos los campos incluyendo el ID ya están
     * definidos. Es ideal para operaciones de consulta y actualización.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Parada paradaExistente = new Parada(15, 3,
     *                                     "parada_plaza_central.geojson",
     *                                     true);
     * System.out.println("Parada cargada: " + paradaExistente.getNombreArchivoGeojson());
     * </pre>
     *
     * @param idParada Identificador único de la parada (debe ser mayor a 0)
     * @param idRecorrido Identificador del recorrido padre (debe ser mayor a 0)
     * @param nombreArchivoGeojson Nombre del archivo GeoJSON (no debe ser null ni vacío)
     * @param activo Estado de activación de la parada
     */
    public Parada(int idParada, int idRecorrido, String nombreArchivoGeojson, boolean activo) {
        this.idParada = idParada;
        this.idRecorrido = idRecorrido;
        this.nombreArchivoGeojson = nombreArchivoGeojson;
        this.activo = activo;
    }

    /**
     * Constructor para crear una nueva parada sin ID específico (para creación).
     *
     * <p>Este constructor es ideal para crear nuevas paradas que serán insertadas
     * en la base de datos, donde el ID será auto-generado por MySQL. Es el
     * constructor más utilizado en operaciones de creación desde el frontend.</p>
     *
     * <p>Establece automáticamente el idParada en 0, que será reemplazado por
     * el valor auto-generado cuando la entidad sea persistida en la base de datos.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Parada nuevaParada = new Parada(3,
     *                                 "parada_terminal_norte.geojson",
     *                                 true);
     * // El ID será asignado automáticamente por la base de datos
     * paradaService.createParada(nuevaParada);
     * </pre>
     *
     * @param idRecorrido Identificador del recorrido padre (debe ser mayor a 0)
     * @param nombreArchivoGeojson Nombre del archivo GeoJSON (no debe ser null ni vacío)
     * @param activo Estado inicial de activación de la parada
     */
    public Parada(int idRecorrido, String nombreArchivoGeojson, boolean activo) {
        this.idRecorrido = idRecorrido;
        this.nombreArchivoGeojson = nombreArchivoGeojson;
        this.activo = activo;
    }

    /**
     * Obtiene el identificador único de la parada.
     *
     * <p>Retorna el ID auto-generado por la base de datos que identifica
     * de manera única esta parada en el sistema. Este valor es inmutable
     * una vez que la parada ha sido persistida en la base de datos.</p>
     *
     * @return Identificador único de la parada, 0 si la parada no ha sido persistida
     */
    public int getIdParada() {
        return idParada;
    }

    /**
     * Establece el identificador único de la parada.
     *
     * <p>Este método generalmente es utilizado por el framework ORM o
     * durante la deserialización de datos desde JSON. En condiciones
     * normales de uso de la aplicación, este valor no debería modificarse
     * manualmente una vez que la parada ha sido creada.</p>
     *
     * <p><strong>Nota de implementación:</strong> Se recomienda usar este método
     * únicamente durante la carga inicial de datos o en procesos de migración.</p>
     *
     * @param idParada Nuevo identificador para la parada (debe ser mayor a 0 para paradas persistidas)
     */
    public void setIdParada(int idParada) {
        this.idParada = idParada;
    }

    /**
     * Obtiene el identificador del recorrido al cual pertenece la parada.
     *
     * <p>Retorna el ID del recorrido padre que contiene esta parada.
     * Esta relación permite organizar múltiples paradas secuencialmente
     * a lo largo de un recorrido de transporte público específico.</p>
     *
     * @return Identificador del recorrido padre
     */
    public int getIdRecorrido() {
        return idRecorrido;
    }

    /**
     * Establece el identificador del recorrido al cual pertenece la parada.
     *
     * <p>Define la relación de pertenencia entre la parada y su recorrido padre.
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
     * Obtiene el nombre del archivo GeoJSON asociado a la parada.
     *
     * <p>Retorna la referencia al archivo que contiene las coordenadas
     * geográficas y metadatos de la parada. Este archivo debe estar
     * disponible en el sistema de archivos del servidor para la
     * visualización correcta en mapas y aplicaciones.</p>
     *
     * @return Nombre del archivo GeoJSON, puede ser null si no ha sido establecido
     */
    public String getNombreArchivoGeojson() {
        return nombreArchivoGeojson;
    }

    /**
     * Establece el nombre del archivo GeoJSON asociado a la parada.
     *
     * <p>Define la referencia al archivo que contiene la información geográfica
     * de la parada. El archivo debe seguir el estándar GeoJSON y contener
     * las coordenadas precisas de la parada así como metadatos descriptivos
     * como nombre, descripción, y servicios disponibles.</p>
     *
     * <p>Recomendaciones para el nombre del archivo:</p>
     * <ul>
     * <li>Usar nombres descriptivos: "parada_centro_comercial_norte.geojson"</li>
     * <li>Incluir ubicación o referencia geográfica</li>
     * <li>Evitar caracteres especiales y espacios</li>
     * <li>Usar extensión .geojson</li>
     * <li>Mantener convenciones de nomenclatura consistentes</li>
     * </ul>
     *
     * @param nombreArchivoGeojson Nombre del archivo GeoJSON (se recomienda que no sea null ni vacío)
     */
    public void setNombreArchivoGeojson(String nombreArchivoGeojson) {
        this.nombreArchivoGeojson = nombreArchivoGeojson;
    }

    /**
     * Verifica si la parada está activa en el sistema.
     *
     * <p>Retorna el estado de activación de la parada. Las paradas
     * activas son visibles para los usuarios y se incluyen en las
     * consultas de recorridos y planificación de viajes.</p>
     *
     * @return <code>true</code> si la parada está activa, <code>false</code> en caso contrario
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado de activación de la parada.
     *
     * <p>Define si la parada está disponible para los usuarios o se
     * encuentra desactivada. Las paradas inactivas se mantienen en
     * la base de datos para fines históricos pero no se muestran en
     * la interfaz de usuario ni en la planificación de rutas.</p>
     *
     * <p>Casos de uso para desactivación:</p>
     * <ul>
     * <li>Paradas temporalmente fuera de servicio por mantenimiento</li>
     * <li>Paradas en construcción o remodelación</li>
     * <li>Paradas estacionales no vigentes</li>
     * <li>Paradas reubicadas pero mantenidas por historial</li>
     * <li>Paradas con restricciones temporales de acceso</li>
     * </ul>
     *
     * @param activo <code>true</code> para activar la parada, <code>false</code> para desactivarla
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Genera una representación en cadena de texto de la instancia de Parada.
     *
     * <p>Este método es útil para depuración, logging y visualización de datos
     * durante el desarrollo. Proporciona una vista completa de todos los
     * campos de la parada en un formato legible y estructurado.</p>
     *
     * <p>Formato de salida:</p>
     * <pre>
     * Parada{idParada=15, idRecorrido=3, nombreArchivoGeojson='parada_plaza_central.geojson', activo=true}
     * </pre>
     *
     * @return Representación en cadena de la parada con todos sus campos
     */
    @Override
    public String toString() {
        return "Parada{" +
                "idParada=" + idParada +
                ", idRecorrido=" + idRecorrido +
                ", nombreArchivoGeojson='" + nombreArchivoGeojson + '\'' +
                ", activo=" + activo +
                '}';
    }
}