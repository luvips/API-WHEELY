package com.wheely.model;

/**
 * Modelo de datos para representar el recorrido específico de una ruta en el sistema Wheely.
 *
 * <p>Esta clase encapsula la información geográfica y operacional de un recorrido particular
 * asociado a una ruta de transporte público. Cada recorrido define el trayecto exacto que
 * sigue el vehículo, incluyendo la referencia al archivo GeoJSON que contiene las coordenadas
 * geográficas del trazado y su estado de activación.</p>
 *
 * <p>La clase está diseñada para integrarse con sistemas de información geográfica (GIS)
 * mediante archivos GeoJSON, permitiendo la visualización precisa de rutas en mapas
 * interactivos. Forma parte del modelo de datos jerárquico donde una ruta puede tener
 * múltiples recorridos (ida, vuelta, variantes, etc.).</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Almacenamiento de referencias a archivos GeoJSON con trazados geográficos</li>
 * <li>Gestión del estado activo/inactivo de recorridos</li>
 * <li>Relación con rutas padre para organización jerárquica</li>
 * <li>Soporte para serialización JSON en comunicaciones REST</li>
 * <li>Base para sistema de paradas y coordenadas detalladas</li>
 * </ul>
 *
 * <p><strong>Relaciones del modelo:</strong></p>
 * <ul>
 * <li>Pertenece a una Ruta específica (relación N:1)</li>
 * <li>Puede tener múltiples Paradas asociadas (relación 1:N)</li>
 * <li>Puede tener múltiples Coordenadas de trazado (relación 1:N)</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Ruta
 * @see com.wheely.model.Parada
 * @see com.wheely.service.RecorridoService
 * @see com.wheely.controller.RecorridoController
 */
public class Recorrido {

    /**
     * Identificador único del recorrido en la base de datos.
     *
     * <p>Este campo representa la clave primaria auto-generada por MySQL.
     * Se utiliza para referenciar de manera única cada recorrido en el sistema
     * y establecer relaciones con otras entidades como paradas y coordenadas.</p>
     */
    private int idRecorrido;

    /**
     * Identificador de la ruta padre a la cual pertenece este recorrido.
     *
     * <p>Este campo establece la relación foreign key con la tabla Ruta.
     * Permite organizar múltiples recorridos bajo una misma ruta de transporte,
     * como recorridos de ida, vuelta, o variantes por diferentes horarios.</p>
     */
    private int idRuta;

    /**
     * Nombre del archivo GeoJSON que contiene el trazado geográfico del recorrido.
     *
     * <p>Este campo almacena la referencia al archivo que contiene las coordenadas
     * geográficas precisas del trazado que sigue el vehículo. El archivo GeoJSON
     * debe estar almacenado en el sistema de archivos del servidor y contener
     * un LineString o MultiLineString con las coordenadas del recorrido.</p>
     *
     * <p>Formato esperado del archivo: "recorrido_ruta_1_ida.geojson"</p>
     */
    private String nombreArchivoGeojson;

    /**
     * Estado de activación del recorrido en el sistema.
     *
     * <p>Indica si el recorrido está actualmente en operación y disponible
     * para los usuarios. Los recorridos inactivos no se muestran en la interfaz
     * de usuario pero se mantienen en la base de datos para fines históricos
     * o para reactivación posterior.</p>
     *
     * <p>Valores posibles:</p>
     * <ul>
     * <li><code>true</code> - Recorrido activo y visible para usuarios</li>
     * <li><code>false</code> - Recorrido inactivo u oculto</li>
     * </ul>
     */
    private boolean activo;

    /**
     * Constructor por defecto requerido para la deserialización JSON.
     *
     * <p>Este constructor vacío es esencial para que Jackson y otros frameworks
     * de serialización puedan crear instancias de la clase durante el proceso
     * de deserialización de objetos JSON enviados desde el frontend.</p>
     *
     * <p>Inicializa una instancia de Recorrido con valores por defecto:
     * idRecorrido = 0, idRuta = 0, nombreArchivoGeojson = null, activo = false</p>
     */
    public Recorrido() {}

    /**
     * Constructor completo para crear una instancia de Recorrido con todos los campos.
     *
     * <p>Este constructor es utilizado principalmente cuando se recuperan datos
     * desde la base de datos, donde todos los campos incluyendo el ID ya están
     * definidos. Es ideal para operaciones de consulta y actualización.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Recorrido recorridoExistente = new Recorrido(1, 5,
     *                                              "recorrido_centro_ida.geojson",
     *                                              true);
     * System.out.println("Recorrido cargado: " + recorridoExistente.getNombreArchivoGeojson());
     * </pre>
     *
     * @param idRecorrido Identificador único del recorrido (debe ser mayor a 0)
     * @param idRuta Identificador de la ruta padre (debe ser mayor a 0)
     * @param nombreArchivoGeojson Nombre del archivo GeoJSON (no debe ser null ni vacío)
     * @param activo Estado de activación del recorrido
     */
    public Recorrido(int idRecorrido, int idRuta, String nombreArchivoGeojson, boolean activo) {
        this.idRecorrido = idRecorrido;
        this.idRuta = idRuta;
        this.nombreArchivoGeojson = nombreArchivoGeojson;
        this.activo = activo;
    }

    /**
     * Constructor para crear un nuevo recorrido sin ID específico (para creación).
     *
     * <p>Este constructor es ideal para crear nuevos recorridos que serán insertados
     * en la base de datos, donde el ID será auto-generado por MySQL. Es el
     * constructor más utilizado en operaciones de creación desde el frontend.</p>
     *
     * <p>Establece automáticamente el idRecorrido en 0, que será reemplazado por
     * el valor auto-generado cuando la entidad sea persistida en la base de datos.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Recorrido nuevoRecorrido = new Recorrido(5,
     *                                          "recorrido_universidad_vuelta.geojson",
     *                                          true);
     * // El ID será asignado automáticamente por la base de datos
     * recorridoService.createRecorrido(nuevoRecorrido);
     * </pre>
     *
     * @param idRuta Identificador de la ruta padre (debe ser mayor a 0)
     * @param nombreArchivoGeojson Nombre del archivo GeoJSON (no debe ser null ni vacío)
     * @param activo Estado inicial de activación del recorrido
     */
    public Recorrido(int idRuta, String nombreArchivoGeojson, boolean activo) {
        this.idRuta = idRuta;
        this.nombreArchivoGeojson = nombreArchivoGeojson;
        this.activo = activo;
    }

    /**
     * Obtiene el identificador único del recorrido.
     *
     * <p>Retorna el ID auto-generado por la base de datos que identifica
     * de manera única este recorrido en el sistema. Este valor es inmutable
     * una vez que el recorrido ha sido persistido en la base de datos.</p>
     *
     * @return Identificador único del recorrido, 0 si el recorrido no ha sido persistido
     */
    public int getIdRecorrido() {
        return idRecorrido;
    }

    /**
     * Establece el identificador único del recorrido.
     *
     * <p>Este método generalmente es utilizado por el framework ORM o
     * durante la deserialización de datos desde JSON. En condiciones
     * normales de uso de la aplicación, este valor no debería modificarse
     * manualmente una vez que el recorrido ha sido creado.</p>
     *
     * <p><strong>Nota de implementación:</strong> Se recomienda usar este método
     * únicamente durante la carga inicial de datos o en procesos de migración.</p>
     *
     * @param idRecorrido Nuevo identificador para el recorrido (debe ser mayor a 0 para recorridos persistidos)
     */
    public void setIdRecorrido(int idRecorrido) {
        this.idRecorrido = idRecorrido;
    }

    /**
     * Obtiene el identificador de la ruta padre del recorrido.
     *
     * <p>Retorna el ID de la ruta a la cual pertenece este recorrido.
     * Esta relación permite organizar múltiples recorridos bajo una
     * misma ruta de transporte público.</p>
     *
     * @return Identificador de la ruta padre
     */
    public int getIdRuta() {
        return idRuta;
    }

    /**
     * Establece el identificador de la ruta padre del recorrido.
     *
     * <p>Define la relación de pertenencia entre el recorrido y su ruta padre.
     * Este valor debe corresponder a un ID válido existente en la tabla Ruta
     * para mantener la integridad referencial de la base de datos.</p>
     *
     * <p><strong>Validación requerida:</strong> El idRuta debe existir en la tabla Ruta.</p>
     *
     * @param idRuta Identificador de la ruta padre (debe ser mayor a 0 y existir en la BD)
     */
    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    /**
     * Obtiene el nombre del archivo GeoJSON asociado al recorrido.
     *
     * <p>Retorna la referencia al archivo que contiene las coordenadas
     * geográficas del trazado del recorrido. Este archivo debe estar
     * disponible en el sistema de archivos del servidor para la
     * visualización correcta en mapas.</p>
     *
     * @return Nombre del archivo GeoJSON, puede ser null si no ha sido establecido
     */
    public String getNombreArchivoGeojson() {
        return nombreArchivoGeojson;
    }

    /**
     * Establece el nombre del archivo GeoJSON asociado al recorrido.
     *
     * <p>Define la referencia al archivo que contiene el trazado geográfico
     * del recorrido. El archivo debe seguir el estándar GeoJSON y contener
     * las coordenadas precisas del recorrido para su visualización en mapas.</p>
     *
     * <p>Recomendaciones para el nombre del archivo:</p>
     * <ul>
     * <li>Usar nombres descriptivos: "recorrido_centro_universidad_ida.geojson"</li>
     * <li>Incluir dirección del recorrido: ida, vuelta, circular</li>
     * <li>Evitar caracteres especiales y espacios</li>
     * <li>Usar extensión .geojson</li>
     * </ul>
     *
     * @param nombreArchivoGeojson Nombre del archivo GeoJSON (se recomienda que no sea null ni vacío)
     */
    public void setNombreArchivoGeojson(String nombreArchivoGeojson) {
        this.nombreArchivoGeojson = nombreArchivoGeojson;
    }

    /**
     * Verifica si el recorrido está activo en el sistema.
     *
     * <p>Retorna el estado de activación del recorrido. Los recorridos
     * activos son visibles para los usuarios y se incluyen en las
     * consultas de rutas disponibles.</p>
     *
     * @return <code>true</code> si el recorrido está activo, <code>false</code> en caso contrario
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado de activación del recorrido.
     *
     * <p>Define si el recorrido está disponible para los usuarios o se
     * encuentra desactivado. Los recorridos inactivos se mantienen en
     * la base de datos para fines históricos pero no se muestran en
     * la interfaz de usuario.</p>
     *
     * <p>Casos de uso para desactivación:</p>
     * <ul>
     * <li>Rutas temporalmente fuera de servicio</li>
     * <li>Recorridos en mantenimiento o reparación</li>
     * <li>Variantes estacionales no vigentes</li>
     * <li>Recorridos obsoletos mantenidos por historial</li>
     * </ul>
     *
     * @param activo <code>true</code> para activar el recorrido, <code>false</code> para desactivarlo
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Genera una representación en cadena de texto de la instancia de Recorrido.
     *
     * <p>Este método es útil para depuración, logging y visualización de datos
     * durante el desarrollo. Proporciona una vista completa de todos los
     * campos del recorrido en un formato legible.</p>
     *
     * <p>Formato de salida:</p>
     * <pre>
     * Recorrido{idRecorrido=1, idRuta=5, nombreArchivoGeojson='recorrido_centro_ida.geojson', activo=true}
     * </pre>
     *
     * @return Representación en cadena del recorrido con todos sus campos
     */
    @Override
    public String toString() {
        return "Recorrido{" +
                "idRecorrido=" + idRecorrido +
                ", idRuta=" + idRuta +
                ", nombreArchivoGeojson='" + nombreArchivoGeojson + '\'' +
                ", activo=" + activo +
                '}';
    }
}