package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.CoordenadaParada;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Repositorio para la gestión de operaciones CRUD de la entidad CoordenadaParada en el sistema WHEELY.
 * Maneja todas las interacciones con la base de datos MySQL para la tabla CoordenadaParada,
 * permitiendo la administración de los puntos geográficos de las paradas.
 * </p>
 * <ul>
 *   <li>Operaciones CRUD completas para coordenadas de parada</li>
 *   <li>Búsqueda por parada, rango geográfico y por ID</li>
 *   <li>Conteo de coordenadas por parada y total</li>
 *   <li>Validación de duplicidad de coordenada en parada</li>
 *   <li>Reordenamiento de puntos en parada</li>
 *   <li>Búsqueda de coordenadas cercanas usando Haversine</li>
 *   <li>Estadísticas geográficas</li>
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
 * @see com.wheely.model.CoordenadaParada
 * @see com.wheely.config.DatabaseConfig
 */
public class CoordenadaParadaRepository {

    public List<CoordenadaParada> findAll() throws SQLException {
        List<CoordenadaParada> coordenadasParada = new ArrayList<>();
        String query = "SELECT idCoordenadaParada, idParada, latitud, longitud, orden_parada FROM CoordenadaParada ORDER BY idParada, orden_parada";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                coordenadasParada.add(mapResultSetToCoordenadaParada(rs));
            }
        }
        return coordenadasParada;
    }

    public CoordenadaParada findById(int idCoordenadaParada) throws SQLException {
        if (idCoordenadaParada <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String query = "SELECT idCoordenadaParada, idParada, latitud, longitud, orden_parada FROM CoordenadaParada WHERE idCoordenadaParada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCoordenadaParada);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoordenadaParada(rs);
                }
            }
        }
        return null;
    }

    public List<CoordenadaParada> findByParada(int idParada) throws SQLException {
        if (idParada <= 0) throw new IllegalArgumentException("El ID de parada debe ser mayor a 0");
        List<CoordenadaParada> coordenadasParada = new ArrayList<>();
        String query = "SELECT idCoordenadaParada, idParada, latitud, longitud, orden_parada FROM CoordenadaParada WHERE idParada = ? ORDER BY orden_parada";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idParada);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    coordenadasParada.add(mapResultSetToCoordenadaParada(rs));
                }
            }
        }
        return coordenadasParada;
    }

    public List<CoordenadaParada> findByRangoGeografico(BigDecimal latitudMin, BigDecimal latitudMax,
                                                        BigDecimal longitudMin, BigDecimal longitudMax) throws SQLException {
        List<CoordenadaParada> coordenadasParada = new ArrayList<>();
        String query = "SELECT idCoordenadaParada, idParada, latitud, longitud, orden_parada FROM CoordenadaParada " +
                "WHERE latitud BETWEEN ? AND ? AND longitud BETWEEN ? AND ? ORDER BY idParada, orden_parada";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, latitudMin);
            stmt.setBigDecimal(2, latitudMax);
            stmt.setBigDecimal(3, longitudMin);
            stmt.setBigDecimal(4, longitudMax);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    coordenadasParada.add(mapResultSetToCoordenadaParada(rs));
                }
            }
        }
        return coordenadasParada;
    }

    public int save(CoordenadaParada coordenadaParada) throws SQLException {
        if (coordenadaParada == null || coordenadaParada.getIdParada() <= 0 || coordenadaParada.getLatitud() == null || coordenadaParada.getLongitud() == null)
            throw new IllegalArgumentException("Datos de coordenada de parada incompletos");
        String query = "INSERT INTO CoordenadaParada (idParada, latitud, longitud, orden_parada) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, coordenadaParada.getIdParada());
            stmt.setBigDecimal(2, coordenadaParada.getLatitud());
            stmt.setBigDecimal(3, coordenadaParada.getLongitud());
            stmt.setInt(4, coordenadaParada.getOrdenParada());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo insertar la coordenada de parada");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Error al crear coordenada de parada, no se obtuvo ID");
    }

    public List<Integer> saveBatch(List<CoordenadaParada> coordenadasParada) throws SQLException {
        List<Integer> idsGenerados = new ArrayList<>();
        String query = "INSERT INTO CoordenadaParada (idParada, latitud, longitud, orden_parada) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                for (CoordenadaParada c : coordenadasParada) {
                    stmt.setInt(1, c.getIdParada());
                    stmt.setBigDecimal(2, c.getLatitud());
                    stmt.setBigDecimal(3, c.getLongitud());
                    stmt.setInt(4, c.getOrdenParada());
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

    public boolean update(CoordenadaParada coordenadaParada) throws SQLException {
        if (coordenadaParada == null || coordenadaParada.getIdCoordenadaParada() <= 0)
            throw new IllegalArgumentException("CoordenadaParada debe tener ID válido");
        String query = "UPDATE CoordenadaParada SET idParada = ?, latitud = ?, longitud = ?, orden_parada = ? WHERE idCoordenadaParada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, coordenadaParada.getIdParada());
            stmt.setBigDecimal(2, coordenadaParada.getLatitud());
            stmt.setBigDecimal(3, coordenadaParada.getLongitud());
            stmt.setInt(4, coordenadaParada.getOrdenParada());
            stmt.setInt(5, coordenadaParada.getIdCoordenadaParada());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int idCoordenadaParada) throws SQLException {
        if (idCoordenadaParada <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String query = "DELETE FROM CoordenadaParada WHERE idCoordenadaParada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCoordenadaParada);
            return stmt.executeUpdate() > 0;
        }
    }

    public int deleteByParada(int idParada) throws SQLException {
        if (idParada <= 0) throw new IllegalArgumentException("El ID de parada debe ser mayor a 0");
        String query = "DELETE FROM CoordenadaParada WHERE idParada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idParada);
            return stmt.executeUpdate();
        }
    }

    public int getNextOrdenParada(int idParada) throws SQLException {
        if (idParada <= 0) throw new IllegalArgumentException("El ID de parada debe ser mayor a 0");
        String query = "SELECT COALESCE(MAX(orden_parada), 0) + 1 FROM CoordenadaParada WHERE idParada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idParada);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 1;
    }

    public void reordenarCoordenadasParada(int idParada) throws SQLException {
        if (idParada <= 0) throw new IllegalArgumentException("El ID de parada debe ser mayor a 0");
        String selectQuery = "SELECT idCoordenadaParada FROM CoordenadaParada WHERE idParada = ? ORDER BY orden_parada";
        String updateQuery = "UPDATE CoordenadaParada SET orden_parada = ? WHERE idCoordenadaParada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, idParada);
            try (ResultSet rs = selectStmt.executeQuery()) {
                int orden = 1;
                while (rs.next()) {
                    int idCoordenadaParada = rs.getInt("idCoordenadaParada");
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, orden++);
                        updateStmt.setInt(2, idCoordenadaParada);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }

    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM CoordenadaParada";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countByParada(int idParada) throws SQLException {
        if (idParada <= 0) throw new IllegalArgumentException("El ID de parada debe ser mayor a 0");
        String query = "SELECT COUNT(*) FROM CoordenadaParada WHERE idParada = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idParada);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean existsCoordenadaDuplicada(int idParada, BigDecimal latitud, BigDecimal longitud) throws SQLException {
        String query = "SELECT 1 FROM CoordenadaParada WHERE idParada = ? AND latitud = ? AND longitud = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idParada);
            stmt.setBigDecimal(2, latitud);
            stmt.setBigDecimal(3, longitud);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<CoordenadaParada> findCercanas(BigDecimal latitud, BigDecimal longitud, double radioKm) throws SQLException {
        List<CoordenadaParada> coordenadasParada = new ArrayList<>();
        // Fórmula Haversine para distancia en km
        String query = "SELECT idCoordenadaParada, idParada, latitud, longitud, orden_parada, " +
                "(6371 * ACOS(COS(RADIANS(?)) * COS(RADIANS(latitud)) * COS(RADIANS(longitud) - RADIANS(?)) + SIN(RADIANS(?)) * SIN(RADIANS(latitud)))) AS distancia " +
                "FROM CoordenadaParada HAVING distancia <= ? ORDER BY distancia";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, latitud);
            stmt.setBigDecimal(2, longitud);
            stmt.setBigDecimal(3, latitud);
            stmt.setDouble(4, radioKm);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CoordenadaParada c = mapResultSetToCoordenadaParada(rs);
                    coordenadasParada.add(c);
                }
            }
        }
        return coordenadasParada;
    }

    public BigDecimal[] getEstadisticasGeograficas() throws SQLException {
        String query = "SELECT MIN(latitud) as lat_min, MAX(latitud) as lat_max, MIN(longitud) as lon_min, MAX(longitud) as lon_max FROM CoordenadaParada";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new BigDecimal[]{
                        rs.getBigDecimal("lat_min"),
                        rs.getBigDecimal("lat_max"),
                        rs.getBigDecimal("lon_min"),
                        rs.getBigDecimal("lon_max")
                };
            }
            return new BigDecimal[4];
        }
    }

    private CoordenadaParada mapResultSetToCoordenadaParada(ResultSet rs) throws SQLException {
        CoordenadaParada coordenadaParada = new CoordenadaParada();
        coordenadaParada.setIdCoordenadaParada(rs.getInt("idCoordenadaParada"));
        coordenadaParada.setIdParada(rs.getInt("idParada"));
        coordenadaParada.setLatitud(rs.getBigDecimal("latitud"));
        coordenadaParada.setLongitud(rs.getBigDecimal("longitud"));
        coordenadaParada.setOrdenParada(rs.getInt("orden_parada"));
        return coordenadaParada;
    }
}