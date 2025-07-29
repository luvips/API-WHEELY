package com.wheely.model;

/**
 * Modelo de datos para representar una ruta de transporte público en el sistema Wheely.
 *
 * <p>Esta clase encapsula la información fundamental de una ruta de transporte, incluyendo
 * sus puntos de origen y destino, así como un identificador único y nombre descriptivo.
 * Representa una ruta de transporte público y ha sido actualizada para nueva estructura
 * sin tiempo_promedio para optimizar el modelo de datos.</p>
 *
 * <p>La clase está diseñada para ser utilizada en conjunto con el framework Javalin
 * y la librería Jackson para serialización/deserialización JSON, facilitando la
 * comunicación entre el backend y el frontend del sistema.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Almacenamiento de información básica de rutas de transporte</li>
 * <li>Soporte para serialización JSON automática</li>
 * <li>Representación coherente para operaciones CRUD</li>
 * <li>Mapeo directo con la tabla Ruta de la base de datos</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.service.RutaService
 * @see com.wheely.controller.RutaController
 * @see com.wheely.repository.RutaRepository
 */
public class Ruta {

    /**
     * Identificador único de la ruta en la base de datos.
     *
     * <p>Este campo representa la clave primaria auto-generada por MySQL.
     * Se utiliza para referenciar de manera única cada ruta en el sistema
     * y establecer relaciones con otras entidades como reportes y recorridos.</p>
     */
    private int idRuta;

    /**
     * Nombre descriptivo de la ruta de transporte.
     *
     * <p>Campo que contiene el nombre comercial o identificativo de la ruta,
     * como "Ruta Centro-Universidad" o "Línea 1 Norte". Este nombre es
     * utilizado en la interfaz de usuario para mostrar información clara
     * a los usuarios del sistema.</p>
     */
    private String nombreRuta;

    /**
     * Ubicación de origen de la ruta de transporte.
     *
     * <p>Especifica el punto de partida geográfico de la ruta. Puede incluir
     * direcciones completas, nombres de colonias, o referencias geográficas
     * que permitan identificar claramente el inicio del recorrido.</p>
     */
    private String origen;

    /**
     * Ubicación de destino final de la ruta de transporte.
     *
     * <p>Define el punto final del recorrido de la ruta. Al igual que el origen,
     * debe proporcionar información geográfica suficiente para identificar
     * claramente el término del trayecto del transporte público.</p>
     */
    private String destino;

    /**
     * Constructor por defecto requerido para la deserialización JSON.
     *
     * <p>Este constructor vacío es esencial para que Jackson pueda crear
     * instancias de la clase durante el proceso de deserialización de
     * objetos JSON enviados desde el frontend o APIs externas.</p>
     *
     * <p>Inicializa una instancia de Ruta con valores por defecto:
     * idRuta = 0, nombreRuta = null, origen = null, destino = null</p>
     */
    public Ruta() {}

    /**
     * Constructor completo para crear una instancia de Ruta con todos los campos.
     *
     * <p>Este constructor es utilizado principalmente cuando se recuperan datos
     * desde la base de datos, donde todos los campos incluyendo el ID ya están
     * definidos. Es ideal para operaciones de consulta y actualización.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Ruta rutaExistente = new Ruta(1, "Ruta Centro-UNACH",
     *                               "Centro Histórico",
     *                               "Ciudad Universitaria");
     * System.out.println("Ruta cargada: " + rutaExistente.getNombreRuta());
     * </pre>
     *
     * @param idRuta Identificador único de la ruta (debe ser mayor a 0)
     * @param nombreRuta Nombre descriptivo de la ruta (no debe ser null ni vacío)
     * @param origen Ubicación de inicio de la ruta (no debe ser null ni vacío)
     * @param destino Ubicación final de la ruta (no debe ser null ni vacío)
     */
    public Ruta(int idRuta, String nombreRuta, String origen, String destino) {
        this.idRuta = idRuta;
        this.nombreRuta = nombreRuta;
        this.origen = origen;
        this.destino = destino;
    }

    /**
     * Constructor para crear una nueva ruta sin ID específico (para creación).
     *
     * <p>Este constructor es ideal para crear nuevas rutas que serán insertadas
     * en la base de datos, donde el ID será auto-generado por MySQL. Es el
     * constructor más utilizado en operaciones de creación desde el frontend.</p>
     *
     * <p>Establece automáticamente el idRuta en 0, que será reemplazado por
     * el valor auto-generado cuando la entidad sea persistida en la base de datos.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Ruta nuevaRuta = new Ruta("Ruta Periférico Norte",
     *                           "Terminal Norte",
     *                           "Plaza Comercial");
     * // El ID será asignado automáticamente por la base de datos
     * </pre>
     *
     * @param nombreRuta Nombre descriptivo de la ruta (no debe ser null ni vacío)
     * @param origen Ubicación de inicio de la ruta (no debe ser null ni vacío)
     * @param destino Ubicación final de la ruta (no debe ser null ni vacío)
     */
    public Ruta(String nombreRuta, String origen, String destino) {
        this.nombreRuta = nombreRuta;
        this.origen = origen;
        this.destino = destino;
    }

    /**
     * Obtiene el identificador único de la ruta.
     *
     * <p>Retorna el ID auto-generado por la base de datos que identifica
     * de manera única esta ruta en el sistema. Este valor es inmutable
     * una vez que la ruta ha sido persistida en la base de datos.</p>
     *
     * @return Identificador único de la ruta, 0 si la ruta no ha sido persistida
     */
    public int getIdRuta() {
        return idRuta;
    }

    /**
     * Establece el identificador único de la ruta.
     *
     * <p>Este método generalmente es utilizado por el framework ORM o
     * durante la deserialización de datos desde JSON. En condiciones
     * normales de uso de la aplicación, este valor no debería modificarse
     * manualmente una vez que la ruta ha sido creada.</p>
     *
     * <p><strong>Nota de implementación:</strong> Se recomienda usar este método
     * únicamente durante la carga inicial de datos o en procesos de migración.</p>
     *
     * @param idRuta Nuevo identificador para la ruta (debe ser mayor a 0 para rutas persistidas)
     */
    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    /**
     * Obtiene el nombre descriptivo de la ruta.
     *
     * <p>Retorna el nombre comercial o identificativo de la ruta que es
     * mostrado a los usuarios en la interfaz del sistema. Este nombre
     * debe ser único y descriptivo para facilitar la identificación.</p>
     *
     * @return Nombre de la ruta, puede ser null si no ha sido establecido
     */
    public String getNombreRuta() {
        return nombreRuta;
    }

    /**
     * Establece el nombre descriptivo de la ruta.
     *
     * <p>Define el nombre que será mostrado a los usuarios en la interfaz
     * del sistema. Se recomienda usar nombres descriptivos y únicos que
     * faciliten la identificación de la ruta por parte de los usuarios.</p>
     *
     * <p>Ejemplos de nombres válidos:</p>
     * <ul>
     * <li>"Ruta Centro-Universidad"</li>
     * <li>"Línea 1 Norte"</li>
     * <li>"Circuito Periférico"</li>
     * </ul>
     *
     * @param nombreRuta Nuevo nombre para la ruta (se recomienda que no sea null ni vacío)
     */
    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    /**
     * Obtiene la ubicación de origen de la ruta.
     *
     * <p>Retorna la descripción del punto de partida geográfico de la ruta.
     * Esta información es utilizada para mostrar a los usuarios dónde
     * inicia el recorrido del transporte público.</p>
     *
     * @return Ubicación de origen de la ruta, puede ser null si no ha sido establecida
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * Establece la ubicación de origen de la ruta.
     *
     * <p>Define el punto de partida geográfico del recorrido. Debe ser
     * una descripción clara y precisa que permita a los usuarios identificar
     * fácilmente el lugar donde pueden abordar el transporte.</p>
     *
     * <p>Ejemplos de ubicaciones de origen válidas:</p>
     * <ul>
     * <li>"Terminal de Autobuses Central"</li>
     * <li>"Plaza Principal, Centro Histórico"</li>
     * <li>"Av. Central Sur #1240, Col. Centro"</li>
     * </ul>
     *
     * @param origen Nueva ubicación de origen (se recomienda que no sea null ni vacío)
     */
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    /**
     * Obtiene la ubicación de destino de la ruta.
     *
     * <p>Retorna la descripción del punto final del recorrido de la ruta.
     * Esta información complementa al origen para dar una visión completa
     * del trayecto que cubre el transporte público.</p>
     *
     * @return Ubicación de destino de la ruta, puede ser null si no ha sido establecida
     */
    public String getDestino() {
        return destino;
    }

    /**
     * Establece la ubicación de destino de la ruta.
     *
     * <p>Define el punto final del recorrido de transporte. Debe proporcionar
     * información geográfica suficiente para que los usuarios identifiquen
     * claramente dónde termina el servicio de la ruta.</p>
     *
     * <p>Ejemplos de ubicaciones de destino válidas:</p>
     * <ul>
     * <li>"Ciudad Universitaria, UNACH"</li>
     * <li>"Centro Comercial Las Américas"</li>
     * <li>"Terminal de Autobuses Norte"</li>
     * </ul>
     *
     * @param destino Nueva ubicación de destino (se recomienda que no sea null ni vacío)
     */
    public void setDestino(String destino) {
        this.destino = destino;
    }

    /**
     * Genera una representación en cadena de texto de la instancia de Ruta.
     *
     * <p>Este método es útil para depuración, logging y visualización de datos
     * durante el desarrollo. Proporciona una vista completa de todos los
     * campos de la ruta en un formato legible.</p>
     *
     * <p>Formato de salida:</p>
     * <pre>
     * Ruta{idRuta=1, nombreRuta='Ruta Centro-UNACH', origen='Centro Histórico', destino='Ciudad Universitaria'}
     * </pre>
     *
     * @return Representación en cadena de la ruta con todos sus campos
     */
    @Override
    public String toString() {
        return "Ruta{" +
                "idRuta=" + idRuta +
                ", nombreRuta='" + nombreRuta + '\'' +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                '}';
    }
}