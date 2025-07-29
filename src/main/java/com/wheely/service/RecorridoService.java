package com.wheely.service;

import com.wheely.model.Recorrido;
import com.wheely.repository.RecorridoRepository;
import com.wheely.repository.RutaRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la lógica de negocio de recorridos del sistema Wheely.
 *
 * <p>Maneja la gestión completa de recorridos que definen los trazados específicos
 * de las rutas de transporte público. Los recorridos contienen la información
 * geográfica en formato GeoJSON y su estado de activación dentro del sistema.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión CRUD completa de recorridos de rutas</li>
 * <li>Validación de archivos GeoJSON requeridos</li>
 * <li>Consultas especializadas por ruta y archivo</li>
 * <li>Control de estado activo/inactivo de recorridos</li>
 * <li>Búsqueda por nombre de archivo GeoJSON</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see Recorrido
 * @see RecorridoRepository
 * @see RutaRepository
 * @see com.wheely.model.Ruta
 */
public class RecorridoService {
    private final RecorridoRepository recorridoRepository;

    /**
     * Constructor que inicializa el servicio con sus dependencias.
     *
     * @param recorridoRepository Repositorio principal para operaciones de recorridos
     * @param rutaRepository Repositorio de rutas para validaciones de integridad
     */
    public RecorridoService(RecorridoRepository recorridoRepository, RutaRepository rutaRepository) {
        this.recorridoRepository = recorridoRepository;
    }

    /**
     * Obtiene todos los recorridos del sistema.
     *
     * <p>Retorna la lista completa de recorridos registrados en el sistema,
     * útil para administración general, visualización de mapas completos
     * y análisis de cobertura geográfica del transporte público.</p>
     *
     * @return Lista completa de recorridos del sistema
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public List<Recorrido> getAllRecorridos() throws SQLException {
        return recorridoRepository.findAll();
    }

    /**
     * Obtiene un recorrido específico por su identificador único.
     *
     * @param id ID único del recorrido a buscar
     * @return Recorrido encontrado o null si no existe
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public Recorrido getRecorridoById(int id) throws SQLException {
        return recorridoRepository.findById(id);
    }

    /**
     * Crea un nuevo recorrido en el sistema.
     *
     * <p>Inserta un nuevo recorrido aplicando validaciones de integridad.
     * Requiere que el recorrido tenga asociado un archivo GeoJSON válido
     * que contenga la información geográfica del trazado.</p>
     *
     * <p>Validaciones aplicadas:</p>
     * <ul>
     * <li>Recorrido no nulo</li>
     * <li>Archivo GeoJSON obligatorio y no vacío</li>
     * <li>Integridad básica de datos requeridos</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Recorrido nuevoRecorrido = new Recorrido();
     * nuevoRecorrido.setIdRuta(1);
     * nuevoRecorrido.setNombreArchivoGeojson("ruta_centro_norte.geojson");
     * nuevoRecorrido.setActivo(true);
     * int id = recorridoService.createRecorrido(nuevoRecorrido);
     * System.out.println("Recorrido creado con ID: " + id);
     * </pre>
     *
     * @param recorrido Recorrido a crear con archivo GeoJSON válido
     * @return ID único del recorrido creado (generado por base de datos)
     * @throws SQLException Si hay error en la inserción
     * @throws IllegalArgumentException Si el archivo GeoJSON es requerido
     * @see #validateRecorrido(Recorrido)
     */
    public int createRecorrido(Recorrido recorrido) throws SQLException {
        validateRecorrido(recorrido);
        return recorridoRepository.save(recorrido);
    }

    /**
     * Actualiza un recorrido existente en el sistema.
     *
     * <p>Modifica los datos de un recorrido previamente creado, aplicando
     * las mismas validaciones que en la creación. Útil para cambios en
     * archivos GeoJSON o actualizaciones de estado.</p>
     *
     * @param recorrido Recorrido con datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si los datos son inválidos
     * @see #validateRecorrido(Recorrido)
     */
    public boolean updateRecorrido(Recorrido recorrido) throws SQLException {
        validateRecorrido(recorrido);
        return recorridoRepository.update(recorrido);
    }

    /**
     * Elimina un recorrido del sistema.
     *
     * <p>Remueve permanentemente un recorrido del sistema. Esta operación
     * debe usarse con precaución ya que puede afectar datos relacionados
     * como coordenadas y paradas asociadas al recorrido.</p>
     *
     * @param id ID único del recorrido a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     */
    public boolean deleteRecorrido(int id) throws SQLException {
        return recorridoRepository.delete(id);
    }

    /**
     * Obtiene todos los recorridos de una ruta específica.
     *
     * <p>Retorna los recorridos asociados a una ruta particular, útil para
     * mostrar las diferentes variantes de trazado que puede tener una misma
     * ruta según horarios, condiciones de tráfico o configuraciones especiales.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;Recorrido&gt; recorridosRuta = recorridoService.getRecorridosByRuta(3);
     * System.out.println("La ruta 3 tiene " + recorridosRuta.size() + " recorridos");
     * for (Recorrido rec : recorridosRuta) {
     *     System.out.println("- " + rec.getNombreArchivoGeojson());
     * }
     * </pre>
     *
     * @param rutaId ID de la ruta para obtener sus recorridos
     * @return Lista de recorridos asociados a la ruta especificada
     * @throws SQLException Si hay error en la consulta
     */
    public List<Recorrido> getRecorridosByRuta(int rutaId) throws SQLException {
        return recorridoRepository.findByRuta(rutaId);
    }

    /**
     * Busca recorridos por nombre de archivo GeoJSON.
     *
     * <p>Permite localizar recorridos utilizando el nombre del archivo GeoJSON
     * asociado. Útil para gestión de archivos geográficos y identificación
     * de recorridos por su fuente de datos geoespaciales.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;Recorrido&gt; recorridos =
     *     recorridoService.buscarRecorridosPorArchivo("centro_norte");
     * // Encuentra recorridos cuyo archivo contenga "centro_norte"
     * </pre>
     *
     * @param nombreArchivo Nombre o parte del nombre del archivo GeoJSON a buscar
     * @return Lista de recorridos que coinciden con el criterio de búsqueda
     * @throws SQLException Si hay error en la consulta
     */
    public List<Recorrido> buscarRecorridosPorArchivo(String nombreArchivo) throws SQLException {
        return recorridoRepository.findByNombreArchivo(nombreArchivo);
    }

    /**
     * Actualiza el estado activo/inactivo de un recorrido específico.
     *
     * <p>Permite activar o desactivar un recorrido sin modificar otros datos.
     * Útil para control operativo del sistema, permitiendo habilitar o
     * deshabilitar recorridos según necesidades del servicio de transporte.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * // Activar recorrido para servicio
     * boolean activado = recorridoService.updateEstadoRecorrido(5, true);
     * if (activado) {
     *     System.out.println("Recorrido activado exitosamente");
     * }
     *
     * // Desactivar recorrido temporalmente
     * recorridoService.updateEstadoRecorrido(5, false);
     * </pre>
     *
     * @param idRecorrido ID del recorrido a modificar
     * @param activo true para activar, false para desactivar
     * @return true si la actualización fue exitosa, false si no se encontró el recorrido
     * @throws SQLException Si hay error en la operación de base de datos
     */
    public boolean updateEstadoRecorrido(int idRecorrido, boolean activo) throws SQLException {
        return recorridoRepository.updateEstado(idRecorrido, activo);
    }

    /**
     * Valida los datos básicos de un recorrido.
     * Método privado que aplica reglas de negocio de validación del sistema.
     *
     * <p>Validación aplicada:</p>
     * <ul>
     * <li>Recorrido no nulo</li>
     * <li>Nombre de archivo GeoJSON obligatorio y no vacío</li>
     * <li>Integridad básica de datos requeridos</li>
     * </ul>
     *
     * @param recorrido Recorrido a validar
     * @throws IllegalArgumentException Si el archivo GeoJSON es requerido
     */
    private void validateRecorrido(Recorrido recorrido) {
        if (recorrido == null || recorrido.getNombreArchivoGeojson() == null || recorrido.getNombreArchivoGeojson().trim().isEmpty()) {
            throw new IllegalArgumentException("Archivo GeoJSON es requerido");
        }
    }
}