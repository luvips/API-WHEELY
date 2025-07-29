package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.Coordenada;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Repositorio para la gestión de operaciones CRUD de la entidad Coordenada en el sistema WHEELY.
 * Maneja todas las interacciones con la base de datos MySQL para la tabla Coordenada,
 * permitiendo la administración de los puntos geográficos de los recorridos.
 * </p>
 * <ul>
 *   <li>Operaciones CRUD completas para coordenadas</li>
 *   <li>Búsqueda por recorrido, rango geográfico y por ID</li>
 *   <li>Conteo de coordenadas por recorrido y total</li>
 *   <li>Validación de duplicidad de coordenada en recorrido</li>
 *   <li>Reordenamiento de puntos en recorrido</li>
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
 * @see com.wheely.model.Coordenada
 * @see com.wheely.config.DatabaseConfig
 */
public class CoordenadaRepository {

    /**
     * <p>
     * Obtiene todas las coordenadas registradas en el sistema, ordenadas por recorrido y orden de punto.
     * </p>
     * <pre>
     * CoordenadaRepository repository = new CoordenadaRepository();
     * List&lt;Coordenada&gt; coordenadas = repository.findAll();
     * </pre>
     *
     * @return Lista de objetos Coordenada, vacía si no hay registros.
     * @throws SQLException si ocurre un error en la consulta SQL o conexión a la base de datos.
     * @see #findById(int)
     * @see #findByRecorrido(int)
     */
    public List<Coordenada> findAll() throws SQLException {
        List<Coordenada> coordenadas = new ArrayList<>();
        String query = "SELECT idCoordenada, idRecorrido, latitud, longitud, orden_punto FROM Coordenada ORDER BY idRecorrido, orden_punto";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                coordenadas.add(mapResultSetToCoordenada(rs));
            }
        }
        return coordenadas;
    }

    /**
     * <p>
     * Busca una coordenada por su identificador único.
     * </p>
     * <pre>
     * Coordenada coordenada = repository.findById(5);
     * </pre>
     *
     * @param idCoordenada Identificador único de la coordenada (mayor a 0)
     * @return Coordenada encontrada o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el ID es menor o igual a 0
     * @see #findAll()
     */
    public Coordenada findById(int idCoordenada) throws SQLException {
        if (idCoordenada <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String query = "SELECT idCoordenada, idRecorrido, latitud, longitud, orden_punto FROM Coordenada WHERE idCoordenada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCoordenada);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoordenada(rs);
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * Obtiene todas las coordenadas de un recorrido específico ordenadas por orden_punto.
     * </p>
     * <pre>
     * List&lt;Coordenada&gt; coordenadas = repository.findByRecorrido(2);
     * </pre>
     *
     * @param idRecorrido ID del recorrido (mayor a 0)
     * @return Lista de coordenadas del recorrido ordenadas
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el ID es menor o igual a 0
     * @see #findAll()
     */
    public List<Coordenada> findByRecorrido(int idRecorrido) throws SQLException {
        if (idRecorrido <= 0) throw new IllegalArgumentException("El ID de recorrido debe ser mayor a 0");
        List<Coordenada> coordenadas = new ArrayList<>();
        String query = "SELECT idCoordenada, idRecorrido, latitud, longitud, orden_punto FROM Coordenada WHERE idRecorrido = ? ORDER BY orden_punto";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRecorrido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    coordenadas.add(mapResultSetToCoordenada(rs));
                }
            }
        }
        return coordenadas;
    }

    /**
     * <p>
     * Obtiene coordenadas dentro de un rango geográfico (bounding box).
     * </p>
     * <pre>
     * List&lt;Coordenada&gt; coordenadas = repository.findByRangoGeografico(minLat, maxLat, minLon, maxLon);
     * </pre>
     *
     * @param latitudMin Latitud mínima
     * @param latitudMax Latitud máxima
     * @param longitudMin Longitud mínima
     * @param longitudMax Longitud máxima
     * @return Lista de coordenadas dentro del rango
     * @throws SQLException si ocurre un error en la consulta
     */
    public List<Coordenada> findByRangoGeografico(BigDecimal latitudMin, BigDecimal latitudMax,
                                                  BigDecimal longitudMin, BigDecimal longitudMax) throws SQLException {
        List<Coordenada> coordenadas = new ArrayList<>();
        String query = "SELECT idCoordenada, idRecorrido, latitud, longitud, orden_punto FROM Coordenada " +
                "WHERE latitud BETWEEN ? AND ? AND longitud BETWEEN ? AND ? ORDER BY idRecorrido, orden_punto";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, latitudMin);
            stmt.setBigDecimal(2, latitudMax);
            stmt.setBigDecimal(3, longitudMin);
            stmt.setBigDecimal(4, longitudMax);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    coordenadas.add(mapResultSetToCoordenada(rs));
                }
            }
        }
        return coordenadas;
    }

    /**
     * <p>
     * Guarda una nueva coordenada en la base de datos.
     * </p>
     * <pre>
     * Coordenada nueva = new Coordenada(...);
     * int idGenerado = repository.save(nueva);
     * </pre>
     *
     * @param coordenada Coordenada a guardar (sin ID)
     * @return ID de la coordenada creada
     * @throws SQLException si ocurre un error en la inserción
     * @throws IllegalArgumentException si los datos son inválidos o incompletos
     * @see #update(Coordenada)
     */
    public int save(Coordenada coordenada) throws SQLException {
        if (coordenada == null || coordenada.getIdRecorrido() <= 0 || coordenada.getLatitud() == null || coordenada.getLongitud() == null)
            throw new IllegalArgumentException("Datos de coordenada incompletos");
        String query = "INSERT INTO Coordenada (idRecorrido, latitud, longitud, orden_punto) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, coordenada.getIdRecorrido());
            stmt.setBigDecimal(2, coordenada.getLatitud());
            stmt.setBigDecimal(3, coordenada.getLongitud());
            stmt.setInt(4, coordenada.getOrdenPunto());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo insertar la coordenada");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Error al crear coordenada, no se obtuvo ID");
    }

    /**
     * <p>
     * Guarda múltiples coordenadas en batch para mejor rendimiento.
     * </p>
     * <pre>
     * List&lt;Coordenada&gt; lista = ...;
     * List&lt;Integer&gt; ids = repository.saveBatch(lista);
     * </pre>
     *
     * @param coordenadas Lista de coordenadas a guardar
     * @return Lista de IDs generados
     * @throws SQLException si ocurre un error en la inserción
     */
    public List<Integer> saveBatch(List<Coordenada> coordenadas) throws SQLException {
        List<Integer> idsGenerados = new ArrayList<>();
        String query = "INSERT INTO Coordenada (idRecorrido, latitud, longitud, orden_punto) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                for (Coordenada c : coordenadas) {
                    stmt.setInt(1, c.getIdRecorrido());
                    stmt.setBigDecimal(2, c.getLatitud());
                    stmt.setBigDecimal(3, c.getLongitud());
                    stmt.setInt(4, c.getOrdenPunto());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    while (keys.next()) {
                        idsGenerados.add(keys.getInt(1));
                    }
                }
            }
            conn.commit();
        }
        return idsGenerados;
    }

    /**
     * <p>
     * Actualiza una coordenada existente.
     * </p>
     * <pre>
     * Coordenada c = repository.findById(3);
     * c.setLatitud(new BigDecimal("19.4326"));
     * boolean actualizado = repository.update(c);
     * </pre>
     *
     * @param coordenada Coordenada con los datos actualizados (debe incluir ID válido)
     * @return true si se actualizó correctamente, false si no se encontró la coordenada
     * @throws SQLException si ocurre un error en la actualización
     * @throws IllegalArgumentException si el objeto es nulo o el ID inválido
     * @see #save(Coordenada)
     * @see #findById(int)
     */
    public boolean update(Coordenada coordenada) throws SQLException {
        if (coordenada == null || coordenada.getIdCoordenada() <= 0)
            throw new IllegalArgumentException("Coordenada debe tener ID válido");
        String query = "UPDATE Coordenada SET idRecorrido = ?, latitud = ?, longitud = ?, orden_punto = ? WHERE idCoordenada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, coordenada.getIdRecorrido());
            stmt.setBigDecimal(2, coordenada.getLatitud());
            stmt.setBigDecimal(3, coordenada.getLongitud());
            stmt.setInt(4, coordenada.getOrdenPunto());
            stmt.setInt(5, coordenada.getIdCoordenada());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Elimina una coordenada por su identificador único.
     * </p>
     * <pre>
     * boolean eliminado = repository.delete(4);
     * </pre>
     *
     * @param idCoordenada ID de la coordenada a eliminar
     * @return true si se eliminó correctamente, false si no se encontró la coordenada
     * @throws SQLException si ocurre un error en la eliminación
     */
    public boolean delete(int idCoordenada) throws SQLException {
        if (idCoordenada <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String query = "DELETE FROM Coordenada WHERE idCoordenada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCoordenada);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Elimina todas las coordenadas de un recorrido.
     * </p>
     * <pre>
     * int eliminadas = repository.deleteByRecorrido(2);
     * </pre>
     *
     * @param idRecorrido ID del recorrido
     * @return Número de coordenadas eliminadas
     * @throws SQLException si ocurre un error en la eliminación
     */
    public int deleteByRecorrido(int idRecorrido) throws SQLException {
        if (idRecorrido <= 0) throw new IllegalArgumentException("El ID de recorrido debe ser mayor a 0");
        String query = "DELETE FROM Coordenada WHERE idRecorrido = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRecorrido);
            return stmt.executeUpdate();
        }
    }

    /**
     * <p>
     * Obtiene el siguiente número de orden para un recorrido.
     * </p>
     * <pre>
     * int siguiente = repository.getNextOrdenPunto(2);
     * </pre>
     *
     * @param idRecorrido ID del recorrido
     * @return Siguiente número de orden disponible
     * @throws SQLException si ocurre un error en la consulta
     */
    public int getNextOrdenPunto(int idRecorrido) throws SQLException {
        if (idRecorrido <= 0) throw new IllegalArgumentException("El ID de recorrido debe ser mayor a 0");
        String query = "SELECT COALESCE(MAX(orden_punto), 0) + 1 FROM Coordenada WHERE idRecorrido = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRecorrido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 1;
    }

    /**
     * <p>
     * Reordena las coordenadas de un recorrido.
     * </p>
     * <pre>
     * repository.reordenarCoordenadas(2);
     * </pre>
     *
     * @param idRecorrido ID del recorrido
     * @throws SQLException si ocurre un error en la actualización
     */
    public void reordenarCoordenadas(int idRecorrido) throws SQLException {
        if (idRecorrido <= 0) throw new IllegalArgumentException("El ID de recorrido debe ser mayor a 0");
        String selectQuery = "SELECT idCoordenada FROM Coordenada WHERE idRecorrido = ? ORDER BY orden_punto";
        String updateQuery = "UPDATE Coordenada SET orden_punto = ? WHERE idCoordenada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, idRecorrido);
            try (ResultSet rs = selectStmt.executeQuery()) {
                int orden = 1;
                while (rs.next()) {
                    int idCoordenada = rs.getInt("idCoordenada");
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, orden++);
                        updateStmt.setInt(2, idCoordenada);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Obtiene el conteo total de coordenadas.
     * </p>
     * <pre>
     * int total = repository.count();
     * </pre>
     *
     * @return Número total de coordenadas
     * @throws SQLException si ocurre un error en la consulta
     */
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM Coordenada";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * <p>
     * Obtiene el conteo de coordenadas por recorrido.
     * </p>
     * <pre>
     * int total = repository.countByRecorrido(2);
     * </pre>
     *
     * @param idRecorrido ID del recorrido
     * @return Número de coordenadas del recorrido
     * @throws SQLException si ocurre un error en la consulta
     */
    public int countByRecorrido(int idRecorrido) throws SQLException {
        if (idRecorrido <= 0) throw new IllegalArgumentException("El ID de recorrido debe ser mayor a 0");
        String query = "SELECT COUNT(*) FROM Coordenada WHERE idRecorrido = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRecorrido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * <p>
     * Verifica si existe una coordenada duplicada en el mismo recorrido.
     * </p>
     * <pre>
     * boolean existe = repository.existsCoordenadaDuplicada(2, lat, lon);
     * </pre>
     *
     * @param idRecorrido ID del recorrido
     * @param latitud Latitud a verificar
     * @param longitud Longitud a verificar
     * @return true si ya existe una coordenada igual en el recorrido
     * @throws SQLException si ocurre un error en la consulta
     */
    public boolean existsCoordenadaDuplicada(int idRecorrido, BigDecimal latitud, BigDecimal longitud) throws SQLException {
        String query = "SELECT 1 FROM Coordenada WHERE idRecorrido = ? AND latitud = ? AND longitud = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRecorrido);
            stmt.setBigDecimal(2, latitud);
            stmt.setBigDecimal(3, longitud);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * <p>
     * Mapea un ResultSet a un objeto Coordenada.
     * </p>
     *
     * @param rs ResultSet con los datos de la coordenada
     * @return Objeto Coordenada mapeado
     * @throws SQLException Si hay error al acceder a los datos
     */
    private Coordenada mapResultSetToCoordenada(ResultSet rs) throws SQLException {
        Coordenada coordenada = new Coordenada();
        coordenada.setIdCoordenada(rs.getInt("idCoordenada"));
        coordenada.setIdRecorrido(rs.getInt("idRecorrido"));
        coordenada.setLatitud(rs.getBigDecimal("latitud"));
        coordenada.setLongitud(rs.getBigDecimal("longitud"));
        coordenada.setOrdenPunto(rs.getInt("orden_punto"));
        return coordenada;
    }
}