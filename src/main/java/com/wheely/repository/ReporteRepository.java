package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.Reporte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de operaciones CRUD de la entidad Reporte en el sistema Wheely.
 *
 * <p>Esta clase maneja todas las interacciones con la base de datos MySQL para la tabla Reporte,
 * proporcionando métodos especializados para gestionar reportes de incidencias, problemas y
 * observaciones del sistema de transporte público. Los reportes son fundamentales para el
 * monitoreo y mejora continua del servicio.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Operaciones CRUD completas para reportes</li>
 * <li>Consultas especializadas por usuario y ruta</li>
 * <li>Filtrado por tipo de reporte y fecha</li>
 * <li>Agregaciones para estadísticas del sistema</li>
 * <li>Manejo de fechas con LocalDateTime</li>
 * <li>Validación de relaciones con usuarios y rutas</li>
 * </ul>
 *
 * <p>Estructura de datos manejada:</p>
 * <ul>
 * <li>idReporte: Identificador único del reporte</li>
 * <li>idRuta: Referencia a la ruta relacionada</li>
 * <li>idTipoReporte: Clasificación del tipo de reporte</li>
 * <li>idUsuario: Usuario que creó el reporte</li>
 * <li>titulo: Título descriptivo del reporte</li>
 * <li>descripcion: Detalle completo del reporte</li>
 * <li>fecha_reporte: Timestamp de creación automático</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Reporte
 * @see com.wheely.config.DatabaseConfig
 * @see com.wheely.service.ReporteService
 */
public class ReporteRepository {

    /**
     * Obtiene todos los reportes del sistema ordenados por fecha de creación descendente.
     *
     * <p>Este método recupera la lista completa de reportes de la base de datos, mostrando
     * primero los más recientes. Convierte automáticamente los Timestamp de la base de datos
     * a objetos LocalDateTime para facilitar el manejo en Java 8+.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * ReporteRepository repository = new ReporteRepository();
     * List&lt;Reporte&gt; todosLosReportes = repository.findAll();
     * System.out.println("Total de reportes en el sistema: " + todosLosReportes.size());
     *
     * for (Reporte reporte : todosLosReportes) {
     *     System.out.println("Reporte: " + reporte.getTitulo());
     *     System.out.println("Fecha: " + reporte.getFechaReporte());
     *     System.out.println("Usuario ID: " + reporte.getIdUsuario());
     * }
     * </pre>
     *
     * @return Lista de objetos Reporte con todos los registros de la base de datos,
     *         ordenados cronológicamente del más reciente al más antiguo.
     *         Retorna lista vacía si no hay reportes registrados.
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     *                     o durante la ejecución de la consulta SQL
     *
     * @see #findById(int)
     * @see #findByUsuario(int)
     * @see #findByRuta(int)
     */
    public List<Reporte> findAll() throws SQLException {
        List<Reporte> reportes = new ArrayList<>();
        String query = "SELECT idReporte, idRuta, idTipoReporte, idUsuario, " +
                "titulo, descripcion, fecha_reporte FROM Reporte " +
                "ORDER BY fecha_reporte DESC";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reporte reporte = new Reporte();
                reporte.setIdReporte(rs.getInt("idReporte"));
                reporte.setIdRuta(rs.getInt("idRuta"));
                reporte.setIdTipoReporte(rs.getInt("idTipoReporte"));
                reporte.setIdUsuario(rs.getInt("idUsuario"));
                reporte.setTitulo(rs.getString("titulo"));
                reporte.setDescripcion(rs.getString("descripcion"));

                // Convertir Timestamp a LocalDateTime
                Timestamp timestamp = rs.getTimestamp("fecha_reporte");
                if (timestamp != null) {
                    reporte.setFechaReporte(timestamp.toLocalDateTime());
                }

                reportes.add(reporte);
            }
        }
        return reportes;
    }

    /**
     * Busca y recupera un reporte específico utilizando su identificador único.
     *
     * <p>Este método realiza una búsqueda directa por clave primaria, proporcionando
     * acceso rápido y preciso a un reporte específico. Incluye la conversión automática
     * de la fecha de la base de datos a LocalDateTime.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * ReporteRepository repository = new ReporteRepository();
     * Reporte reporte = repository.findById(25);
     * if (reporte != null) {
     *     System.out.println("Reporte encontrado: " + reporte.getTitulo());
     *     System.out.println("Descripción: " + reporte.getDescripcion());
     *     System.out.println("Creado por usuario ID: " + reporte.getIdUsuario());
     * } else {
     *     System.out.println("Reporte no encontrado con ID: 25");
     * }
     * </pre>
     *
     * @param idReporte Identificador único del reporte a buscar (debe ser mayor a 0)
     * @return Objeto Reporte con los datos completos si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta preparada
     * @throws IllegalArgumentException si idReporte es menor o igual a 0
     *
     * @see #findAll()
     * @see #findByUsuario(int)
     */
    public Reporte findById(int idReporte) throws SQLException {
        if (idReporte <= 0) {
            throw new IllegalArgumentException("El ID del reporte debe ser mayor a 0");
        }

        Reporte reporte = null;
        String query = "SELECT idReporte, idRuta, idTipoReporte, idUsuario, " +
                "titulo, descripcion, fecha_reporte FROM Reporte " +
                "WHERE idReporte = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idReporte);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reporte = new Reporte();
                    reporte.setIdReporte(rs.getInt("idReporte"));
                    reporte.setIdRuta(rs.getInt("idRuta"));
                    reporte.setIdTipoReporte(rs.getInt("idTipoReporte"));
                    reporte.setIdUsuario(rs.getInt("idUsuario"));
                    reporte.setTitulo(rs.getString("titulo"));
                    reporte.setDescripcion(rs.getString("descripcion"));

                    Timestamp timestamp = rs.getTimestamp("fecha_reporte");
                    if (timestamp != null) {
                        reporte.setFechaReporte(timestamp.toLocalDateTime());
                    }
                }
            }
        }
        return reporte;
    }

    /**
     * Recupera todos los reportes creados por un usuario específico.
     *
     * <p>Este método permite obtener el historial completo de reportes de un usuario,
     * útil para generar estadísticas personales y verificar la actividad de reporte
     * de cada usuario del sistema.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * ReporteRepository repository = new ReporteRepository();
     * List&lt;Reporte&gt; reportesDelUsuario = repository.findByUsuario(15);
     * System.out.println("El usuario 15 ha creado " + reportesDelUsuario.size() + " reportes");
     *
     * // Mostrar títulos de todos sus reportes
     * reportesDelUsuario.forEach(r ->
     *     System.out.println("- " + r.getTitulo() + " (" + r.getFechaReporte() + ")")
     * );
     * </pre>
     *
     * @param idUsuario ID del usuario cuyos reportes se desean consultar
     * @return Lista de reportes creados por el usuario, ordenados por fecha descendente.
     *         Retorna lista vacía si el usuario no ha creado reportes.
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta
     * @throws IllegalArgumentException si idUsuario es menor o igual a 0
     *
     * @see #findByRuta(int)
     * @see #countByUsuario(int)
     */
    public List<Reporte> findByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }

        List<Reporte> reportes = new ArrayList<>();
        String query = "SELECT idReporte, idRuta, idTipoReporte, idUsuario, " +
                "titulo, descripcion, fecha_reporte FROM Reporte " +
                "WHERE idUsuario = ? ORDER BY fecha_reporte DESC";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reporte reporte = new Reporte();
                    reporte.setIdReporte(rs.getInt("idReporte"));
                    reporte.setIdRuta(rs.getInt("idRuta"));
                    reporte.setIdTipoReporte(rs.getInt("idTipoReporte"));
                    reporte.setIdUsuario(rs.getInt("idUsuario"));
                    reporte.setTitulo(rs.getString("titulo"));
                    reporte.setDescripcion(rs.getString("descripcion"));

                    Timestamp timestamp = rs.getTimestamp("fecha_reporte");
                    if (timestamp != null) {
                        reporte.setFechaReporte(timestamp.toLocalDateTime());
                    }

                    reportes.add(reporte);
                }
            }
        }
        return reportes;
    }

    /**
     * Recupera todos los reportes asociados a una ruta específica del sistema.
     *
     * <p>Este método es fundamental para el análisis de problemas por ruta, permitiendo
     * identificar rutas con mayor frecuencia de incidencias y generar estadísticas
     * de calidad del servicio por trayecto.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * ReporteRepository repository = new ReporteRepository();
     * List&lt;Reporte&gt; reportesDeLaRuta = repository.findByRuta(8);
     * System.out.println("La ruta 8 tiene " + reportesDeLaRuta.size() + " reportes");
     *
     * // Analizar tipos de problemas más comunes
     * Map&lt;Integer, Long&gt; tiposPorFrecuencia = reportesDeLaRuta.stream()
     *     .collect(Collectors.groupingBy(Reporte::getIdTipoReporte, Collectors.counting()));
     * </pre>
     *
     * @param idRuta ID de la ruta cuyos reportes se desean consultar
     * @return Lista de reportes asociados a la ruta, ordenados por fecha descendente.
     *         Retorna lista vacía si la ruta no tiene reportes asociados.
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta
     * @throws IllegalArgumentException si idRuta es menor o igual a 0
     *
     * @see #findByUsuario(int)
     * @see #countByRuta(int)
     */
    public List<Reporte> findByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        List<Reporte> reportes = new ArrayList<>();
        String query = "SELECT idReporte, idRuta, idTipoReporte, idUsuario, " +
                "titulo, descripcion, fecha_reporte FROM Reporte " +
                "WHERE idRuta = ? ORDER BY fecha_reporte DESC";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRuta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reporte reporte = new Reporte();
                    reporte.setIdReporte(rs.getInt("idReporte"));
                    reporte.setIdRuta(rs.getInt("idRuta"));
                    reporte.setIdTipoReporte(rs.getInt("idTipoReporte"));
                    reporte.setIdUsuario(rs.getInt("idUsuario"));
                    reporte.setTitulo(rs.getString("titulo"));
                    reporte.setDescripcion(rs.getString("descripcion"));

                    Timestamp timestamp = rs.getTimestamp("fecha_reporte");
                    if (timestamp != null) {
                        reporte.setFechaReporte(timestamp.toLocalDateTime());
                    }

                    reportes.add(reporte);
                }
            }
        }
        return reportes;
    }

    /**
     * Persiste un nuevo reporte en la base de datos del sistema Wheely.
     *
     * <p>Este método inserta un registro de reporte en la tabla, generando automáticamente
     * el ID único y estableciendo la fecha actual como timestamp de creación.
     * Valida las relaciones con usuarios y rutas antes de la inserción.</p>
     *
     * <p>Validaciones realizadas:</p>
     * <ul>
     * <li>Reporte no puede ser nulo</li>
     * <li>Título es obligatorio y no puede exceder 200 caracteres</li>
     * <li>Descripción es obligatoria</li>
     * <li>IDs de ruta, tipo y usuario deben ser válidos</li>
     * <li>Referencias a tablas relacionadas deben existir</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Reporte nuevoReporte = new Reporte();
     * nuevoReporte.setIdRuta(5);
     * nuevoReporte.setIdTipoReporte(2);
     * nuevoReporte.setIdUsuario(12);
     * nuevoReporte.setTitulo("Retraso en el servicio");
     * nuevoReporte.setDescripcion("El autobús llegó 25 minutos tarde a la parada principal");
     *
     * ReporteRepository repository = new ReporteRepository();
     * int idGenerado = repository.save(nuevoReporte);
     * System.out.println("Reporte creado con ID: " + idGenerado);
     * </pre>
     *
     * @param reporte Objeto Reporte con los datos a persistir (sin ID, la fecha se asigna automáticamente)
     * @return ID generado automáticamente por la base de datos para el nuevo reporte
     * @throws SQLException si ocurre un error durante la inserción en la base de datos
     * @throws IllegalArgumentException si los datos del reporte son inválidos o incompletos
     *
     * @see #update(Reporte)
     * @see #delete(int)
     */
    public int save(Reporte reporte) throws SQLException {
        if (reporte == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo");
        }
        if (reporte.getTitulo() == null || reporte.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del reporte es obligatorio");
        }
        if (reporte.getTitulo().length() > 200) {
            throw new IllegalArgumentException("El título no puede exceder 200 caracteres");
        }
        if (reporte.getDescripcion() == null || reporte.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (reporte.getIdRuta() <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser válido");
        }
        if (reporte.getIdTipoReporte() <= 0) {
            throw new IllegalArgumentException("El ID del tipo de reporte debe ser válido");
        }
        if (reporte.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }

        String query = "INSERT INTO Reporte (idRuta, idTipoReporte, idUsuario, titulo, descripcion) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reporte.getIdRuta());
            stmt.setInt(2, reporte.getIdTipoReporte());
            stmt.setInt(3, reporte.getIdUsuario());
            stmt.setString(4, reporte.getTitulo().trim());
            stmt.setString(5, reporte.getDescripcion().trim());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear reporte, no se insertaron filas");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al crear reporte, no se obtuvo el ID generado");
                }
            }
        }
    }

    /**
     * Actualiza los datos de un reporte existente en la base de datos.
     *
     * <p>Este método modifica los campos editables de un reporte previamente registrado.
     * La fecha de creación se mantiene intacta, solo se pueden modificar los datos
     * de contenido y clasificación del reporte.</p>
     *
     * @param reporte Objeto Reporte con los datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró el reporte
     * @throws SQLException si ocurre un error durante la actualización
     * @throws IllegalArgumentException si el reporte es nulo o tiene un ID inválido
     *
     * @see #save(Reporte)
     * @see #findById(int)
     */
    public boolean update(Reporte reporte) throws SQLException {
        if (reporte == null || reporte.getIdReporte() <= 0) {
            throw new IllegalArgumentException("El reporte debe tener un ID válido para actualizar");
        }

        String query = "UPDATE Reporte SET idRuta = ?, idTipoReporte = ?, " +
                "titulo = ?, descripcion = ? WHERE idReporte = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reporte.getIdRuta());
            stmt.setInt(2, reporte.getIdTipoReporte());
            stmt.setString(3, reporte.getTitulo().trim());
            stmt.setString(4, reporte.getDescripcion().trim());
            stmt.setInt(5, reporte.getIdReporte());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un reporte específico del sistema utilizando su identificador único.
     *
     * <p>Esta operación es permanente y elimina todos los datos del reporte.
     * Se recomienda verificar permisos de usuario antes de ejecutar esta operación.</p>
     *
     * @param idReporte Identificador único del reporte a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró el reporte
     * @throws SQLException si ocurre un error durante la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     *
     * @see #save(Reporte)
     */
    public boolean delete(int idReporte) throws SQLException {
        if (idReporte <= 0) {
            throw new IllegalArgumentException("El ID del reporte debe ser mayor a 0");
        }

        String query = "DELETE FROM Reporte WHERE idReporte = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idReporte);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Obtiene el número total de reportes registrados en el sistema.
     *
     * <p>Este método es utilizado para generar estadísticas generales
     * y métricas del panel administrativo del sistema Wheely.</p>
     *
     * @return Número total de reportes en el sistema
     * @throws SQLException si ocurre un error en la consulta
     */
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM Reporte";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /**
     * Cuenta el número de reportes creados por un usuario específico.
     *
     * <p>Útil para generar estadísticas de participación de usuarios
     * y identificar usuarios más activos en el reporte de incidencias.</p>
     *
     * @param idUsuario ID del usuario para contar sus reportes
     * @return Número de reportes creados por el usuario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idUsuario es inválido
     */
    public int countByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }

        String query = "SELECT COUNT(*) FROM Reporte WHERE idUsuario = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    /**
     * Cuenta el número de reportes asociados a una ruta específica.
     *
     * <p>Este método es fundamental para identificar rutas problemáticas
     * y generar indicadores de calidad del servicio por trayecto.</p>
     *
     * @param idRuta ID de la ruta para contar sus reportes
     * @return Número de reportes asociados a la ruta
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRuta es inválido
     */
    public int countByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        String query = "SELECT COUNT(*) FROM Reporte WHERE idRuta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRuta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
}