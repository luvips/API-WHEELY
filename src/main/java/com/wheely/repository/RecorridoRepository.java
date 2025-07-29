package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.Recorrido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de operaciones CRUD de la entidad Recorrido en el sistema Wheely.
 *
 * <p>Esta clase maneja todas las interacciones con la base de datos MySQL para la tabla Recorrido,
 * proporcionando métodos seguros para crear, leer, actualizar y eliminar recorridos de rutas
 * del sistema de transporte público. Permite la gestión de archivos GeoJSON y el estado de activación.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Operaciones CRUD completas para recorridos</li>
 *   <li>Búsqueda por ruta y por nombre de archivo</li>
 *   <li>Gestión de recorridos activos/inactivos</li>
 *   <li>Conteo de recorridos para estadísticas</li>
 *   <li>Validación de unicidad de nombre de archivo GeoJSON</li>
 * </ul>
 *
 * <p>Consideraciones de seguridad:</p>
 * <ul>
 *   <li>Validación de parámetros para evitar SQL injection</li>
 *   <li>Uso de PreparedStatements en todas las consultas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Recorrido
 * @see com.wheely.config.DatabaseConfig
 * @see com.wheely.service.RecorridoService
 */
public class RecorridoRepository {

    /**
     * Obtiene todos los recorridos registrados en el sistema, ordenados por ruta y nombre de archivo.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RecorridoRepository repository = new RecorridoRepository();
     * List&lt;Recorrido&gt; recorridos = repository.findAll();
     * for (Recorrido r : recorridos) {
     *     System.out.println("Recorrido: " + r.getIdRecorrido() + " - Ruta: " + r.getIdRuta());
     * }
     * </pre>
     *
     * @return Lista de objetos Recorrido con todos los registros, lista vacía si no hay recorridos.
     * @throws SQLException si ocurre un error en la consulta SQL o conexión a la base de datos.
     * @see #findById(int)
     * @see #findByRuta(int)
     */
    public List<Recorrido> findAll() throws SQLException {
        List<Recorrido> recorridos = new ArrayList<>();
        String query = "SELECT idRecorrido, idRuta, nombre_archivo_geojson, activo FROM Recorrido ORDER BY idRuta, nombre_archivo_geojson";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Recorrido recorrido = new Recorrido();
                recorrido.setIdRecorrido(rs.getInt("idRecorrido"));
                recorrido.setIdRuta(rs.getInt("idRuta"));
                recorrido.setNombreArchivoGeojson(rs.getString("nombre_archivo_geojson"));
                recorrido.setActivo(rs.getBoolean("activo"));
                recorridos.add(recorrido);
            }
        }
        return recorridos;
    }

    /**
     * Busca y recupera un recorrido específico por su identificador único.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RecorridoRepository repository = new RecorridoRepository();
     * Recorrido recorrido = repository.findById(5);
     * if (recorrido != null) {
     *     System.out.println("Recorrido encontrado: " + recorrido.getNombreArchivoGeojson());
     * } else {
     *     System.out.println("Recorrido no encontrado con ID: 5");
     * }
     * </pre>
     *
     * @param idRecorrido Identificador único del recorrido (mayor a 0)
     * @return Objeto Recorrido si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRecorrido es menor o igual a 0
     * @see #findAll()
     * @see #findByRuta(int)
     */
    public Recorrido findById(int idRecorrido) throws SQLException {
        if (idRecorrido <= 0) {
            throw new IllegalArgumentException("El ID del recorrido debe ser mayor a 0");
        }
        Recorrido recorrido = null;
        String query = "SELECT idRecorrido, idRuta, nombre_archivo_geojson, activo FROM Recorrido WHERE idRecorrido = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRecorrido);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    recorrido = new Recorrido();
                    recorrido.setIdRecorrido(rs.getInt("idRecorrido"));
                    recorrido.setIdRuta(rs.getInt("idRuta"));
                    recorrido.setNombreArchivoGeojson(rs.getString("nombre_archivo_geojson"));
                    recorrido.setActivo(rs.getBoolean("activo"));
                }
            }
        }
        return recorrido;
    }

    /**
     * Obtiene todos los recorridos asociados a una ruta específica.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RecorridoRepository repository = new RecorridoRepository();
     * List&lt;Recorrido&gt; recorridosRuta = repository.findByRuta(2);
     * for (Recorrido r : recorridosRuta) {
     *     System.out.println("Recorrido: " + r.getNombreArchivoGeojson());
     * }
     * </pre>
     *
     * @param idRuta Identificador de la ruta (mayor a 0)
     * @return Lista de recorridos de la ruta, vacía si no hay recorridos
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRuta es menor o igual a 0
     * @see #findAll()
     * @see #findActivosByRuta(int)
     */
    public List<Recorrido> findByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }
        List<Recorrido> recorridos = new ArrayList<>();
        String query = "SELECT idRecorrido, idRuta, nombre_archivo_geojson, activo FROM Recorrido WHERE idRuta = ? ORDER BY nombre_archivo_geojson";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRuta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recorrido recorrido = new Recorrido();
                    recorrido.setIdRecorrido(rs.getInt("idRecorrido"));
                    recorrido.setIdRuta(rs.getInt("idRuta"));
                    recorrido.setNombreArchivoGeojson(rs.getString("nombre_archivo_geojson"));
                    recorrido.setActivo(rs.getBoolean("activo"));
                    recorridos.add(recorrido);
                }
            }
        }
        return recorridos;
    }

    /**
     * Obtiene todos los recorridos activos del sistema.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RecorridoRepository repository = new RecorridoRepository();
     * List&lt;Recorrido&gt; activos = repository.findActivos();
     * System.out.println("Recorridos activos: " + activos.size());
     * </pre>
     *
     * @return Lista de recorridos activos, vacía si no hay activos
     * @throws SQLException si ocurre un error en la consulta
     * @see #findActivosByRuta(int)
     */
    public List<Recorrido> findActivos() throws SQLException {
        List<Recorrido> recorridos = new ArrayList<>();
        String query = "SELECT idRecorrido, idRuta, nombre_archivo_geojson, activo FROM Recorrido WHERE activo = 1 ORDER BY idRuta, nombre_archivo_geojson";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Recorrido recorrido = new Recorrido();
                recorrido.setIdRecorrido(rs.getInt("idRecorrido"));
                recorrido.setIdRuta(rs.getInt("idRuta"));
                recorrido.setNombreArchivoGeojson(rs.getString("nombre_archivo_geojson"));
                recorrido.setActivo(rs.getBoolean("activo"));
                recorridos.add(recorrido);
            }
        }
        return recorridos;
    }

    /**
     * Obtiene los recorridos activos de una ruta específica.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RecorridoRepository repository = new RecorridoRepository();
     * List&lt;Recorrido&gt; activosRuta = repository.findActivosByRuta(2);
     * </pre>
     *
     * @param idRuta Identificador de la ruta (mayor a 0)
     * @return Lista de recorridos activos de la ruta, vacía si no hay activos
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRuta es menor o igual a 0
     * @see #findActivos()
     */
    public List<Recorrido> findActivosByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }
        List<Recorrido> recorridos = new ArrayList<>();
        String query = "SELECT idRecorrido, idRuta, nombre_archivo_geojson, activo FROM Recorrido WHERE idRuta = ? AND activo = 1 ORDER BY nombre_archivo_geojson";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRuta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recorrido recorrido = new Recorrido();
                    recorrido.setIdRecorrido(rs.getInt("idRecorrido"));
                    recorrido.setIdRuta(rs.getInt("idRuta"));
                    recorrido.setNombreArchivoGeojson(rs.getString("nombre_archivo_geojson"));
                    recorrido.setActivo(rs.getBoolean("activo"));
                    recorridos.add(recorrido);
                }
            }
        }
        return recorridos;
    }

    /**
     * Persiste un nuevo recorrido en la base de datos.
     *
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>Recorrido no puede ser nulo</li>
     *   <li>idRuta debe ser mayor a 0</li>
     *   <li>nombre_archivo_geojson es obligatorio</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Recorrido nuevo = new Recorrido();
     * nuevo.setIdRuta(1);
     * nuevo.setNombreArchivoGeojson("recorrido1.geojson");
     * nuevo.setActivo(true);
     * RecorridoRepository repository = new RecorridoRepository();
     * int idGenerado = repository.save(nuevo);
     * </pre>
     *
     * @param recorrido Objeto Recorrido con los datos a persistir (sin ID)
     * @return ID generado automáticamente por la base de datos
     * @throws SQLException si ocurre un error en la inserción
     * @throws IllegalArgumentException si los datos son inválidos o incompletos
     * @see #update(Recorrido)
     */
    public int save(Recorrido recorrido) throws SQLException {
        if (recorrido == null) {
            throw new IllegalArgumentException("El recorrido no puede ser nulo");
        }
        if (recorrido.getIdRuta() <= 0) {
            throw new IllegalArgumentException("El ID de la ruta es obligatorio y debe ser mayor a 0");
        }
        if (recorrido.getNombreArchivoGeojson() == null || recorrido.getNombreArchivoGeojson().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo GeoJSON es obligatorio");
        }

        String query = "INSERT INTO Recorrido (idRuta, nombre_archivo_geojson, activo) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, recorrido.getIdRuta());
            stmt.setString(2, recorrido.getNombreArchivoGeojson().trim());
            stmt.setBoolean(3, recorrido.isActivo());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear recorrido, no se insertaron filas");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al crear recorrido, no se obtuvo el ID generado");
                }
            }
        }
    }

    /**
     * Actualiza los datos de un recorrido existente.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Recorrido recorrido = repository.findById(3);
     * recorrido.setNombreArchivoGeojson("nuevo.geojson");
     * boolean actualizado = repository.update(recorrido);
     * </pre>
     *
     * @param recorrido Objeto Recorrido con los datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró el recorrido
     * @throws SQLException si ocurre un error en la actualización
     * @throws IllegalArgumentException si recorrido es nulo o ID inválido
     * @see #save(Recorrido)
     * @see #findById(int)
     */
    public boolean update(Recorrido recorrido) throws SQLException {
        if (recorrido == null || recorrido.getIdRecorrido() <= 0) {
            throw new IllegalArgumentException("El recorrido debe tener un ID válido para actualizar");
        }

        String query = "UPDATE Recorrido SET idRuta = ?, nombre_archivo_geojson = ?, activo = ? WHERE idRecorrido = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, recorrido.getIdRuta());
            stmt.setString(2, recorrido.getNombreArchivoGeojson().trim());
            stmt.setBoolean(3, recorrido.isActivo());
            stmt.setInt(4, recorrido.getIdRecorrido());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un recorrido específico del sistema utilizando su identificador único.
     *
     * <p>Esta operación es permanente y elimina todos los datos del recorrido.</p>
     *
     * @param idRecorrido Identificador único del recorrido a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró el recorrido
     * @throws SQLException si ocurre un error en la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean delete(int idRecorrido) throws SQLException {
        if (idRecorrido <= 0) {
            throw new IllegalArgumentException("El ID del recorrido debe ser mayor a 0");
        }

        String query = "DELETE FROM Recorrido WHERE idRecorrido = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRecorrido);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Activa o desactiva un recorrido específico.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * boolean exito = repository.updateEstado(4, false);
     * </pre>
     *
     * @param idRecorrido Identificador del recorrido
     * @param activo Estado a establecer (true para activo, false para inactivo)
     * @return true si se actualizó correctamente, false si no se encontró el recorrido
     * @throws SQLException si ocurre un error en la actualización
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean updateEstado(int idRecorrido, boolean activo) throws SQLException {
        if (idRecorrido <= 0) {
            throw new IllegalArgumentException("El ID del recorrido debe ser mayor a 0");
        }
        String query = "UPDATE Recorrido SET activo = ? WHERE idRecorrido = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBoolean(1, activo);
            stmt.setInt(2, idRecorrido);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Busca recorridos por nombre de archivo GeoJSON (búsqueda parcial).
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;Recorrido&gt; resultados = repository.findByNombreArchivo("ruta");
     * </pre>
     *
     * @param nombreArchivo Nombre o parte del nombre del archivo a buscar
     * @return Lista de recorridos que coinciden, vacía si no hay coincidencias
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si nombreArchivo es nulo o vacío
     */
    public List<Recorrido> findByNombreArchivo(String nombreArchivo) throws SQLException {
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de archivo a buscar no puede ser nulo o vacío");
        }
        List<Recorrido> recorridos = new ArrayList<>();
        String query = "SELECT idRecorrido, idRuta, nombre_archivo_geojson, activo FROM Recorrido WHERE nombre_archivo_geojson LIKE ? ORDER BY idRuta, nombre_archivo_geojson";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + nombreArchivo.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recorrido recorrido = new Recorrido();
                    recorrido.setIdRecorrido(rs.getInt("idRecorrido"));
                    recorrido.setIdRuta(rs.getInt("idRuta"));
                    recorrido.setNombreArchivoGeojson(rs.getString("nombre_archivo_geojson"));
                    recorrido.setActivo(rs.getBoolean("activo"));
                    recorridos.add(recorrido);
                }
            }
        }
        return recorridos;
    }

    /**
     * Verifica si existe un recorrido con el mismo nombre de archivo GeoJSON.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * boolean existe = repository.existsByNombreArchivo("recorrido1.geojson");
     * </pre>
     *
     * @param nombreArchivo Nombre del archivo a verificar
     * @return true si ya existe un recorrido con ese nombre de archivo, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si nombreArchivo es nulo o vacío
     */
    public boolean existsByNombreArchivo(String nombreArchivo) throws SQLException {
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de archivo no puede ser nulo o vacío");
        }
        String query = "SELECT 1 FROM Recorrido WHERE nombre_archivo_geojson = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreArchivo.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Obtiene el número total de recorridos registrados en el sistema.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * int total = repository.count();
     * </pre>
     *
     * @return Número total de recorridos
     * @throws SQLException si ocurre un error en la consulta
     */
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM Recorrido";

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
     * Obtiene el número de recorridos activos en el sistema.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * int activos = repository.countActivos();
     * </pre>
     *
     * @return Número de recorridos activos
     * @throws SQLException si ocurre un error en la consulta
     */
    public int countActivos() throws SQLException {
        String query = "SELECT COUNT(*) FROM Recorrido WHERE activo = 1";

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
     * Obtiene el número de recorridos asociados a una ruta específica.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * int recorridosRuta = repository.countByRuta(2);
     * </pre>
     *
     * @param idRuta Identificador de la ruta (mayor a 0)
     * @return Número de recorridos de la ruta
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRuta es menor o igual a 0
     */
    public int countByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }
        String query = "SELECT COUNT(*) FROM Recorrido WHERE idRuta = ?";

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