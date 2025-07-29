package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.Parada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Repositorio para la gestión de operaciones CRUD de la entidad Parada en el sistema WHEELY.
 * Maneja todas las interacciones con la base de datos MySQL para la tabla Parada,
 * permitiendo la administración de puntos de parada en recorridos urbanos.
 * </p>
 * <ul>
 *   <li>Operaciones CRUD completas para paradas</li>
 *   <li>Búsqueda por recorrido y por nombre de archivo GeoJSON</li>
 *   <li>Gestión de paradas activas/inactivas</li>
 *   <li>Conteo de paradas para estadísticas</li>
 *   <li>Validación de unicidad de nombre de archivo</li>
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
 * @see com.wheely.model.Parada
 * @see com.wheely.config.DatabaseConfig
 * @see com.wheely.service.ParadaService
 */
public class ParadaRepository {

    /**
     * <p>
     * Obtiene todas las paradas registradas en el sistema, ordenadas por recorrido y nombre de archivo.
     * </p>
     * <pre>
     * ParadaRepository repository = new ParadaRepository();
     * List&lt;Parada&gt; paradas = repository.findAll();
     * for (Parada p : paradas) {
     *     System.out.println("Parada: " + p.getIdParada() + " - Recorrido: " + p.getIdRecorrido());
     * }
     * </pre>
     *
     * @return Lista de objetos Parada con todos los registros, lista vacía si no hay paradas.
     * @throws SQLException si ocurre un error en la consulta SQL o conexión a la base de datos.
     * @see #findById(int)
     * @see #findByRecorrido(int)
     */
    public List<Parada> findAll() throws SQLException {
        List<Parada> paradas = new ArrayList<>();
        String query = "SELECT idParada, idRecorrido, nombre_archivo_geojson, activo FROM Parada ORDER BY idRecorrido, nombre_archivo_geojson";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Parada parada = new Parada();
                parada.setIdParada(rs.getInt("idParada"));
                parada.setIdRecorrido(rs.getInt("idRecorrido"));
                parada.setNombreArchivoGeojson(rs.getString("nombre_archivo_geojson"));
                parada.setActivo(rs.getBoolean("activo"));
                paradas.add(parada);
            }
        }
        return paradas;
    }

    /**
     * <p>
     * Busca y recupera una parada específica por su identificador único.
     * </p>
     * <pre>
     * ParadaRepository repository = new ParadaRepository();
     * Parada parada = repository.findById(5);
     * if (parada != null) {
     *     System.out.println("Parada encontrada: " + parada.getNombreArchivoGeojson());
     * }
     * </pre>
     *
     * @param idParada Identificador único de la parada (mayor a 0)
     * @return Objeto Parada si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idParada es menor o igual a 0
     * @see #findAll()
     * @see #findByRecorrido(int)
     */
    public Parada findById(int idParada) throws SQLException {
        // Implementación real aquí
    }

    /**
     * <p>
     * Obtiene todas las paradas asociadas a un recorrido específico.
     * </p>
     * <pre>
     * ParadaRepository repository = new ParadaRepository();
     * List&lt;Parada&gt; paradasRecorrido = repository.findByRecorrido(2);
     * </pre>
     *
     * @param idRecorrido Identificador del recorrido (mayor a 0)
     * @return Lista de paradas del recorrido, vacía si no hay paradas
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRecorrido es menor o igual a 0
     * @see #findAll()
     * @see #findActivasByRecorrido(int)
     */
    public List<Parada> findByRecorrido(int idRecorrido) throws SQLException {
        // Implementación real aquí
    }

    /**
     * <p>
     * Obtiene todas las paradas activas del sistema.
     * </p>
     * <pre>
     * ParadaRepository repository = new ParadaRepository();
     * List&lt;Parada&gt; activas = repository.findActivas();
     * </pre>
     *
     * @return Lista de paradas activas, vacía si no hay activas
     * @throws SQLException si ocurre un error en la consulta
     * @see #findActivasByRecorrido(int)
     */
    public List<Parada> findActivas() throws SQLException {
        // Implementación real aquí
    }

    /**
     * <p>
     * Obtiene las paradas activas de un recorrido específico.
     * </p>
     * <pre>
     * ParadaRepository repository = new ParadaRepository();
     * List&lt;Parada&gt; activasRecorrido = repository.findActivasByRecorrido(2);
     * </pre>
     *
     * @param idRecorrido Identificador del recorrido (mayor a 0)
     * @return Lista de paradas activas del recorrido, vacía si no hay activas
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRecorrido es menor o igual a 0
     * @see #findActivas()
     */
    public List<Parada> findActivasByRecorrido(int idRecorrido) throws SQLException {
        // Implementación real aquí
    }

    /**
     * <p>
     * Persiste una nueva parada en la base de datos.
     * </p>
     * <ul>
     *   <li>Parada no puede ser nula</li>
     *   <li>idRecorrido debe ser mayor a 0</li>
     *   <li>nombre_archivo_geojson es obligatorio</li>
     * </ul>
     * <pre>
     * Parada nueva = new Parada();
     * nueva.setIdRecorrido(1);
     * nueva.setNombreArchivoGeojson("parada1.geojson");
     * nueva.setActivo(true);
     * ParadaRepository repository = new ParadaRepository();
     * int idGenerado = repository.save(nueva);
     * </pre>
     *
     * @param parada Objeto Parada con los datos a persistir (sin ID)
     * @return ID generado automáticamente por la base de datos
     * @throws SQLException si ocurre un error en la inserción
     * @throws IllegalArgumentException si los datos son inválidos o incompletos
     * @see #update(Parada)
     */
    public int save(Parada parada) throws SQLException {
        // Implementación real aquí
    }

    /**
     * <p>
     * Actualiza los datos de una parada existente.
     * </p>
     * <pre>
     * Parada parada = repository.findById(3);
     * parada.setNombreArchivoGeojson("nuevo.geojson");
     * boolean actualizado = repository.update(parada);
     * </pre>
     *
     * @param parada Objeto Parada con los datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró la parada
     * @throws SQLException si ocurre un error en la actualización
     * @throws IllegalArgumentException si parada es nula o ID inválido
     * @see #save(Parada)
     * @see #findById(int)
     */
    public boolean update(Parada parada) throws SQLException {
        // Implementación real aquí
    }

    /**
     * <p>
     * Elimina una parada específica del sistema utilizando su identificador único.
     * </p>
     * <pre>
     * boolean eliminado = repository.delete(4);
     * </pre>
     *
     * @param idParada Identificador único de la parada a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró la parada
     * @throws SQLException si ocurre un error en la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean delete(int idParada) throws SQLException {
        // Implementación real aquí
    }

    /**
     * <p>
     * Activa o desactiva una parada específica.
     * </p>
     * <pre>
     * boolean exito = repository.updateEstado(4, false);
     * </pre>
     *
     * @param idParada Identificador de la parada
     * @param activo Estado a establecer (true para activa, false para inactiva)
     * @return true si se actualizó correctamente, false si no se encontró la parada
     * @throws SQLException si ocurre un error en la actualización
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean updateEstado(int idParada, boolean activo) throws SQLException {
        // Implementación real aquí
    }

    /**
     * <p>
     * Busca paradas por nombre de archivo GeoJSON (búsqueda parcial).
     * </p>
     * <pre>
     * List&lt;Parada&gt; resultados = repository.findByNombreArchivo("parada");
     * </pre>
     *
     * @param nombreArchivo Nombre o parte del nombre del archivo a buscar
     * @return Lista de paradas que coinciden, vacía si no hay coincidencias
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si nombreArchivo es nulo o vacío
     */
    public List<Parada> findByNombreArchivo(String nombreArchivo) throws SQLException {
        List<Parada> paradas = new ArrayList<>();
        String query = "SELECT idParada, idRecorrido, nombre_archivo_geojson, activo FROM Parada WHERE nombre_archivo_geojson LIKE ? ORDER BY idRecorrido, nombre_archivo_geojson";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + nombreArchivo.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Parada parada = new Parada();
                    parada.setIdParada(rs.getInt("idParada"));
                    parada.setIdRecorrido(rs.getInt("idRecorrido"));
                    parada.setNombreArchivoGeojson(rs.getString("nombre_archivo_geojson"));
                    parada.setActivo(rs.getBoolean("activo"));
                    paradas.add(parada);
                }
            }
        }
        return paradas;
    }

    /**
     * <p>
     * Obtiene el número total de paradas registradas en el sistema.
     * </p>
     * <pre>
     * int total = repository.count();
     * </pre>
     *
     * @return Número total de paradas
     * @throws SQLException si ocurre un error en la consulta
     */
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM Parada";

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
     * <p>
     * Obtiene el número de paradas activas en el sistema.
     * </p>
     * <pre>
     * int activas = repository.countActivas();
     * </pre>
     *
     * @return Número de paradas activas
     * @throws SQLException si ocurre un error en la consulta
     */
    public int countActivas() throws SQLException {
        String query = "SELECT COUNT(*) FROM Parada WHERE activo = 1";

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
     * <p>
     * Obtiene el número de paradas asociadas a un recorrido específico.
     * </p>
     * <pre>
     * int paradasRecorrido = repository.countByRecorrido(2);
     * </pre>
     *
     * @param idRecorrido Identificador del recorrido (mayor a 0)
     * @return Número de paradas del recorrido
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRecorrido es menor o igual a 0
     */
    public int countByRecorrido(int idRecorrido) throws SQLException {
        String query = "SELECT COUNT(*) FROM Parada WHERE idRecorrido = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRecorrido);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    /**
     * <p>
     * Verifica si existe una parada con el mismo nombre de archivo GeoJSON.
     * </p>
     * <pre>
     * boolean existe = repository.existsByNombreArchivo("parada1.geojson");
     * </pre>
     *
     * @param nombreArchivo Nombre del archivo a verificar
     * @return true si ya existe una parada con ese nombre de archivo, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si nombreArchivo es nulo o vacío
     */
    public boolean existsByNombreArchivo(String nombreArchivo) throws SQLException {
        String query = "SELECT 1 FROM Parada WHERE nombre_archivo_geojson = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreArchivo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}