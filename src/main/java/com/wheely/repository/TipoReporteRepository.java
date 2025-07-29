package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.TipoReporte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Repositorio para la gestión de operaciones CRUD de la entidad TipoReporte en el sistema WHEELY.
 * Maneja todas las interacciones con la base de datos MySQL para la tabla TipoReporte,
 * permitiendo la administración de los tipos de reporte disponibles en el sistema.
 * </p>
 * <ul>
 *   <li>Operaciones CRUD completas para tipos de reporte</li>
 *   <li>Búsqueda por nombre y por ID</li>
 *   <li>Validación de unicidad de nombre de tipo de reporte</li>
 *   <li>Conteo de tipos de reporte para estadísticas</li>
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
 * @see com.wheely.model.TipoReporte
 * @see com.wheely.config.DatabaseConfig
 */
public class TipoReporteRepository {

    /**
     * <p>
     * Obtiene todos los tipos de reporte registrados en el sistema, ordenados por nombre.
     * </p>
     * <pre>
     * TipoReporteRepository repository = new TipoReporteRepository();
     * List&lt;TipoReporte&gt; tipos = repository.findAll();
     * for (TipoReporte t : tipos) {
     *     System.out.println("Tipo: " + t.getNombreTipo());
     * }
     * </pre>
     *
     * @return Lista de objetos TipoReporte, vacía si no hay registros.
     * @throws SQLException si ocurre un error en la consulta SQL o conexión a la base de datos.
     * @see #findById(int)
     * @see #findByNombre(String)
     */
    public List<TipoReporte> findAll() throws SQLException {
        List<TipoReporte> tiposReporte = new ArrayList<>();
        String query = "SELECT idTipoReporte, nombre_tipo, descripcion FROM TipoReporte ORDER BY nombre_tipo";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tiposReporte.add(mapResultSetToTipoReporte(rs));
            }
        }
        return tiposReporte;
    }

    /**
     * <p>
     * Busca un tipo de reporte por su identificador único.
     * </p>
     * <pre>
     * TipoReporte tipo = repository.findById(2);
     * </pre>
     *
     * @param idTipoReporte Identificador único del tipo de reporte (mayor a 0)
     * @return TipoReporte encontrado o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el ID es menor o igual a 0
     * @see #findAll()
     */
    public TipoReporte findById(int idTipoReporte) throws SQLException {
        if (idTipoReporte <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String query = "SELECT idTipoReporte, nombre_tipo, descripcion FROM TipoReporte WHERE idTipoReporte = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idTipoReporte);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTipoReporte(rs);
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * Guarda un nuevo tipo de reporte en la base de datos.
     * </p>
     * <pre>
     * TipoReporte nuevo = new TipoReporte();
     * nuevo.setNombreTipo("Incidente");
     * nuevo.setDescripcion("Reporte de incidente");
     * int idGenerado = repository.save(nuevo);
     * </pre>
     *
     * @param tipoReporte TipoReporte con los datos a persistir (sin ID)
     * @return ID generado automáticamente por la base de datos
     * @throws SQLException si ocurre un error en la inserción
     * @throws IllegalArgumentException si los datos son inválidos o incompletos
     * @see #update(TipoReporte)
     */
    public int save(TipoReporte tipoReporte) throws SQLException {
        if (tipoReporte == null || tipoReporte.getNombreTipo() == null || tipoReporte.getNombreTipo().trim().isEmpty())
            throw new IllegalArgumentException("Datos de tipo de reporte incompletos");
        String query = "INSERT INTO TipoReporte (nombre_tipo, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tipoReporte.getNombreTipo().trim());
            stmt.setString(2, tipoReporte.getDescripcion());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo insertar el tipo de reporte");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Error al crear tipo de reporte, no se obtuvo ID");
    }

    /**
     * <p>
     * Actualiza los datos de un tipo de reporte existente.
     * </p>
     * <pre>
     * TipoReporte tipo = repository.findById(3);
     * tipo.setDescripcion("Nuevo texto");
     * boolean actualizado = repository.update(tipo);
     * </pre>
     *
     * @param tipoReporte TipoReporte con los datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró el registro
     * @throws SQLException si ocurre un error en la actualización
     * @throws IllegalArgumentException si el objeto es nulo o el ID inválido
     * @see #save(TipoReporte)
     * @see #findById(int)
     */
    public boolean update(TipoReporte tipoReporte) throws SQLException {
        if (tipoReporte == null || tipoReporte.getIdTipoReporte() <= 0)
            throw new IllegalArgumentException("TipoReporte debe tener ID válido");
        String query = "UPDATE TipoReporte SET nombre_tipo = ?, descripcion = ? WHERE idTipoReporte = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tipoReporte.getNombreTipo().trim());
            stmt.setString(2, tipoReporte.getDescripcion());
            stmt.setInt(3, tipoReporte.getIdTipoReporte());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Elimina un tipo de reporte específico por su identificador único.
     * </p>
     * <pre>
     * boolean eliminado = repository.delete(4);
     * </pre>
     *
     * @param idTipoReporte Identificador único del tipo de reporte a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró el registro
     * @throws SQLException si ocurre un error en la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean delete(int idTipoReporte) throws SQLException {
        if (idTipoReporte <= 0) throw new IllegalArgumentException("El ID debe ser mayor a 0");
        String query = "DELETE FROM TipoReporte WHERE idTipoReporte = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idTipoReporte);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * <p>
     * Busca tipos de reporte por nombre (búsqueda parcial).
     * </p>
     * <pre>
     * List&lt;TipoReporte&gt; resultados = repository.findByNombre("inci");
     * </pre>
     *
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de tipos de reporte que coinciden con la búsqueda
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     * @see #findAll()
     */
    public List<TipoReporte> findByNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        List<TipoReporte> tiposReporte = new ArrayList<>();
        String query = "SELECT idTipoReporte, nombre_tipo, descripcion FROM TipoReporte WHERE nombre_tipo LIKE ? ORDER BY nombre_tipo";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + nombre.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tiposReporte.add(mapResultSetToTipoReporte(rs));
                }
            }
        }
        return tiposReporte;
    }

    /**
     * <p>
     * Obtiene el número total de tipos de reporte registrados en el sistema.
     * </p>
     * <pre>
     * int total = repository.count();
     * </pre>
     *
     * @return Número total de tipos de reporte
     * @throws SQLException si ocurre un error en la consulta
     */
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM TipoReporte";
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
     * Verifica si existe un tipo de reporte con el mismo nombre.
     * </p>
     * <pre>
     * boolean existe = repository.existsByNombre("Incidente");
     * </pre>
     *
     * @param nombreTipo Nombre del tipo de reporte a verificar
     * @return true si ya existe un tipo de reporte con ese nombre, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public boolean existsByNombre(String nombreTipo) throws SQLException {
        if (nombreTipo == null || nombreTipo.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        String query = "SELECT COUNT(*) FROM TipoReporte WHERE nombre_tipo = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreTipo.trim());
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
     * Verifica si existe un tipo de reporte con el mismo nombre, excluyendo un ID específico.
     * Útil para validaciones en actualizaciones.
     * </p>
     * <pre>
     * boolean existe = repository.existsByNombreExcludingId("Incidente", 5);
     * </pre>
     *
     * @param nombreTipo Nombre del tipo de reporte a verificar
     * @param excludeId ID del tipo de reporte a excluir de la verificación
     * @return true si ya existe un tipo de reporte con ese nombre en otro registro, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el nombre es nulo o vacío o el ID es inválido
     */
    public boolean existsByNombreExcludingId(String nombreTipo, int excludeId) throws SQLException {
        if (nombreTipo == null || nombreTipo.trim().isEmpty() || excludeId <= 0)
            throw new IllegalArgumentException("Parámetros inválidos");
        String query = "SELECT COUNT(*) FROM TipoReporte WHERE nombre_tipo = ? AND idTipoReporte != ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreTipo.trim());
            stmt.setInt(2, excludeId);
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
     * Mapea un ResultSet a un objeto TipoReporte.
     * </p>
     *
     * @param rs ResultSet con los datos del tipo de reporte
     * @return Objeto TipoReporte mapeado
     * @throws SQLException Si hay error al acceder a los datos
     */
    private TipoReporte mapResultSetToTipoReporte(ResultSet rs) throws SQLException {
        TipoReporte tipoReporte = new TipoReporte();
        tipoReporte.setIdTipoReporte(rs.getInt("idTipoReporte"));
        tipoReporte.setNombreTipo(rs.getString("nombre_tipo"));
        tipoReporte.setDescripcion(rs.getString("descripcion"));
        return tipoReporte;
    }
}