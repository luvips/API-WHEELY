package com.wheely.model;

/**
 * Modelo de datos para representar las rutas favoritas de los usuarios en el sistema Wheely.
 *
 * <p>Esta clase encapsula la relación de muchos a muchos entre usuarios y rutas de transporte,
 * permitiendo a los usuarios marcar rutas específicas como favoritas para acceso rápido y
 * personalización de su experiencia. Actúa como una tabla de unión (junction table) que
 * establece las preferencias individuales de cada usuario respecto a las rutas disponibles
 * en el sistema de transporte público.</p>
 *
 * <p>La clase está diseñada para facilitar la personalización de la experiencia del usuario,
 * permitiendo funcionalidades como filtrado rápido de rutas, acceso prioritario a información
 * de rutas frecuentemente utilizadas, y generación de recomendaciones basadas en preferencias.
 * Forma parte del módulo de gestión de usuarios y personalización del sistema Wheely.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión de relaciones usuario-ruta para marcado de favoritos</li>
 * <li>Soporte para consultas personalizadas por usuario</li>
 * <li>Base para sistemas de recomendación y filtrado</li>
 * <li>Optimización de acceso a rutas frecuentemente consultadas</li>
 * <li>Análisis de preferencias de usuarios para mejoras del servicio</li>
 * </ul>
 *
 * <p><strong>Relaciones del modelo:</strong></p>
 * <ul>
 * <li>Pertenece a un Usuario específico (relación N:1)</li>
 * <li>Hace referencia a una Ruta específica (relación N:1)</li>
 * <li>Implementa relación muchos-a-muchos entre Usuario y Ruta</li>
 * </ul>
 *
 * <p><strong>Casos de uso principales:</strong></p>
 * <ul>
 * <li>Marcado y desmarcado de rutas como favoritas por parte del usuario</li>
 * <li>Visualización prioritaria de rutas favoritas en la interfaz</li>
 * <li>Generación de estadísticas de uso y preferencias</li>
 * <li>Filtrado personalizado de búsquedas de rutas</li>
 * <li>Notificaciones específicas para rutas de interés del usuario</li>
 * </ul>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Usuario
 * @see com.wheely.model.Ruta
 * @see com.wheely.service.RutaFavoritaService
 * @see com.wheely.controller.RutaFavoritaController
 */
public class RutaFavorita {

    /**
     * Identificador único de la relación ruta favorita en la base de datos.
     *
     * <p>Este campo representa la clave primaria auto-generada por MySQL.
     * Se utiliza para referenciar de manera única cada relación usuario-ruta
     * en el sistema, permitiendo operaciones CRUD específicas sobre las
     * preferencias individuales de los usuarios.</p>
     */
    private int idRutaFavorita;

    /**
     * Identificador del usuario al cual pertenece esta ruta favorita.
     *
     * <p>Este campo establece la relación foreign key con la tabla Usuario.
     * Permite asociar múltiples rutas favoritas a un usuario específico,
     * creando un perfil personalizado de preferencias de transporte para
     * cada usuario registrado en el sistema.</p>
     */
    private int idUsuario;

    /**
     * Identificador de la ruta marcada como favorita por el usuario.
     *
     * <p>Este campo establece la relación foreign key con la tabla Ruta.
     * Permite referenciar la ruta específica que ha sido seleccionada
     * como favorita, manteniendo la integridad referencial y facilitando
     * las consultas de información detallada de la ruta.</p>
     */
    private int idRuta;

    /**
     * Constructor por defecto requerido para la deserialización JSON.
     *
     * <p>Este constructor vacío es esencial para que Jackson y otros frameworks
     * de serialización puedan crear instancias de la clase durante el proceso
     * de deserialización de objetos JSON enviados desde el frontend o APIs externas.</p>
     *
     * <p>Inicializa una instancia de RutaFavorita con valores por defecto:
     * idRutaFavorita = 0, idUsuario = 0, idRuta = 0</p>
     */
    public RutaFavorita() {}

    /**
     * Constructor completo para crear una instancia de RutaFavorita con todos los campos.
     *
     * <p>Este constructor es utilizado principalmente cuando se recuperan datos
     * desde la base de datos, donde todos los campos incluyendo el ID ya están
     * definidos. Es ideal para operaciones de consulta y gestión de favoritos existentes.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavorita favoritaExistente = new RutaFavorita(25, 7, 3);
     * System.out.println("Favorita cargada - Usuario: " + favoritaExistente.getIdUsuario() +
     *                    ", Ruta: " + favoritaExistente.getIdRuta());
     * </pre>
     *
     * @param idRutaFavorita Identificador único de la ruta favorita (debe ser mayor a 0)
     * @param idUsuario Identificador del usuario propietario (debe ser mayor a 0)
     * @param idRuta Identificador de la ruta favorita (debe ser mayor a 0)
     */
    public RutaFavorita(int idRutaFavorita, int idUsuario, int idRuta) {
        this.idRutaFavorita = idRutaFavorita;
        this.idUsuario = idUsuario;
        this.idRuta = idRuta;
    }

    /**
     * Constructor para crear una nueva relación ruta favorita sin ID específico (para creación).
     *
     * <p>Este constructor es ideal para crear nuevas relaciones de favoritos que serán
     * insertadas en la base de datos, donde el ID será auto-generado por MySQL. Es el
     * constructor más utilizado cuando un usuario marca una ruta como favorita desde
     * la interfaz de usuario.</p>
     *
     * <p>Establece automáticamente el idRutaFavorita en 0, que será reemplazado por
     * el valor auto-generado cuando la entidad sea persistida en la base de datos.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * // Usuario 7 marca la ruta 3 como favorita
     * RutaFavorita nuevaFavorita = new RutaFavorita(7, 3);
     * // El ID será asignado automáticamente por la base de datos
     * rutaFavoritaService.createRutaFavorita(nuevaFavorita);
     * </pre>
     *
     * @param idUsuario Identificador del usuario que marca la favorita (debe ser mayor a 0)
     * @param idRuta Identificador de la ruta a marcar como favorita (debe ser mayor a 0)
     */
    public RutaFavorita(int idUsuario, int idRuta) {
        this.idUsuario = idUsuario;
        this.idRuta = idRuta;
    }

    /**
     * Obtiene el identificador único de la ruta favorita.
     *
     * <p>Retorna el ID auto-generado por la base de datos que identifica
     * de manera única esta relación usuario-ruta en el sistema. Este valor
     * es inmutable una vez que la relación ha sido persistida en la base de datos.</p>
     *
     * @return Identificador único de la ruta favorita, 0 si no ha sido persistida
     */
    public int getIdRutaFavorita() {
        return idRutaFavorita;
    }

    /**
     * Establece el identificador único de la ruta favorita.
     *
     * <p>Este método generalmente es utilizado por el framework ORM o
     * durante la deserialización de datos desde JSON. En condiciones
     * normales de uso de la aplicación, este valor no debería modificarse
     * manualmente una vez que la relación ha sido creada.</p>
     *
     * <p><strong>Nota de implementación:</strong> Se recomienda usar este método
     * únicamente durante la carga inicial de datos o en procesos de migración.</p>
     *
     * @param idRutaFavorita Nuevo identificador para la ruta favorita (debe ser mayor a 0 para favoritas persistidas)
     */
    public void setIdRutaFavorita(int idRutaFavorita) {
        this.idRutaFavorita = idRutaFavorita;
    }

    /**
     * Obtiene el identificador del usuario propietario de la ruta favorita.
     *
     * <p>Retorna el ID del usuario que ha marcado esta ruta como favorita.
     * Esta relación permite consultar todas las rutas favoritas de un usuario
     * específico y personalizar su experiencia en el sistema.</p>
     *
     * @return Identificador del usuario propietario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario propietario de la ruta favorita.
     *
     * <p>Define la relación de pertenencia entre la ruta favorita y el usuario.
     * Este valor debe corresponder a un ID válido existente en la tabla Usuario
     * para mantener la integridad referencial de la base de datos.</p>
     *
     * <p><strong>Validación requerida:</strong> El idUsuario debe existir en la tabla Usuario.</p>
     *
     * @param idUsuario Identificador del usuario propietario (debe ser mayor a 0 y existir en la BD)
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el identificador de la ruta marcada como favorita.
     *
     * <p>Retorna el ID de la ruta que ha sido seleccionada como favorita
     * por el usuario. Esta referencia permite acceder a toda la información
     * detallada de la ruta (nombre, origen, destino, recorridos, etc.).</p>
     *
     * @return Identificador de la ruta favorita
     */
    public int getIdRuta() {
        return idRuta;
    }

    /**
     * Establece el identificador de la ruta marcada como favorita.
     *
     * <p>Define la referencia a la ruta específica que será marcada como favorita.
     * Este valor debe corresponder a un ID válido existente en la tabla Ruta
     * para mantener la integridad referencial y garantizar que la ruta
     * efectivamente existe en el sistema.</p>
     *
     * <p><strong>Validación requerida:</strong> El idRuta debe existir en la tabla Ruta.</p>
     * <p><strong>Restricción de unicidad:</strong> La combinación idUsuario-idRuta debe ser única.</p>
     *
     * @param idRuta Identificador de la ruta a marcar como favorita (debe ser mayor a 0 y existir en la BD)
     */
    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    /**
     * Genera una representación en cadena de texto de la instancia de RutaFavorita.
     *
     * <p>Este método es útil para depuración, logging y visualización de datos
     * durante el desarrollo. Proporciona una vista completa de todos los
     * campos de la ruta favorita en un formato legible y estructurado.</p>
     *
     * <p>Formato de salida:</p>
     * <pre>
     * RutaFavorita{idRutaFavorita=25, idUsuario=7, idRuta=3}
     * </pre>
     *
     * @return Representación en cadena de la ruta favorita con todos sus campos
     */
    @Override
    public String toString() {
        return "RutaFavorita{" +
                "idRutaFavorita=" + idRutaFavorita +
                ", idUsuario=" + idUsuario +
                ", idRuta=" + idRuta +
                '}';
    }
}