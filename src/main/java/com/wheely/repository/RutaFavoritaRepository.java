package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.RutaFavorita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de operaciones CRUD de la entidad RutaFavorita en el sistema Wheely.
 *
 * <p>Esta clase maneja todas las interacciones con la base de datos MySQL para la tabla RutaFavorita,
 * que implementa una relación muchos-a-muchos entre usuarios y rutas. Permite a los usuarios marcar
 * sus rutas de transporte preferidas para acceso rápido y personalización de la experiencia.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión de relaciones usuario-ruta favorita</li>
 * <li>Validación de unicidad para evitar duplicados</li>
 * <li>Consultas especializadas por usuario y por ruta</li>
 * <li>Operaciones de conteo para estadísticas</li>
 * <li>Eliminación selectiva por combinación usuario-ruta</li>
 * <li>Verificación de existencia de favoritos</li>
 * </ul>
 *
 * <p>Estructura de la tabla RutaFavorita:</p>
 * <ul>
 * <li>idRutaFavorita: Clave primaria auto-incremental</li>
 * <li>idUsuario: Clave foránea hacia tabla Usuario</li>
 * <li>idRuta: Clave foránea hacia tabla Ruta</li>
 * <li>Restricción única en la combinación (idUsuario, idRuta)</li>
 * </ul>
 *
 * <p>Casos de uso típicos:</p>
 * <ul>
 * <li>Personalización del dashboard de usuario</li>
 * <li>Análisis de popularidad de rutas</li>
 * <li>Recomendaciones basadas en preferencias</li>
 * <li>Métricas de engagement del usuario</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.RutaFavorita
 * @see com.wheely.model.Usuario
 * @see com.wheely.model.Ruta
 * @see com.wheely.config.DatabaseConfig
 */
public class RutaFavoritaRepository {

    /**
     * Obtiene todas las relaciones de rutas favoritas registradas en el sistema.
     *
     * <p>Este metodo recupera la lista completa de todas las combinaciones usuario-ruta
     * marcadas como favoritas, ordenadas primero por usuario y luego por ruta para
     * facilitar el análisis y presentación de datos.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavoritaRepository repository = new RutaFavoritaRepository();
     * List&lt;RutaFavorita&gt; todasLasFavoritas = repository.findAll();
     * System.out.println("Total de favoritos en el sistema: " + todasLasFavoritas.size());
     *
     * // Agrupar por usuario para análisis
     * Map&lt;Integer, List&lt;RutaFavorita&gt;&gt; favoritosPorUsuario = todasLasFavoritas.stream()
     *     .collect(Collectors.groupingBy(RutaFavorita::getIdUsuario));
     * </pre>
     *
     * @return Lista de objetos RutaFavorita con todas las relaciones registradas,
     *         ordenadas por idUsuario y luego por idRuta. Retorna lista vacía si no hay favoritos.
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     *                     o durante la ejecución de la consulta SQL
     *
     * @see #findByUsuario(int)
     * @see #findByRuta(int)
     */
    public List<RutaFavorita> findAll() throws SQLException {
        List<RutaFavorita> favoritas = new ArrayList<>();
        String query = "SELECT idRutaFavorita, idUsuario, idRuta FROM RutaFavorita ORDER BY idUsuario, idRuta";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                RutaFavorita favorita = new RutaFavorita();
                favorita.setIdRutaFavorita(rs.getInt("idRutaFavorita"));
                favorita.setIdUsuario(rs.getInt("idUsuario"));
                favorita.setIdRuta(rs.getInt("idRuta"));
                favoritas.add(favorita);
            }
        }
        return favoritas;
    }

    /**
     * Busca y recupera una relación de ruta favorita específica utilizando su identificador único.
     *
     * <p>Este metodo realiza una búsqueda directa por clave primaria, útil para
     * operaciones administrativas y verificaciones específicas del sistema.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavoritaRepository repository = new RutaFavoritaRepository();
     * RutaFavorita favorita = repository.findById(42);
     * if (favorita != null) {
     *     System.out.println("Favorito encontrado:");
     *     System.out.println("Usuario ID: " + favorita.getIdUsuario());
     *     System.out.println("Ruta ID: " + favorita.getIdRuta());
     * } else {
     *     System.out.println("Favorito no encontrado con ID: 42");
     * }
     * </pre>
     *
     * @param idRutaFavorita Identificador único de la ruta favorita a buscar (debe ser mayor a 0)
     * @return Objeto RutaFavorita con los datos de la relación si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta preparada
     * @throws IllegalArgumentException si idRutaFavorita es menor o igual a 0
     *
     * @see #findByUsuarioAndRuta(int, int)
     */
    public RutaFavorita findById(int idRutaFavorita) throws SQLException {
        if (idRutaFavorita <= 0) {
            throw new IllegalArgumentException("El ID de la ruta favorita debe ser mayor a 0");
        }

        RutaFavorita favorita = null;
        String query = "SELECT idRutaFavorita, idUsuario, idRuta FROM RutaFavorita WHERE idRutaFavorita = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRutaFavorita);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    favorita = new RutaFavorita();
                    favorita.setIdRutaFavorita(rs.getInt("idRutaFavorita"));
                    favorita.setIdUsuario(rs.getInt("idUsuario"));
                    favorita.setIdRuta(rs.getInt("idRuta"));
                }
            }
        }
        return favorita;
    }

    /**
     * Recupera todas las rutas marcadas como favoritas por un usuario específico.
     *
     * <p>Este método es fundamental para personalizar la experiencia del usuario,
     * permitiendo mostrar sus rutas preferidas en el dashboard y facilitar el
     * acceso rápido a la información de transporte más relevante.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavoritaRepository repository = new RutaFavoritaRepository();
     * List&lt;RutaFavorita&gt; favoritasDelUsuario = repository.findByUsuario(25);
     * System.out.println("El usuario 25 tiene " + favoritasDelUsuario.size() + " rutas favoritas");
     *
     * // Obtener IDs de rutas para consulta adicional
     * List&lt;Integer&gt; rutaIds = favoritasDelUsuario.stream()
     *     .map(RutaFavorita::getIdRuta)
     *     .collect(Collectors.toList());
     * </pre>
     *
     * @param idUsuario ID del usuario cuyas rutas favoritas se desean consultar
     * @return Lista de rutas favoritas del usuario, ordenadas por ID de ruta.
     *         Retorna lista vacía si el usuario no tiene favoritos.
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta
     * @throws IllegalArgumentException si idUsuario es menor o igual a 0
     *
     * @see #countByUsuario(int)
     * @see #existsByUsuarioAndRuta(int, int)
     */
    public List<RutaFavorita> findByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }

        List<RutaFavorita> favoritas = new ArrayList<>();
        String query = "SELECT idRutaFavorita, idUsuario, idRuta FROM RutaFavorita WHERE idUsuario = ? ORDER BY idRuta";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RutaFavorita favorita = new RutaFavorita();
                    favorita.setIdRutaFavorita(rs.getInt("idRutaFavorita"));
                    favorita.setIdUsuario(rs.getInt("idUsuario"));
                    favorita.setIdRuta(rs.getInt("idRuta"));
                    favoritas.add(favorita);
                }
            }
        }
        return favoritas;
    }

    /**
     * Recupera todos los usuarios que han marcado una ruta específica como favorita.
     *
     * <p>Este método es esencial para análisis de popularidad de rutas y generación
     * de métricas de engagement. Permite identificar las rutas más apreciadas por
     * los usuarios y optimizar el servicio en consecuencia.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavoritaRepository repository = new RutaFavoritaRepository();
     * List&lt;RutaFavorita&gt; usuariosQueFavorecen = repository.findByRuta(15);
     * System.out.println("La ruta 15 es favorita de " + usuariosQueFavorecen.size() + " usuarios");
     *
     * // Analizar demografía de usuarios que favorecen esta ruta
     * Set&lt;Integer&gt; usuarioIds = usuariosQueFavorecen.stream()
     *     .map(RutaFavorita::getIdUsuario)
     *     .collect(Collectors.toSet());
     * </pre>
     *
     * @param idRuta ID de la ruta cuyos favoritos se desean consultar
     * @return Lista de relaciones RutaFavorita para la ruta especificada, ordenadas por usuario.
     *         Retorna lista vacía si la ruta no tiene favoritos.
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta
     * @throws IllegalArgumentException si idRuta es menor o igual a 0
     *
     * @see #countByRuta(int)
     */
    public List<RutaFavorita> findByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        List<RutaFavorita> favoritas = new ArrayList<>();
        String query = "SELECT idRutaFavorita, idUsuario, idRuta FROM RutaFavorita WHERE idRuta = ? ORDER BY idUsuario";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRuta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RutaFavorita favorita = new RutaFavorita();
                    favorita.setIdRutaFavorita(rs.getInt("idRutaFavorita"));
                    favorita.setIdUsuario(rs.getInt("idUsuario"));
                    favorita.setIdRuta(rs.getInt("idRuta"));
                    favoritas.add(favorita);
                }
            }
        }
        return favoritas;
    }

    /**
     * Persiste una nueva relación de ruta favorita en la base de datos del sistema Wheely.
     *
     * <p>Este método crea una nueva relación usuario-ruta favorita, generando automáticamente
     * el ID único. Antes de usar este método, se recomienda verificar que la combinación
     * no exista ya usando existsByUsuarioAndRuta() para evitar violaciones de restricción única.</p>
     *
     * <p>Validaciones realizadas:</p>
     * <ul>
     * <li>RutaFavorita no puede ser nula</li>
     * <li>ID de usuario debe ser válido (mayor a 0)</li>
     * <li>ID de ruta debe ser válido (mayor a 0)</li>
     * <li>La combinación usuario-ruta debe ser única</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavorita nuevaFavorita = new RutaFavorita();
     * nuevaFavorita.setIdUsuario(18);
     * nuevaFavorita.setIdRuta(7);
     *
     * RutaFavoritaRepository repository = new RutaFavoritaRepository();
     *
     * // Verificar que no exista ya
     * if (!repository.existsByUsuarioAndRuta(18, 7)) {
     *     int idGenerado = repository.save(nuevaFavorita);
     *     System.out.println("Favorito creado con ID: " + idGenerado);
     * } else {
     *     System.out.println("El usuario ya tiene esta ruta como favorita");
     * }
     * </pre>
     *
     * @param rutaFavorita Objeto RutaFavorita con los datos a persistir (sin ID)
     * @return ID generado automáticamente por la base de datos para la nueva relación
     * @throws SQLException si ocurre un error durante la inserción, incluyendo violaciones
     *                     de restricción única cuando la combinación usuario-ruta ya existe
     * @throws IllegalArgumentException si los datos de la rutaFavorita son inválidos
     *
     * @see #existsByUsuarioAndRuta(int, int)
     * @see #deleteByUsuarioAndRuta(int, int)
     */
    public int save(RutaFavorita rutaFavorita) throws SQLException {
        if (rutaFavorita == null) {
            throw new IllegalArgumentException("La ruta favorita no puede ser nula");
        }
        if (rutaFavorita.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (rutaFavorita.getIdRuta() <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        String query = "INSERT INTO RutaFavorita (idUsuario, idRuta) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, rutaFavorita.getIdUsuario());
            stmt.setInt(2, rutaFavorita.getIdRuta());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear ruta favorita, no se insertaron filas");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al crear ruta favorita, no se obtuvo el ID generado");
                }
            }
        }
    }

    /**
     * Elimina una relación de ruta favorita específica utilizando su identificador único.
     *
     * <p>Este método elimina permanentemente un registro de favorito usando la clave primaria.
     * Es menos común que deleteByUsuarioAndRuta() pero útil para operaciones administrativas.</p>
     *
     * @param idRutaFavorita Identificador único de la ruta favorita a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró el registro
     * @throws SQLException si ocurre un error durante la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     *
     * @see #deleteByUsuarioAndRuta(int, int)
     */
    public boolean delete(int idRutaFavorita) throws SQLException {
        if (idRutaFavorita <= 0) {
            throw new IllegalArgumentException("El ID de la ruta favorita debe ser mayor a 0");
        }

        String query = "DELETE FROM RutaFavorita WHERE idRutaFavorita = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRutaFavorita);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una relación de ruta favorita utilizando la combinación usuario-ruta.
     *
     * <p>Este es el método preferido para eliminar favoritos desde la interfaz de usuario,
     * ya que permite trabajar directamente con los IDs de usuario y ruta sin necesidad
     * de conocer el ID de la tabla intermedia.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavoritaRepository repository = new RutaFavoritaRepository();
     * boolean eliminado = repository.deleteByUsuarioAndRuta(18, 7);
     * if (eliminado) {
     *     System.out.println("Ruta eliminada de favoritos exitosamente");
     * } else {
     *     System.out.println("La ruta no estaba en favoritos");
     * }
     * </pre>
     *
     * @param idUsuario ID del usuario propietario del favorito
     * @param idRuta ID de la ruta a eliminar de favoritos
     * @return true si la eliminación fue exitosa, false si no se encontró la combinación
     * @throws SQLException si ocurre un error durante la eliminación
     * @throws IllegalArgumentException si algún ID es inválido
     *
     * @see #existsByUsuarioAndRuta(int, int)
     */
    public boolean deleteByUsuarioAndRuta(int idUsuario, int idRuta) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        String query = "DELETE FROM RutaFavorita WHERE idUsuario = ? AND idRuta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idRuta);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Verifica si una combinación específica de usuario y ruta ya existe como favorita.
     *
     * <p>Este método es esencial para validar duplicados antes de crear nuevos favoritos
     * y para verificar el estado actual de una ruta en la lista de favoritos del usuario.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavoritaRepository repository = new RutaFavoritaRepository();
     * if (repository.existsByUsuarioAndRuta(18, 7)) {
     *     System.out.println("La ruta ya está en favoritos");
     *     // Mostrar opción de eliminar
     * } else {
     *     System.out.println("La ruta no está en favoritos");
     *     // Mostrar opción de agregar
     * }
     * </pre>
     *
     * @param idUsuario ID del usuario a verificar
     * @param idRuta ID de la ruta a verificar
     * @return true si ya existe la combinación usuario-ruta como favorita, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si algún ID es inválido
     *
     * @see #findByUsuarioAndRuta(int, int)
     */
    public boolean existsByUsuarioAndRuta(int idUsuario, int idRuta) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        String query = "SELECT 1 FROM RutaFavorita WHERE idUsuario = ? AND idRuta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idRuta);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Busca la relación de ruta favorita específica por combinación usuario-ruta.
     *
     * <p>Este método retorna el objeto completo de la relación favorita, incluyendo
     * su ID único, útil cuando se necesita el registro completo para operaciones posteriores.</p>
     *
     * @param idUsuario ID del usuario
     * @param idRuta ID de la ruta
     * @return Objeto RutaFavorita con todos los datos si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si algún ID es inválido
     *
     * @see #existsByUsuarioAndRuta(int, int)
     */
    public RutaFavorita findByUsuarioAndRuta(int idUsuario, int idRuta) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        RutaFavorita favorita = null;
        String query = "SELECT idRutaFavorita, idUsuario, idRuta FROM RutaFavorita WHERE idUsuario = ? AND idRuta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idRuta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    favorita = new RutaFavorita();
                    favorita.setIdRutaFavorita(rs.getInt("idRutaFavorita"));
                    favorita.setIdUsuario(rs.getInt("idUsuario"));
                    favorita.setIdRuta(rs.getInt("idRuta"));
                }
            }
        }
        return favorita;
    }

    /**
     * Obtiene el número total de relaciones de rutas favoritas en el sistema.
     *
     * <p>Este método proporciona métricas generales de engagement y utilización
     * de la funcionalidad de favoritos en el sistema Wheely.</p>
     *
     * @return Número total de rutas favoritas registradas en el sistema
     * @throws SQLException si ocurre un error en la consulta
     */
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM RutaFavorita";

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
     * Cuenta el número de rutas favoritas que tiene un usuario específico.
     *
     * <p>Útil para estadísticas de usuario y para implementar límites
     * en el número de favoritos por usuario si fuera necesario.</p>
     *
     * @param idUsuario ID del usuario para contar sus favoritos
     * @return Número de rutas favoritas del usuario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idUsuario es inválido
     */
    public int countByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }

        String query = "SELECT COUNT(*) FROM RutaFavorita WHERE idUsuario = ?";

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
     * Cuenta el número de usuarios que han marcado una ruta específica como favorita.
     *
     * <p>Este método es fundamental para medir la popularidad de las rutas y
     * generar rankings de las rutas más apreciadas por los usuarios del sistema.</p>
     *
     * @param idRuta ID de la ruta para contar sus favoritos
     * @return Número de usuarios que tienen esta ruta como favorita
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si idRuta es inválido
     */
    public int countByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("El ID de la ruta debe ser mayor a 0");
        }

        String query = "SELECT COUNT(*) FROM RutaFavorita WHERE idRuta = ?";

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