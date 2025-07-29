package com.wheely.service;

import com.wheely.model.Reporte;
import com.wheely.repository.ReporteRepository;
import com.wheely.repository.UsuarioRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la lógica de negocio de reportes del sistema Wheely.
 *
 * <p>Maneja la gestión completa de reportes generados por usuarios sobre
 * problemas y incidencias en el sistema de transporte público. Incluye
 * validaciones de integridad referencial, control de autorización y
 * facilitación del monitoreo de calidad del servicio.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión CRUD completa de reportes de usuarios</li>
 * <li>Validación de existencia de usuarios reportantes</li>
 * <li>Control de autorización para edición y eliminación</li>
 * <li>Consultas especializadas por usuario y ruta</li>
 * <li>Aplicación de reglas de negocio para contenido de reportes</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see Reporte
 * @see ReporteRepository
 * @see UsuarioRepository
 * @see TipoReporteService
 */
public class ReporteService {
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor que inicializa el servicio con sus dependencias.
     *
     * @param reporteRepository Repositorio principal para operaciones de reportes
     * @param usuarioRepository Repositorio de usuarios para validaciones de integridad
     */
    public ReporteService(ReporteRepository reporteRepository, UsuarioRepository usuarioRepository) {
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene todos los reportes del sistema ordenados por fecha.
     *
     * <p>Retorna la lista completa de reportes generados por usuarios,
     * útil para análisis estadístico, reportes de calidad del servicio
     * y monitoreo de tendencias en problemas del transporte.</p>
     *
     * @return Lista completa de reportes ordenados por fecha de creación
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public List<Reporte> getAllReportes() throws SQLException {
        return reporteRepository.findAll();
    }

    /**
     * Obtiene un reporte específico por su identificador único.
     *
     * @param id ID único del reporte a buscar
     * @return Reporte encontrado o null si no existe
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public Reporte getReporteById(int id) throws SQLException {
        return reporteRepository.findById(id);
    }

    /**
     * Crea un nuevo reporte en el sistema.
     *
     * <p>Inserta un nuevo reporte de usuario con validaciones completas
     * de integridad referencial y reglas de negocio. Verifica la existencia
     * del usuario reportante y valida el contenido del reporte.</p>
     *
     * <p>Validaciones aplicadas:</p>
     * <ul>
     * <li>Existencia del usuario reportante en el sistema</li>
     * <li>Contenido válido de título y descripción</li>
     * <li>Tipo de reporte válido (1-5)</li>
     * <li>Longitud apropiada del título (máx. 100 caracteres)</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Reporte nuevoReporte = new Reporte(5, 1, 10,
     *     "Retraso en ruta", "Autobús llegó 15 minutos tarde");
     * int id = reporteService.createReporte(nuevoReporte);
     * System.out.println("Reporte creado con ID: " + id);
     * </pre>
     *
     * @param reporte Reporte a crear con datos válidos
     * @return ID único del reporte creado (generado por base de datos)
     * @throws SQLException Si hay error en la inserción
     * @throws IllegalArgumentException Si los datos son inválidos o el usuario no existe
     * @see #validateReporte(Reporte)
     */
    public int createReporte(Reporte reporte) throws SQLException {
        validateReporte(reporte);
        if (usuarioRepository.findById(reporte.getIdUsuario()) == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return reporteRepository.save(reporte);
    }

    /**
     * Actualiza un reporte existente en el sistema.
     *
     * <p>Modifica los datos de un reporte previamente creado, aplicando
     * validaciones y control de autorización. Solo el autor original
     * del reporte puede editarlo, garantizando integridad y seguridad.</p>
     *
     * @param reporte Reporte con datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si los datos son inválidos, el reporte no existe o el usuario no tiene autorización
     */
    public boolean updateReporte(Reporte reporte) throws SQLException {
        validateReporte(reporte);
        Reporte existente = reporteRepository.findById(reporte.getIdReporte());
        if (existente == null) {
            throw new IllegalArgumentException("Reporte no encontrado");
        }
        if (existente.getIdUsuario() != reporte.getIdUsuario()) {
            throw new IllegalArgumentException("Solo el autor puede editar el reporte");
        }
        return reporteRepository.update(reporte);
    }

    /**
     * Elimina un reporte del sistema con control de autorización.
     *
     * <p>Remueve permanentemente un reporte del sistema aplicando control
     * de autorización. Solo el usuario que creó el reporte puede eliminarlo,
     * protegiendo la integridad de los datos y la responsabilidad individual.</p>
     *
     * @param id ID único del reporte a eliminar
     * @param usuarioId ID del usuario que solicita la eliminación
     * @return true si la eliminación fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si el reporte no existe o el usuario no tiene autorización
     */
    public boolean deleteReporte(int id, int usuarioId) throws SQLException {
        Reporte reporte = reporteRepository.findById(id);
        if (reporte == null) {
            throw new IllegalArgumentException("Reporte no encontrado");
        }
        if (reporte.getIdUsuario() != usuarioId) {
            throw new IllegalArgumentException("Solo el autor puede eliminar el reporte");
        }
        return reporteRepository.delete(id);
    }

    /**
     * Obtiene todos los reportes de un usuario específico.
     *
     * <p>Retorna el historial completo de reportes generados por un usuario
     * particular, útil para análisis de participación ciudadana y seguimiento
     * de usuarios activos en el reporte de problemas del sistema.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;Reporte&gt; misReportes = reporteService.getReportesByUsuario(10);
     * System.out.println("Usuario tiene " + misReportes.size() + " reportes");
     * </pre>
     *
     * @param usuarioId ID del usuario para obtener sus reportes
     * @return Lista de reportes del usuario ordenados por fecha descendente
     * @throws SQLException Si hay error en la consulta
     */
    public List<Reporte> getReportesByUsuario(int usuarioId) throws SQLException {
        return reporteRepository.findByUsuario(usuarioId);
    }

    /**
     * Obtiene todos los reportes de una ruta específica.
     *
     * <p>Retorna los reportes filtrados por ruta, útil para análisis
     * de problemas específicos en rutas particulares y identificación
     * de patrones de incidencias por ubicación geográfica.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;Reporte&gt; reportesRuta5 = reporteService.getReportesByRuta(5);
     * // Analizar problemas específicos de la ruta 5
     * for (Reporte r : reportesRuta5) {
     *     System.out.println("Problema: " + r.getTitulo());
     * }
     * </pre>
     *
     * @param rutaId ID de la ruta para filtrar reportes
     * @return Lista de reportes específicos de la ruta ordenados por fecha
     * @throws SQLException Si hay error en la consulta
     */
    public List<Reporte> getReportesByRuta(int rutaId) throws SQLException {
        return reporteRepository.findByRuta(rutaId);
    }

    /**
     * Valida los datos básicos de un reporte.
     * Método privado que aplica reglas de negocio de validación específicas del sistema.
     *
     * <p>Validaciones incluidas:</p>
     * <ul>
     * <li>Reporte no nulo con título y descripción obligatorios</li>
     * <li>Título con longitud máxima de 100 caracteres</li>
     * <li>Tipo de reporte válido en rango 1-5</li>
     * <li>Integridad básica de datos requeridos</li>
     * </ul>
     *
     * @param reporte Reporte a validar
     * @throws IllegalArgumentException Si algún dato es inválido
     */
    private void validateReporte(Reporte reporte) {
        if (reporte == null || reporte.getTitulo() == null || reporte.getDescripcion() == null) {
            throw new IllegalArgumentException("Datos de reporte incompletos");
        }
        if (reporte.getTitulo().length() > 100) {
            throw new IllegalArgumentException("Título muy largo");
        }
        if (reporte.getIdTipoReporte() < 1 || reporte.getIdTipoReporte() > 5) {
            throw new IllegalArgumentException("Tipo de reporte no válido");
        }
    }
}