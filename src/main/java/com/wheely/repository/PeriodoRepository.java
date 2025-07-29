package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.Periodo;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Repositorio para la gestión de operaciones CRUD de la entidad Periodo en el sistema WHEELY.
 * Maneja todas las interacciones con la base de datos MySQL para la tabla Periodo,
 * permitiendo la administración de bloques horarios del sistema de transporte público.
 * </p>
 * <ul>
 *   <li>Operaciones CRUD completas para periodos</li>
 *   <li>Búsqueda por nombre y por ID</li>
 *   <li>Obtención del periodo actual según la hora del sistema</li>
 *   <li>Conteo de periodos para estadísticas</li>
 *   <li>Validación de unicidad de nombre de periodo</li>
 * </ul>
 * <p>
 * Consideraciones de seguridad:
 * <ul>
 *   <li>Validación de parámetros para evitar SQL injection</li>
 *   <li>Uso de PreparedStatements en todas las consultas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Periodo
 * @see com.wheely.config.DatabaseConfig
 * @see com.wheely.service.PeriodoService
 */
public class PeriodoRepository {

    /**
     * <p>
     * Obtiene todos los periodos registrados en el sistema, ordenados por hora de inicio.
     * </p>
     * <pre>
     * PeriodoRepository repository = new PeriodoRepository();
     * List&lt;Periodo&gt; periodos = repository.findAll();
     * for (Periodo p : periodos) {
     *     System.out.println("Periodo: " + p.getNombre() + " - " + p.getHoraInicio());
     * }
     * </pre>
     *
     * @return Lista de objetos Periodo con todos los registros, lista vacía si no hay periodos.
     * @throws SQLException si ocurre un error en la consulta SQL o conexión a la base de datos.
     * @see #findById(int)
     * @see #findByNombre(String)
     */
    public List<Periodo> findAll() throws SQLException {
        List<Periodo> periodos = new ArrayList<>();
        String sql = "SELECT idPeriodo, nombre_periodo, hora_inicio, hora_fin, descripcion FROM Periodo ORDER BY hora_inicio";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                periodos.add(mapResultSetToPeriodo(rs));
            }
        }
        return periodos;
    }

    /**
     * <p>
     * Busca y recupera un periodo específico por su identificador único.
     * </p>
     * <pre>
     * PeriodoRepository repository = new PeriodoRepository();
     * Periodo periodo = repository.findById(2);
     * if (periodo != null) {
     *     System.out.println("Periodo encontrado: " + periodo.getNombre());
     * }
     * </pre>
     *
     * @param idPeriodo Identificador único del periodo (mayor a 0)
     * @return Objeto Periodo si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idPeriodo es menor o igual a 0
     * @see #findAll()
     * @see #findByNombre(String)
     */
    public Periodo findById(int idPeriodo) throws SQLException {
        if (idPeriodo <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String sql = "SELECT idPeriodo, nombre_periodo, hora_inicio, hora_fin, descripcion FROM Periodo WHERE idPeriodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPeriodo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPeriodo(rs);
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * Busca un periodo por su nombre.
     * </p>
     * <pre>
     * PeriodoRepository repository = new PeriodoRepository();
     * Periodo periodo = repository.findByNombre("Mañana");
     * </pre>
     *
     * @param nombre Nombre del periodo a buscar
     * @return Objeto Periodo si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si nombre es nulo o vacío
     * @see #findAll()
     * @see #findById(int)
     */
    public Periodo findByNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        String sql = "SELECT idPeriodo, nombre_periodo, hora_inicio, hora_fin, descripcion FROM Periodo WHERE nombre_periodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPeriodo(rs);
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * Obtiene el periodo actual según la hora del sistema.
     * </p>
     * <pre>
     * PeriodoRepository repository = new PeriodoRepository();
     * Periodo actual = repository.findPeriodoActual();
     * </pre>
     *
     * @return Periodo actual si existe, null si no hay periodo para la hora actual
     * @throws SQLException si ocurre un error en la consulta
     */
    public Periodo findPeriodoActual() throws SQLException {
        LocalTime ahora = LocalTime.now();
        String sql = "SELECT idPeriodo, nombre_periodo, hora_inicio, hora_fin, descripcion FROM Periodo WHERE hora_inicio <= ? AND hora_fin >= ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, Time.valueOf(ahora));
            stmt.setTime(2, Time.valueOf(ahora));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPeriodo(rs);
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * Persiste un nuevo periodo en la base de datos.
     * </p>
     * <ul>
     *   <li>Periodo no puede ser nulo</li>
     *   <li>Nombre es obligatorio</li>
     *   <li>Hora de inicio y fin son obligatorias</li>
     * </ul>
     * <pre>
     * Periodo nuevo = new Periodo();
     * nuevo.setNombre("Noche");
     * nuevo.setHoraInicio(LocalTime.of(18, 0));
     * nuevo.setHoraFin(LocalTime.of(23, 59));
     * PeriodoRepository repository = new PeriodoRepository();
     * int idGenerado = repository.save(nuevo);
     * </pre>
     *
     * @param periodo Objeto Periodo con los datos a persistir (sin ID)
     * @return ID generado automáticamente por la base de datos
     * @throws SQLException si ocurre un error en la inserción
     * @throws IllegalArgumentException si los datos son inválidos o incompletos
     * @see #update(Periodo)
     */
    public int save(Periodo periodo) throws SQLException {
        if (periodo == null || periodo.getNombre() == null || periodo.getNombre().trim().isEmpty()
                || periodo.getHoraInicio() == null || periodo.getHoraFin() == null)
            throw new IllegalArgumentException("Datos de periodo incompletos");
        String sql = "INSERT INTO Periodo (nombre_periodo, hora_inicio, hora_fin, descripcion) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, periodo.getNombre().trim());
            stmt.setTime(2, Time.valueOf(periodo.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(periodo.getHoraFin()));
            stmt.setString(4, periodo.getDescripcion());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo insertar el periodo");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Error al crear periodo, no se obtuvo ID");
    }

    /**
     * <p>
     * Actualiza los datos de un periodo existente.
     * </p>
     * <pre>
     * Periodo periodo = repository.findById(3);
     * periodo.setNombre("Tarde Extendida");
     * boolean actualizado = repository.update(periodo);
     * </pre>
     *
     * @param periodo Objeto Periodo con los datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró el periodo
     * @throws SQLException si ocurre un error en la actualización
     * @throws IllegalArgumentException si periodo es nulo o ID inválido
     * @see #save(Periodo)
     * @see #findById(int)
     */
    public boolean update(Periodo periodo) throws SQLException {
        if (periodo == null || periodo.getIdPeriodo() <= 0)
            throw new IllegalArgumentException("Periodo debe tener ID válido");
        String sql = "UPDATE Periodo SET nombre_periodo = ?, hora_inicio = ?, hora_fin = ?, descripcion = ? WHERE idPeriodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, periodo.getNombre().trim());
            stmt.setTime(2, Time.valueOf(periodo.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(periodo.getHoraFin()));
            stmt.setString(4, periodo.getDescripcion());
            stmt.setInt(5, periodo.getIdPeriodo());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Elimina un periodo específico del sistema utilizando su identificador único.
     * </p>
     * <pre>
     * boolean eliminado = repository.delete(4);
     * </pre>
     *
     * @param idPeriodo Identificador único del periodo a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró el periodo
     * @throws SQLException si ocurre un error en la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean delete(int idPeriodo) throws SQLException {
        if (idPeriodo <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String sql = "DELETE FROM Periodo WHERE idPeriodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPeriodo);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Verifica si existe un periodo con el mismo nombre.
     * </p>
     * <pre>
     * boolean existe = repository.nombreExists("Mañana");
     * </pre>
     *
     * @param nombrePeriodo Nombre del periodo a verificar
     * @return true si ya existe un periodo con ese nombre, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si nombrePeriodo es nulo o vacío
     */
    public boolean nombreExists(String nombrePeriodo) throws SQLException {
        if (nombrePeriodo == null || nombrePeriodo.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        String sql = "SELECT COUNT(*) FROM Periodo WHERE nombre_periodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombrePeriodo.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Obtiene el número total de periodos registrados en el sistema.
     * </p>
     * <pre>
     * int total = repository.count();
     * </pre>
     *
     * @return Número total de periodos
     * @throws SQLException si ocurre un error en la consulta
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Periodo";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * <p>
     * Mapea un ResultSet a un objeto Periodo.
     * </p>
     *
     * @param rs ResultSet con los datos del periodo
     * @return Objeto Periodo mapeado
     * @throws SQLException Si hay error al acceder a los datos
     */
    private Periodo mapResultSetToPeriodo(ResultSet rs) throws SQLException {
        return new Periodo(
                rs.getInt("idPeriodo"),
                rs.getString("nombre_periodo"),
                rs.getTime("hora_inicio").toLocalTime(),
                rs.getTime("hora_fin").toLocalTime(),
                rs.getString("descripcion")
        );
    }
}