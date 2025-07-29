package com.wheely.service;

import com.wheely.model.Ruta;
import com.wheely.repository.RutaRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * <p>Servicio especializado para la gestión integral de rutas del sistema de transporte público WHEELY.</p>
 *
 * <p>Esta clase implementa toda la lógica de negocio relacionada con las rutas de transporte colectivo
 * en Tuxtla Gutiérrez, Chiapas. Proporciona una capa de abstracción entre los controladores REST y
 * el repositorio de datos, aplicando reglas de validación, transformaciones de datos y operaciones
 * complejas específicas del dominio del transporte urbano.</p>
 *
 * <p><strong>Responsabilidades principales:</strong></p>
 * <ul>
 *   <li>Validación de datos de rutas según reglas de negocio del transporte público</li>
 *   <li>Aplicación de restricciones de unicidad para nombres de rutas</li>
 *   <li>Gestión de búsquedas avanzadas por origen, destino y nombre</li>
 *   <li>Coordinación con otros servicios del sistema WHEELY</li>
 *   <li>Manejo centralizado de excepciones relacionadas con rutas</li>
 * </ul>
 *
 * <p>El servicio está diseñado siguiendo los principios de arquitectura limpia, separando claramente
 * las responsabilidades de acceso a datos (repositorio) de la lógica de negocio (servicio).
 * Actualizado para nueva estructura sin tiempo_promedio.</p>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Ruta
 * @see com.wheely.repository.RutaRepository
 * @see com.wheely.controller.RutaController
 */
public class RutaService {

    /**
     * Repositorio para operaciones de persistencia de rutas.
     * Proporciona acceso directo a la base de datos MySQL del sistema WHEELY.
     */
    private final RutaRepository rutaRepository;

    /**
     * <p>Constructor que inicializa el servicio de rutas con su repositorio correspondiente.</p>
     *
     * <p>Este constructor implementa el patrón de inyección de dependencias, permitiendo
     * un acoplamiento débil y facilitando las pruebas unitarias del servicio. Es utilizado
     * por el módulo de configuración de dependencias de la aplicación.</p>
     *
     * @param rutaRepository Instancia del repositorio de rutas previamente configurado.
     *                       No debe ser {@code null}.
     * @see com.wheely.di.AppModule#initRutas()
     *
     * <pre>
     * // Ejemplo de inicialización en AppModule
     * RutaRepository rutaRepository = new RutaRepository();
     * RutaService rutaService = new RutaService(rutaRepository);
     * </pre>
     */
    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    /**
     * <p>Recupera todas las rutas de transporte público disponibles en el sistema WHEELY.</p>
     *
     * <p>Este método es utilizado principalmente para:</p>
     * <ul>
     *   <li>Mostrar el catálogo completo de rutas en la aplicación web</li>
     *   <li>Generar reportes estadísticos del sistema de transporte</li>
     *   <li>Proporcionar datos para el panel administrativo</li>
     *   <li>Alimentar los mapas interactivos con todas las opciones de transporte</li>
     * </ul>
     *
     * <p>Las rutas se devuelven ordenadas alfabéticamente por nombre para facilitar
     * la navegación del usuario en la interfaz web.</p>
     *
     * @return Lista de todas las rutas registradas en el sistema.
     *         Nunca retorna {@code null}, pero puede retornar una lista vacía
     *         si no hay rutas configuradas.
     * @throws SQLException Si ocurre un error de conectividad con la base de datos MySQL
     *                      o si hay problemas en la ejecución de la consulta SQL.
     * @see #getRutaById(int)
     * @see #searchByNombre(String)
     *
     * <pre>
     * // Ejemplo de uso en el controlador REST
     * try {
     *     List&lt;Ruta&gt; todasLasRutas = rutaService.getAllRutas();
     *     System.out.println("Total de rutas disponibles: " + todasLasRutas.size());
     *
     *     for (Ruta ruta : todasLasRutas) {
     *         System.out.printf("Ruta: %s - De %s a %s%n",
     *                          ruta.getNombreRuta(),
     *                          ruta.getOrigen(),
     *                          ruta.getDestino());
     *     }
     * } catch (SQLException e) {
     *     logger.error("Error al consultar rutas: " + e.getMessage());
     * }
     * </pre>
     */
    public List<Ruta> getAllRutas() throws SQLException {
        return rutaRepository.findAll();
    }

    /**
     * <p>Busca y retorna una ruta específica utilizando su identificador único.</p>
     *
     * <p>Este método es fundamental para operaciones que requieren acceso a los detalles
     * completos de una ruta particular, como:</p>
     * <ul>
     *   <li>Mostrar información detallada de una ruta seleccionada por el usuario</li>
     *   <li>Validar referencias de ruta en reportes de incidencias</li>
     *   <li>Generar recorridos y paradas asociadas a una ruta específica</li>
     *   <li>Operaciones de actualización y eliminación de rutas</li>
     * </ul>
     *
     * @param id Identificador único de la ruta en la base de datos.
     *           Debe ser un número entero positivo válido.
     * @return La ruta encontrada con todos sus datos completos, o {@code null}
     *         si no existe una ruta con el ID especificado.
     * @throws SQLException Si hay problemas de conectividad con la base de datos
     *                      o errores en la ejecución de la consulta.
     * @see #getAllRutas()
     * @see #updateRuta(Ruta)
     * @see #deleteRuta(int)
     *
     * <pre>
     * // Ejemplo de uso para mostrar detalles de ruta
     * try {
     *     int idRutaSeleccionada = 15;
     *     Ruta rutaDetalle = rutaService.getRutaById(idRutaSeleccionada);
     *
     *     if (rutaDetalle != null) {
     *         System.out.printf("Ruta encontrada: %s%n", rutaDetalle.getNombreRuta());
     *         System.out.printf("Recorrido: %s → %s%n",
     *                          rutaDetalle.getOrigen(),
     *                          rutaDetalle.getDestino());
     *     } else {
     *         System.out.println("No se encontró la ruta con ID: " + idRutaSeleccionada);
     *     }
     * } catch (SQLException e) {
     *     logger.error("Error al buscar ruta por ID: " + e.getMessage());
     * }
     * </pre>
     */
    public Ruta getRutaById(int id) throws SQLException {
        return rutaRepository.findById(id);
    }

    /**
     * <p>Registra una nueva ruta de transporte público en el sistema WHEELY.</p>
     *
     * <p>Este método realiza validaciones exhaustivas antes de persistir la nueva ruta:</p>
     * <ul>
     *   <li>Validación de formato y contenido de todos los campos obligatorios</li>
     *   <li>Verificación de unicidad del nombre de ruta en el sistema</li>
     *   <li>Normalización de datos de origen y destino</li>
     *   <li>Aplicación de reglas de negocio específicas del transporte público</li>
     * </ul>
     *
     * <p>Una vez creada la ruta, queda disponible inmediatamente para consultas de usuarios
     * y puede ser referenciada por reportes de incidencias y gestión de recorridos.</p>
     *
     * @param ruta Objeto Ruta con los datos completos de la nueva ruta.
     *             Todos los campos obligatorios deben estar definidos:
     *             nombreRuta, origen y destino.
     * @return El identificador único asignado automáticamente a la nueva ruta creada.
     *         Este ID puede utilizarse para futuras operaciones sobre la ruta.
     * @throws SQLException Si ocurre un error durante la inserción en la base de datos
     *                      o problemas de conectividad.
     * @throws IllegalArgumentException Si la ruta proporcionada es {@code null},
     *                                  tiene campos vacíos o inválidos, o si ya existe
     *                                  una ruta con el mismo nombre.
     * @see #validateRuta(Ruta)
     * @see #updateRuta(Ruta)
     * @see com.wheely.model.Ruta
     *
     * <pre>
     * // Ejemplo de creación de nueva ruta
     * try {
     *     Ruta nuevaRuta = new Ruta();
     *     nuevaRuta.setNombreRuta("Ruta 42 - Express Centro");
     *     nuevaRuta.setOrigen("Terminal de Autobuses");
     *     nuevaRuta.setDestino("Zona Dorada");
     *
     *     int idNuevaRuta = rutaService.createRuta(nuevaRuta);
     *     System.out.printf("Ruta creada exitosamente con ID: %d%n", idNuevaRuta);
     *
     * } catch (IllegalArgumentException e) {
     *     System.err.println("Error de validación: " + e.getMessage());
     * } catch (SQLException e) {
     *     System.err.println("Error de base de datos: " + e.getMessage());
     * }
     * </pre>
     */
    public int createRuta(Ruta ruta) throws SQLException {
        validateRuta(ruta);

        // Verificar que no exista una ruta con el mismo nombre
        if (rutaRepository.existsByNombre(ruta.getNombreRuta())) {
            throw new IllegalArgumentException("Ya existe una ruta con ese nombre");
        }

        return rutaRepository.save(ruta);
    }

    /**
     * <p>Actualiza los datos de una ruta existente en el sistema WHEELY.</p>
     *
     * <p>Esta operación permite modificar cualquier aspecto de una ruta previamente registrada,
     * manteniendo la integridad referencial con reportes, recorridos y paradas asociadas.
     * El sistema valida que los nuevos datos cumplan con todas las reglas de negocio antes
     * de aplicar los cambios.</p>
     *
     * <p><strong>Consideraciones importantes:</strong></p>
     * <ul>
     *   <li>El ID de la ruta no puede modificarse una vez asignado</li>
     *   <li>Los cambios en origen/destino pueden afectar reportes históricos</li>
     *   <li>La modificación del nombre requiere verificación de unicidad</li>
     *   <li>Los cambios se propagan automáticamente a la aplicación web</li>
     * </ul>
     *
     * @param ruta Objeto Ruta con los datos actualizados. Debe incluir el ID
     *             de la ruta existente y todos los campos que se desean modificar.
     * @return {@code true} si la actualización se realizó correctamente,
     *         {@code false} si no se pudo actualizar la ruta.
     * @throws SQLException Si hay problemas de conectividad con la base de datos
     *                      o errores durante la operación de actualización.
     * @throws IllegalArgumentException Si la ruta es {@code null}, tiene datos inválidos,
     *                                  no se encuentra en el sistema, o si el nuevo nombre
     *                                  ya existe en otra ruta.
     * @see #createRuta(Ruta)
     * @see #getRutaById(int)
     * @see #validateRuta(Ruta)
     *
     * <pre>
     * // Ejemplo de actualización de ruta existente
     * try {
     *     // Obtener ruta actual
     *     Ruta rutaExistente = rutaService.getRutaById(25);
     *
     *     if (rutaExistente != null) {
     *         // Actualizar datos necesarios
     *         rutaExistente.setNombreRuta("Ruta 25 - Expreso Aeropuerto");
     *         rutaExistente.setDestino("Aeropuerto Internacional");
     *
     *         boolean actualizada = rutaService.updateRuta(rutaExistente);
     *
     *         if (actualizada) {
     *             System.out.println("Ruta actualizada exitosamente");
     *         } else {
     *             System.out.println("No se pudo actualizar la ruta");
     *         }
     *     }
     * } catch (SQLException e) {
     *     logger.error("Error al actualizar ruta: " + e.getMessage());
     * }
     * </pre>
     */
    public boolean updateRuta(Ruta ruta) throws SQLException {
        validateRuta(ruta);

        // Verificar que la ruta existe
        Ruta existente = rutaRepository.findById(ruta.getIdRuta());
        if (existente == null) {
            throw new IllegalArgumentException("Ruta no encontrada");
        }

        // Verificar que no exista otra ruta con el mismo nombre
        if (rutaRepository.existsByNombreExcludingId(ruta.getNombreRuta(), ruta.getIdRuta())) {
            throw new IllegalArgumentException("Ya existe otra ruta con ese nombre");
        }

        return rutaRepository.update(ruta);
    }

    /**
     * <p>Elimina permanentemente una ruta del sistema WHEELY.</p>
     *
     * <p><strong>⚠️ OPERACIÓN CRÍTICA:</strong> Esta acción es irreversible y puede afectar
     * la integridad del sistema si la ruta tiene dependencias activas. Se recomienda verificar
     * que no existan reportes, recorridos o rutas favoritas asociadas antes de proceder.</p>
     *
     * <p>La eliminación de una ruta implica:</p>
     * <ul>
     *   <li>Remoción permanente de todos los datos de la ruta</li>
     *   <li>Posible impacto en reportes históricos que referencien la ruta</li>
     *   <li>Eliminación automática de recorridos y paradas asociadas</li>
     *   <li>Remoción de la ruta de listas de favoritos de usuarios</li>
     * </ul>
     *
     * @param id Identificador único de la ruta a eliminar.
     *           Debe corresponder a una ruta existente en el sistema.
     * @return {@code true} si la ruta fue eliminada exitosamente,
     *         {@code false} si no se pudo eliminar la ruta.
     * @throws SQLException Si ocurren problemas durante la operación de eliminación
     *                      o errores de conectividad con la base de datos.
     * @throws IllegalArgumentException Si no se encuentra una ruta con el ID especificado.
     * @see #getRutaById(int)
     * @see com.wheely.service.ReporteService
     *
     * <pre>
     * // Ejemplo de eliminación segura de ruta
     * try {
     *     int idRutaAEliminar = 33;
     *
     *     // Verificar que la ruta existe antes de eliminar
     *     Ruta rutaAEliminar = rutaService.getRutaById(idRutaAEliminar);
     *
     *     if (rutaAEliminar != null) {
     *         boolean eliminada = rutaService.deleteRuta(idRutaAEliminar);
     *         if (eliminada) {
     *             System.out.println("✅ Ruta eliminada exitosamente");
     *         } else {
     *             System.out.println("❌ No se pudo eliminar la ruta");
     *         }
     *     } else {
     *         System.out.println("❌ No se encontró la ruta especificada");
     *     }
     * } catch (SQLException e) {
     *     logger.error("Error al eliminar ruta: " + e.getMessage());
     * }
     * </pre>
     */
    public boolean deleteRuta(int id) throws SQLException {
        Ruta ruta = rutaRepository.findById(id);
        if (ruta == null) {
            throw new IllegalArgumentException("Ruta no encontrada");
        }

        return rutaRepository.delete(id);
    }

    /**
     * <p>Busca rutas por coincidencias parciales en el nombre, facilitando la navegación del usuario.</p>
     *
     * <p>Este método implementa búsqueda inteligente que permite a los usuarios encontrar rutas
     * sin necesidad de recordar el nombre exacto. Es especialmente útil para:</p>
     * <ul>
     *   <li>Funcionalidad de autocompletado en la aplicación web</li>
     *   <li>Búsquedas rápidas desde dispositivos móviles</li>
     *   <li>Filtrado dinámico en listas de rutas</li>
     *   <li>Sugerencias inteligentes basadas en texto parcial</li>
     * </ul>
     *
     * @param nombre Término de búsqueda que puede ser parte del nombre de la ruta.
     *               No distingue entre mayúsculas y minúsculas.
     *               No puede ser {@code null} o vacío.
     * @return Lista de rutas que contienen el término buscado en su nombre,
     *         ordenada alfabéticamente para facilitar la selección.
     * @throws SQLException Si hay problemas de conectividad o ejecución de consulta.
     * @throws IllegalArgumentException Si el nombre de búsqueda es {@code null} o vacío.
     * @see #searchByOrigen(String)
     * @see #buscarRutasPorOrigenDestino(String, String)
     * @see #getAllRutas()
     *
     * <pre>
     * // Ejemplo de búsqueda dinámica para autocompletado
     * try {
     *     String terminoBusqueda = "centro"; // Usuario escribiendo
     *     List&lt;Ruta&gt; rutasEncontradas = rutaService.searchByNombre(terminoBusqueda);
     *
     *     System.out.printf("Se encontraron %d rutas que contienen '%s':%n",
     *                      rutasEncontradas.size(), terminoBusqueda);
     *
     *     for (Ruta ruta : rutasEncontradas) {
     *         System.out.printf("  • %s (%s → %s)%n",
     *                          ruta.getNombreRuta(),
     *                          ruta.getOrigen(),
     *                          ruta.getDestino());
     *     }
     * } catch (SQLException e) {
     *     System.err.println("Error en búsqueda: " + e.getMessage());
     * }
     * </pre>
     */
    public List<Ruta> searchByNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de búsqueda no puede estar vacío");
        }
        return rutaRepository.findByNombre(nombre.trim());
    }

    /**
     * <p>Localiza rutas que incluyen un origen específico en su recorrido.</p>
     *
     * <p>Funcionalidad clave para planificación de viajes que permite a los ciudadanos
     * de Tuxtla Gutiérrez encontrar todas las opciones de transporte disponibles desde
     * su ubicación de partida. Soporta búsqueda flexible con coincidencias parciales.</p>
     *
     * <p>Esta función es fundamental para el sistema de consulta de rutas, permitiendo
     * a los usuarios planificar sus viajes desde puntos específicos de la ciudad.</p>
     *
     * @param origen Punto de partida o ubicación de origen para filtrar rutas.
     *               Acepta nombres parciales de lugares, colonias o puntos de referencia.
     *               No puede ser {@code null} o vacío.
     * @return Lista de rutas que incluyen el origen especificado,
     *         ordenada por nombre para facilitar comparación de opciones.
     * @throws SQLException Si hay errores de conectividad o consulta de base de datos.
     * @throws IllegalArgumentException Si el origen es {@code null} o vacío.
     * @see #searchByNombre(String)
     * @see #buscarRutasPorOrigenDestino(String, String)
     *
     * <pre>
     * // Ejemplo de planificación de viaje desde ubicación específica
     * try {
     *     String miUbicacion = "Terán";
     *     List&lt;Ruta&gt; rutasDisponibles = rutaService.searchByOrigen(miUbicacion);
     *
     *     System.out.printf("Rutas disponibles desde %s:%n", miUbicacion);
     *     for (Ruta ruta : rutasDisponibles) {
     *         System.out.printf("  🚌 %s → %s%n", ruta.getNombreRuta(), ruta.getDestino());
     *     }
     * } catch (SQLException e) {
     *     logger.error("Error al buscar por origen: " + e.getMessage());
     * }
     * </pre>
     */
    public List<Ruta> searchByOrigen(String origen) throws SQLException {
        if (origen == null || origen.trim().isEmpty()) {
            throw new IllegalArgumentException("El origen de búsqueda no puede estar vacío");
        }
        return rutaRepository.findByOrigen(origen.trim());
    }

    /**
     * <p>Busca rutas combinando criterios de origen y destino para planificación completa de viajes.</p>
     *
     * <p>Este método avanzado permite a los usuarios del sistema WHEELY encontrar rutas específicas
     * que conecten dos puntos de interés en Tuxtla Gutiérrez. Implementa búsqueda flexible donde
     * tanto el origen como el destino son opcionales, permitiendo consultas parciales.</p>
     *
     * <p><strong>Funcionalidades de búsqueda:</strong></p>
     * <ul>
     *   <li>Búsqueda por origen únicamente (si destino es null o vacío)</li>
     *   <li>Búsqueda por destino únicamente (si origen es null o vacío)</li>
     *   <li>Búsqueda combinada origen-destino para rutas específicas</li>
     *   <li>Coincidencias parciales en ambos campos usando contenido de cadenas</li>
     * </ul>
     *
     * <p>La búsqueda es case-insensitive y utiliza filtrado en memoria para mayor flexibilidad
     * en las coincidencias parciales.</p>
     *
     * @param origen Punto de partida o ubicación de origen. Puede ser {@code null} o vacío
     *               para buscar solo por destino.
     * @param destino Punto de llegada o destino final. Puede ser {@code null} o vacío
     *                para buscar solo por origen.
     * @return Lista filtrada de rutas que coinciden con los criterios especificados.
     *         Si ambos parámetros son null o vacíos, retorna todas las rutas.
     * @throws SQLException Si hay problemas de conectividad con la base de datos
     *                      o errores durante la consulta.
     * @see #searchByOrigen(String)
     * @see #getAllRutas()
     * @see com.wheely.controller.RutaController#buscarPorOrigenDestino(io.javalin.http.Context)
     *
     * <pre>
     * // Ejemplo de búsqueda completa de ruta
     * try {
     *     String puntoPartida = "Terminal";
     *     String puntoLlegada = "Centro";
     *
     *     List&lt;Ruta&gt; rutasDirectas = rutaService.buscarRutasPorOrigenDestino(
     *         puntoPartida, puntoLlegada);
     *
     *     System.out.printf("Rutas de %s a %s:%n", puntoPartida, puntoLlegada);
     *     for (Ruta ruta : rutasDirectas) {
     *         System.out.printf("  🎯 %s: %s → %s%n",
     *                          ruta.getNombreRuta(),
     *                          ruta.getOrigen(),
     *                          ruta.getDestino());
     *     }
     * } catch (SQLException e) {
     *     logger.error("Error en búsqueda combinada: " + e.getMessage());
     * }
     * </pre>
     */
    public List<Ruta> buscarRutasPorOrigenDestino(String origen, String destino) throws SQLException {
        return getAllRutas().stream()
                .filter(ruta -> (origen == null || origen.trim().isEmpty() ||
                        ruta.getOrigen().toLowerCase().contains(origen.toLowerCase())) &&
                        (destino == null || destino.trim().isEmpty() ||
                                ruta.getDestino().toLowerCase().contains(destino.toLowerCase())))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * <p>Valida exhaustivamente los datos de una ruta según las reglas de negocio del sistema WHEELY.</p>
     *
     * <p>Este método privado centraliza todas las validaciones necesarias para garantizar
     * la integridad y consistencia de los datos de rutas en el sistema. Implementa las
     * reglas específicas del dominio del transporte público urbano y normaliza los datos
     * de entrada.</p>
     *
     * <p><strong>Validaciones aplicadas:</strong></p>
     * <ul>
     *   <li>Verificación de nulidad del objeto ruta</li>
     *   <li>Validación de presencia y formato del nombre de ruta</li>
     *   <li>Verificación de origen y destino no vacíos</li>
     *   <li>Aplicación de reglas de longitud máxima para campos de texto (100 caracteres)</li>
     *   <li>Normalización de espacios en blanco mediante trim()</li>
     * </ul>
     *
     * <p><strong>Normalización de datos:</strong> El método automáticamente limpia espacios
     * en blanco de los extremos de todos los campos de texto para mantener consistencia
     * en la base de datos.</p>
     *
     * @param ruta Objeto Ruta a validar antes de operaciones de persistencia.
     *             No puede ser {@code null}.
     * @throws IllegalArgumentException Si alguna validación falla, con mensaje descriptivo
     *                                  específico indicando el problema encontrado.
     * @see #createRuta(Ruta)
     * @see #updateRuta(Ruta)
     *
     * <pre>
     * // Ejemplo interno de validación
     * // Este método es llamado automáticamente por createRuta() y updateRuta()
     * private void ejemploUsoValidacion() {
     *     Ruta rutaInvalida = new Ruta();
     *     // rutaInvalida.setNombreRuta(""); // Campo vacío
     *
     *     try {
     *         validateRuta(rutaInvalida);
     *     } catch (IllegalArgumentException e) {
     *         System.out.println("Validación falló: " + e.getMessage());
     *         // Output: "El nombre de la ruta es obligatorio"
     *     }
     * }
     * </pre>
     */
    private void validateRuta(Ruta ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }

        if (ruta.getNombreRuta() == null || ruta.getNombreRuta().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ruta es obligatorio");
        }

        if (ruta.getNombreRuta().length() > 100) {
            throw new IllegalArgumentException("El nombre de la ruta no puede exceder 100 caracteres");
        }

        if (ruta.getOrigen() == null || ruta.getOrigen().trim().isEmpty()) {
            throw new IllegalArgumentException("El origen es obligatorio");
        }

        if (ruta.getOrigen().length() > 100) {
            throw new IllegalArgumentException("El origen no puede exceder 100 caracteres");
        }

        if (ruta.getDestino() == null || ruta.getDestino().trim().isEmpty()) {
            throw new IllegalArgumentException("El destino es obligatorio");
        }

        if (ruta.getDestino().length() > 100) {
            throw new IllegalArgumentException("El destino no puede exceder 100 caracteres");
        }

        // Limpiar espacios en blanco
        ruta.setNombreRuta(ruta.getNombreRuta().trim());
        ruta.setOrigen(ruta.getOrigen().trim());
        ruta.setDestino(ruta.getDestino().trim());;
    }
}