package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.Ruta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 * Repositorio para la gestión de operaciones CRUD de la entidad Ruta en el sistema Wheely.
 *
 * <p>Esta clase encapsula todas las operaciones de acceso a datos para la tabla Ruta,
 * proporcionando métodos para crear, leer, actualizar y eliminar rutas de transporte público.
 * Utiliza JDBC con HikariCP para manejo eficiente de conexiones a la base de datos MySQL.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Consultas de todas las rutas disponibles</li>
 * <li>Búsqueda de rutas por ID único</li>
 * <li>Inserción de nuevas rutas</li>
 * <li>Actualización de datos de rutas existentes</li>
 * <li>Eliminación de rutas del sistema</li>
 * <li>Verificación de existencia por nombre</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Ruta
 * @see com.wheely.config.DatabaseConfig
 */
public class RutaRepository {

    /**
     * Obtiene todas las rutas registradas en el sistema ordenadas alfabéticamente.
     *
     * <p>Este método ejecuta una consulta SQL que recupera todos los registros
     * de la tabla Ruta, ordenándolos por nombre para facilitar la presentación
     * al usuario final.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaRepository repository = new RutaRepository();
     * List&lt;Ruta&gt; todasLasRutas = repository.findAll();
     * System.out.println("Total de rutas: " + todasLasRutas.size());
     * </pre>
     *
     * @return Lista de objetos Ruta con todos los registros de la base de datos.
     *         Retorna lista vacía si no hay rutas registradas.
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     *                     o durante la ejecución de la consulta SQL
     *
     * @see #findById(int)
     */
    public List<Ruta> findAll() throws SQLException {
        List<Ruta> rutas = new ArrayList<>();
        String query = "SELECT idRuta, nombre_ruta, origen, destino FROM Ruta ORDER BY nombre_ruta";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ruta ruta = new Ruta();
                ruta.setIdRuta(rs.getInt("idRuta"));
                ruta.setNombreRuta(rs.getString("nombre_ruta"));
                ruta.setOrigen(rs.getString("origen"));
                ruta.setDestino(rs.getString("destino"));
                rutas.add(ruta);
            }
        }
        return rutas;
    }

    /**
     * Busca y recupera una ruta específica utilizando su identificador único.
     *
     * <p>Este método realiza una búsqueda puntual en la base de datos usando
     * el ID como clave primaria, garantizando una respuesta rápida y precisa.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaRepository repository = new RutaRepository();
     * Ruta ruta = repository.findById(5);
     * if (ruta != null) {
     *     System.out.println("Ruta encontrada: " + ruta.getNombreRuta());
     * } else {
     *     System.out.println("Ruta no encontrada");
     * }
     * </pre>
     *
     * @param idRuta Identificador único de la ruta a buscar (debe ser mayor a 0)
     * @return Objeto Ruta con los datos completos si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta preparada
     * @throws IllegalArgumentException si idRuta es menor o igual a 0
     *
     * @see #findAll()
     * @see #existsByNombre(String)
     */
    public Ruta findById(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        Ruta ruta = null;
        String query = "SELECT idRuta, nombre_ruta, origen, destino FROM Ruta WHERE idRuta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRuta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ruta = new Ruta();
                    ruta.setIdRuta(rs.getInt("idRuta"));
                    ruta.setNombreRuta(rs.getString("nombre_ruta"));
                    ruta.setOrigen(rs.getString("origen"));
                    ruta.setDestino(rs.getString("destino"));
                }
            }
        }
        return ruta;
    }

    /**
     * Persiste una nueva ruta en la base de datos del sistema Wheely.
     *
     * <p>Este método inserta un nuevo registro en la tabla Ruta, validando
     * que todos los campos requeridos estén presentes y sean válidos.
     * El ID se genera automáticamente por la base de datos.</p>
     *
     * <p>Validaciones realizadas:</p>
     * <ul>
     * <li>Nombre de ruta no puede ser nulo o vacío</li>
     * <li>Origen y destino deben ser diferentes</li>
     * <li>Longitud máxima de campos según esquema de BD</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Ruta nuevaRuta = new Ruta();
     * nuevaRuta.setNombreRuta("Ruta Centro-Universidad");
     * nuevaRuta.setOrigen("Centro de la Ciudad");
     * nuevaRuta.setDestino("Universidad Politécnica");
     *
     * RutaRepository repository = new RutaRepository();
     * int idGenerado = repository.save(nuevaRuta);
     * System.out.println("Ruta creada con ID: " + idGenerado);
     * </pre>
     *
     * @param ruta Objeto Ruta con los datos a persistir (sin ID)
     * @return ID generado automáticamente por la base de datos para la nueva ruta
     * @throws SQLException si ocurre un error durante la inserción en la base de datos
     * @throws IllegalArgumentException si los datos de la ruta son inválidos o incompletos
     *
     * @see #update(Ruta)
     * @see #existsByNombre(String)
     */
    public int save(Ruta ruta) throws SQLException {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }
        if (ruta.getNombreRuta() == null || ruta.getNombreRuta().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ruta es obligatorio");
        }
        if (ruta.getOrigen() == null || ruta.getOrigen().trim().isEmpty()) {
            throw new IllegalArgumentException("El origen es obligatorio");
        }
        if (ruta.getDestino() == null || ruta.getDestino().trim().isEmpty()) {
            throw new IllegalArgumentException("El destino es obligatorio");
        }

        String query = "INSERT INTO Ruta (nombre_ruta, origen, destino) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ruta.getNombreRuta().trim());
            stmt.setString(2, ruta.getOrigen().trim());
            stmt.setString(3, ruta.getDestino().trim());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear la ruta, no se insertaron filas");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al crear la ruta, no se obtuvo el ID generado");
                }
            }
        }
    }

    /**
     * Actualiza los datos de una ruta existente en la base de datos.
     *
     * <p>Este método modifica los campos de una ruta previamente registrada,
     * utilizando su ID como identificador. Todos los campos son actualizados
     * con los nuevos valores proporcionados.</p>
     *
     * @param ruta Objeto Ruta con los datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró la ruta
     * @throws SQLException si ocurre un error durante la actualización
     * @throws IllegalArgumentException si la ruta es nula o tiene datos inválidos
     *
     * @see #save(Ruta)
     * @see #findById(int)
     */
    public boolean update(Ruta ruta) throws SQLException {
        if (ruta == null || ruta.getIdRuta() <= 0) {
            throw new IllegalArgumentException("La ruta debe tener un ID válido para actualizar");
        }

        String query = "UPDATE Ruta SET nombre_ruta = ?, origen = ?, destino = ? WHERE idRuta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ruta.getNombreRuta().trim());
            stmt.setString(2, ruta.getOrigen().trim());
            stmt.setString(3, ruta.getDestino().trim());
            stmt.setInt(4, ruta.getIdRuta());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una ruta específica del sistema utilizando su identificador único.
     *
     * <p>Esta operación es permanente y elimina todos los datos asociados
     * a la ruta. Se recomienda verificar dependencias antes de ejecutar.</p>
     *
     * @param idRuta Identificador único de la ruta a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró la ruta
     * @throws SQLException si ocurre un error durante la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean delete(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        String query = "DELETE FROM Ruta WHERE idRuta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRuta);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Verifica si existe una ruta con el nombre especificado en el sistema.
     *
     * <p>Este método es útil para validar duplicados antes de crear nuevas rutas,
     * asegurando la unicidad de nombres en el sistema de transporte.</p>
     *
     * @param nombreRuta Nombre de la ruta a verificar
     * @return true si existe una ruta con ese nombre, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public boolean existsByNombre(String nombreRuta) throws SQLException {
        if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ruta no puede ser nulo o vacío");
        }

        String query = "SELECT COUNT(*) FROM Ruta WHERE nombre_ruta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreRuta.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}