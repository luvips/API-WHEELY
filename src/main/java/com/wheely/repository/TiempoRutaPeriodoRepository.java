package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.TiempoRutaPeriodo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Repositorio para la gestión de operaciones CRUD de la entidad TiempoRutaPeriodo en el sistema WHEELY.
 * Maneja todas las interacciones con la base de datos MySQL para la tabla TiempoRutaPeriodo,
 * permitiendo la administración de los tiempos promedio de rutas por periodo.
 * </p>
 * <ul>
 *   <li>Operaciones CRUD completas para tiempos ruta-periodo</li>
 *   <li>Búsqueda por ruta, periodo y combinación ruta-periodo</li>
 *   <li>Eliminación masiva por ruta</li>
 *   <li>Validación de existencia de combinación ruta-periodo</li>
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
 * @see com.wheely.model.TiempoRutaPeriodo
 * @see com.wheely.config.DatabaseConfig
 */
public class TiempoRutaPeriodoRepository {

    /**
     * <p>
     * Obtiene todos los tiempos de ruta por periodo registrados en el sistema, ordenados por ruta y periodo.
     * </p>
     * <pre>
     * TiempoRutaPeriodoRepository repository = new TiempoRutaPeriodoRepository();
     * List&lt;TiempoRutaPeriodo&gt; tiempos = repository.findAll();
     * for (TiempoRutaPeriodo t : tiempos) {
     *     System.out.println("Ruta: " + t.getIdRuta() + " - Periodo: " + t.getIdPeriodo() + " - Tiempo: " + t.getTiempoPromedio());
     * }
     * </pre>
     *
     * @return Lista de objetos TiempoRutaPeriodo, vacía si no hay registros.
     * @throws SQLException si ocurre un error en la consulta SQL o conexión a la base de datos.
     * @see #findById(int)
     * @see #findByRuta(int)
     * @see #findByPeriodo(int)
     */
    public List<TiempoRutaPeriodo> findAll() throws SQLException {
        List<TiempoRutaPeriodo> tiempos = new ArrayList<>();
        String sql = "SELECT idTiempoRutaPeriodo, idRuta, idPeriodo, tiempo_promedio FROM TiempoRutaPeriodo ORDER BY idRuta, idPeriodo";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tiempos.add(mapResultSetToTiempoRutaPeriodo(rs));
            }
        }
        return tiempos;
    }

    /**
     * <p>
     * Busca un tiempo ruta-periodo por su identificador único.
     * </p>
     * <pre>
     * TiempoRutaPeriodoRepository repository = new TiempoRutaPeriodoRepository();
     * TiempoRutaPeriodo tiempo = repository.findById(5);
     * </pre>
     *
     * @param id ID del tiempo a buscar (mayor a 0)
     * @return TiempoRutaPeriodo encontrado o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el ID es menor o igual a 0
     * @see #findAll()
     */
    public TiempoRutaPeriodo findById(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String sql = "SELECT idTiempoRutaPeriodo, idRuta, idPeriodo, tiempo_promedio FROM TiempoRutaPeriodo WHERE idTiempoRutaPeriodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTiempoRutaPeriodo(rs);
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * Busca todos los tiempos asociados a una ruta específica.
     * </p>
     * <pre>
     * List&lt;TiempoRutaPeriodo&gt; tiemposRuta = repository.findByRuta(2);
     * </pre>
     *
     * @param idRuta ID de la ruta (mayor a 0)
     * @return Lista de tiempos para la ruta especificada
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el ID de ruta es menor o igual a 0
     * @see #findAll()
     * @see #findByPeriodo(int)
     */
    public List<TiempoRutaPeriodo> findByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) throw new IllegalArgumentException("El ID de ruta debe ser mayor a 0");
        List<TiempoRutaPeriodo> tiempos = new ArrayList<>();
        String sql = "SELECT idTiempoRutaPeriodo, idRuta, idPeriodo, tiempo_promedio FROM TiempoRutaPeriodo WHERE idRuta = ? ORDER BY idPeriodo";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRuta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tiempos.add(mapResultSetToTiempoRutaPeriodo(rs));
                }
            }
        }
        return tiempos;
    }

    /**
     * <p>
     * Busca todos los tiempos asociados a un periodo específico.
     * </p>
     * <pre>
     * List&lt;TiempoRutaPeriodo&gt; tiemposPeriodo = repository.findByPeriodo(3);
     * </pre>
     *
     * @param idPeriodo ID del periodo (mayor a 0)
     * @return Lista de tiempos para el periodo especificado
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el ID de periodo es menor o igual a 0
     * @see #findAll()
     * @see #findByRuta(int)
     */
    public List<TiempoRutaPeriodo> findByPeriodo(int idPeriodo) throws SQLException {
        if (idPeriodo <= 0) throw new IllegalArgumentException("El ID de periodo debe ser mayor a 0");
        List<TiempoRutaPeriodo> tiempos = new ArrayList<>();
        String sql = "SELECT idTiempoRutaPeriodo, idRuta, idPeriodo, tiempo_promedio FROM TiempoRutaPeriodo WHERE idPeriodo = ? ORDER BY idRuta";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPeriodo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tiempos.add(mapResultSetToTiempoRutaPeriodo(rs));
                }
            }
        }
        return tiempos;
    }

    /**
     * <p>
     * Busca el tiempo específico para una combinación ruta-periodo.
     * </p>
     * <pre>
     * TiempoRutaPeriodo tiempo = repository.findByRutaAndPeriodo(2, 3);
     * </pre>
     *
     * @param idRuta ID de la ruta (mayor a 0)
     * @param idPeriodo ID del periodo (mayor a 0)
     * @return TiempoRutaPeriodo encontrado o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si los IDs son inválidos
     * @see #existsRutaPeriodo(int, int)
     */
    public TiempoRutaPeriodo findByRutaAndPeriodo(int idRuta, int idPeriodo) throws SQLException {
        if (idRuta <= 0 || idPeriodo <= 0)
            throw new IllegalArgumentException("IDs deben ser mayores a 0");
        String sql = "SELECT idTiempoRutaPeriodo, idRuta, idPeriodo, tiempo_promedio FROM TiempoRutaPeriodo WHERE idRuta = ? AND idPeriodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRuta);
            stmt.setInt(2, idPeriodo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTiempoRutaPeriodo(rs);
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * Persiste un nuevo tiempo ruta-periodo en la base de datos.
     * </p>
     * <pre>
     * TiempoRutaPeriodo nuevo = new TiempoRutaPeriodo(0, 1, 2, 35);
     * int idGenerado = repository.save(nuevo);
     * </pre>
     *
     * @param tiempoRutaPeriodo Objeto TiempoRutaPeriodo con los datos a persistir (sin ID)
     * @return ID generado automáticamente por la base de datos
     * @throws SQLException si ocurre un error en la inserción
     * @throws IllegalArgumentException si los datos son inválidos o incompletos
     * @see #update(TiempoRutaPeriodo)
     */
    public int save(TiempoRutaPeriodo tiempoRutaPeriodo) throws SQLException {
        if (tiempoRutaPeriodo == null || tiempoRutaPeriodo.getIdRuta() <= 0 || tiempoRutaPeriodo.getIdPeriodo() <= 0)
            throw new IllegalArgumentException("Datos de tiempo ruta-periodo incompletos");
        String sql = "INSERT INTO TiempoRutaPeriodo (idRuta, idPeriodo, tiempo_promedio) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, tiempoRutaPeriodo.getIdRuta());
            stmt.setInt(2, tiempoRutaPeriodo.getIdPeriodo());
            stmt.setInt(3, tiempoRutaPeriodo.getTiempoPromedio());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo insertar el tiempo ruta-periodo");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Error al crear tiempo ruta-periodo, no se obtuvo ID");
    }

    /**
     * <p>
     * Actualiza los datos de un tiempo ruta-periodo existente.
     * </p>
     * <pre>
     * TiempoRutaPeriodo tiempo = repository.findById(3);
     * tiempo.setTiempoPromedio(40);
     * boolean actualizado = repository.update(tiempo);
     * </pre>
     *
     * @param tiempoRutaPeriodo Objeto TiempoRutaPeriodo con los datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró el registro
     * @throws SQLException si ocurre un error en la actualización
     * @throws IllegalArgumentException si el objeto es nulo o el ID inválido
     * @see #save(TiempoRutaPeriodo)
     * @see #findById(int)
     */
    public boolean update(TiempoRutaPeriodo tiempoRutaPeriodo) throws SQLException {
        if (tiempoRutaPeriodo == null || tiempoRutaPeriodo.getIdTiempoRutaPeriodo() <= 0)
            throw new IllegalArgumentException("TiempoRutaPeriodo debe tener ID válido");
        String sql = "UPDATE TiempoRutaPeriodo SET idRuta = ?, idPeriodo = ?, tiempo_promedio = ? WHERE idTiempoRutaPeriodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tiempoRutaPeriodo.getIdRuta());
            stmt.setInt(2, tiempoRutaPeriodo.getIdPeriodo());
            stmt.setInt(3, tiempoRutaPeriodo.getTiempoPromedio());
            stmt.setInt(4, tiempoRutaPeriodo.getIdTiempoRutaPeriodo());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Elimina un tiempo ruta-periodo específico por su identificador único.
     * </p>
     * <pre>
     * boolean eliminado = repository.delete(4);
     * </pre>
     *
     * @param id ID del tiempo a eliminar (mayor a 0)
     * @return true si la eliminación fue exitosa, false si no se encontró el registro
     * @throws SQLException si ocurre un error en la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean delete(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String sql = "DELETE FROM TiempoRutaPeriodo WHERE idTiempoRutaPeriodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Elimina todos los tiempos asociados a una ruta específica.
     * </p>
     * <pre>
     * boolean eliminados = repository.deleteByRuta(2);
     * </pre>
     *
     * @param idRuta ID de la ruta (mayor a 0)
     * @return true si se eliminaron registros, false si no había registros
     * @throws SQLException si ocurre un error en la eliminación
     * @throws IllegalArgumentException si el ID de ruta es inválido
     */
    public boolean deleteByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) throw new IllegalArgumentException("El ID de ruta debe ser mayor a 0");
        String sql = "DELETE FROM TiempoRutaPeriodo WHERE idRuta = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRuta);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Verifica si existe una combinación ruta-periodo en la base de datos.
     * </p>
     * <pre>
     * boolean existe = repository.existsRutaPeriodo(2, 3);
     * </pre>
     *
     * @param idRuta ID de la ruta (mayor a 0)
     * @param idPeriodo ID del periodo (mayor a 0)
     * @return true si existe la combinación, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si los IDs son inválidos
     */
    public boolean existsRutaPeriodo(int idRuta, int idPeriodo) throws SQLException {
        if (idRuta <= 0 || idPeriodo <= 0)
            throw new IllegalArgumentException("IDs deben ser mayores a 0");
        String sql = "SELECT COUNT(*) FROM TiempoRutaPeriodo WHERE idRuta = ? AND idPeriodo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRuta);
            stmt.setInt(2, idPeriodo);
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
     * Mapea un ResultSet a un objeto TiempoRutaPeriodo.
     * </p>
     *
     * @param rs ResultSet con los datos del tiempo
     * @return Objeto TiempoRutaPeriodo mapeado
     * @throws SQLException Si hay error al acceder a los datos
     */
    private TiempoRutaPeriodo mapResultSetToTiempoRutaPeriodo(ResultSet rs) throws SQLException {
        return new TiempoRutaPeriodo(
                rs.getInt("idTiempoRutaPeriodo"),
                rs.getInt("idRuta"),
                rs.getInt("idPeriodo"),
                rs.getInt("tiempo_promedio")
        );
    }
}