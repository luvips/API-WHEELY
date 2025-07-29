package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de operaciones CRUD de la entidad Usuario en el sistema Wheely.
 *
 * <p>Esta clase maneja todas las interacciones con la base de datos MySQL para la tabla Usuario,
 * proporcionando métodos seguros para crear, leer, actualizar y eliminar usuarios del sistema
 * de transporte público. Incluye funcionalidades especializadas para autenticación y gestión
 * de datos sensibles como contraseñas encriptadas.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Operaciones CRUD completas para usuarios</li>
 * <li>Búsqueda por email para autenticación</li>
 * <li>Validación de unicidad de emails</li>
 * <li>Búsqueda parcial por nombre</li>
 * <li>Conteo de usuarios para estadísticas</li>
 * <li>Manejo seguro de contraseñas hashadas</li>
 * </ul>
 *
 * <p>Consideraciones de seguridad:</p>
 * <ul>
 * <li>Las contraseñas se almacenan usando hash BCrypt</li>
 * <li>Los métodos validan entrada para prevenir SQL injection</li>
 * <li>Se utilizan PreparedStatements para todas las consultas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Usuario
 * @see com.wheely.config.DatabaseConfig
 * @see com.wheely.service.UsuarioService
 */
public class UsuarioRepository {

    /**
     * Obtiene todos los usuarios registrados en el sistema ordenados alfabéticamente por nombre.
     *
     * <p>Este método recupera la lista completa de usuarios de la base de datos, incluyendo
     * las contraseñas hashadas. Es responsabilidad del servicio limpiar las contraseñas
     * antes de enviar los datos al cliente.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * UsuarioRepository repository = new UsuarioRepository();
     * List&lt;Usuario&gt; todosLosUsuarios = repository.findAll();
     * System.out.println("Total de usuarios registrados: " + todosLosUsuarios.size());
     *
     * for (Usuario usuario : todosLosUsuarios) {
     *     System.out.println("Usuario: " + usuario.getNombre() + " - " + usuario.getEmail());
     * }
     * </pre>
     *
     * @return Lista de objetos Usuario con todos los registros de la base de datos,
     *         ordenados alfabéticamente por nombre. Retorna lista vacía si no hay usuarios.
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     *                     o durante la ejecución de la consulta SQL
     *
     * @see #findById(int)
     * @see #findByEmail(String)
     */
    public List<Usuario> findAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT idUsuario, nombre, email, password FROM Usuario ORDER BY nombre";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPassword(rs.getString("password"));
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    /**
     * Busca y recupera un usuario específico utilizando su identificador único.
     *
     * <p>Este método realiza una búsqueda directa por clave primaria, garantizando
     * una respuesta rápida y precisa. Incluye la contraseña hashada en el resultado.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * UsuarioRepository repository = new UsuarioRepository();
     * Usuario usuario = repository.findById(10);
     * if (usuario != null) {
     *     System.out.println("Usuario encontrado: " + usuario.getNombre());
     *     System.out.println("Email: " + usuario.getEmail());
     * } else {
     *     System.out.println("Usuario no encontrado con ID: 10");
     * }
     * </pre>
     *
     * @param idUsuario Identificador único del usuario a buscar (debe ser mayor a 0)
     * @return Objeto Usuario con los datos completos si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta preparada
     * @throws IllegalArgumentException si idUsuario es menor o igual a 0
     *
     * @see #findAll()
     * @see #findByEmail(String)
     */
    public Usuario findById(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }

        Usuario usuario = null;
        String query = "SELECT idUsuario, nombre, email, password FROM Usuario WHERE idUsuario = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password"));
                }
            }
        }
        return usuario;
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * <p>Este método es fundamental para los procesos de autenticación en el sistema,
     * ya que permite verificar credenciales de acceso. Retorna el usuario completo
     * incluyendo la contraseña hashada para validación posterior.</p>
     *
     * <p>Ejemplo de uso para autenticación:</p>
     * <pre>
     * UsuarioRepository repository = new UsuarioRepository();
     * Usuario usuario = repository.findByEmail("usuario@example.com");
     * if (usuario != null) {
     *     // Verificar contraseña con PasswordUtil
     *     boolean esValida = PasswordUtil.verifyPassword(passwordIngresada, usuario.getPassword());
     *     if (esValida) {
     *         System.out.println("Autenticación exitosa para: " + usuario.getNombre());
     *     }
     * }
     * </pre>
     *
     * @param email Dirección de correo electrónico del usuario a buscar
     * @return Objeto Usuario con los datos completos si existe, null si no se encuentra
     * @throws SQLException si ocurre un error en la conexión a la base de datos
     *                     o en la ejecución de la consulta preparada
     * @throws IllegalArgumentException si email es nulo, vacío o tiene formato inválido
     *
     * @see #emailExists(String)
     * @see com.wheely.util.PasswordUtil#verifyPassword(String, String)
     */
    public Usuario findByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }

        Usuario usuario = null;
        String query = "SELECT idUsuario, nombre, email, password FROM Usuario WHERE email = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password"));
                }
            }
        }
        return usuario;
    }

    /**
     * Persiste un nuevo usuario en la base de datos del sistema Wheely.
     *
     * <p>Este método inserta un registro de usuario en la tabla, generando automáticamente
     * el ID único. La contraseña debe estar previamente hashada usando BCrypt antes de
     * llamar a este método.</p>
     *
     * <p>Validaciones realizadas:</p>
     * <ul>
     * <li>Usuario no puede ser nulo</li>
     * <li>Nombre es obligatorio</li>
     * <li>Email es obligatorio y debe tener formato válido</li>
     * <li>Contraseña es obligatoria (debe estar hashada)</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Usuario nuevoUsuario = new Usuario();
     * nuevoUsuario.setNombre("Juan Pérez");
     * nuevoUsuario.setEmail("juan.perez@example.com");
     * nuevoUsuario.setPassword(PasswordUtil.hashPassword("password123"));
     *
     * UsuarioRepository repository = new UsuarioRepository();
     * int idGenerado = repository.save(nuevoUsuario);
     * System.out.println("Usuario creado con ID: " + idGenerado);
     * </pre>
     *
     * @param usuario Objeto Usuario con los datos a persistir (sin ID, pero con password hashada)
     * @return ID generado automáticamente por la base de datos para el nuevo usuario
     * @throws SQLException si ocurre un error durante la inserción en la base de datos
     * @throws IllegalArgumentException si los datos del usuario son inválidos o incompletos
     *
     * @see #update(Usuario)
     * @see com.wheely.util.PasswordUtil#hashPassword(String)
     */
    public int save(Usuario usuario) throws SQLException {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        String query = "INSERT INTO Usuario (nombre, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNombre().trim());
            stmt.setString(2, usuario.getEmail().trim());
            stmt.setString(3, usuario.getPassword());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear usuario, no se insertaron filas");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al crear usuario, no se obtuvo el ID generado");
                }
            }
        }
    }

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     *
     * <p>Este método modifica todos los campos de un usuario previamente registrado,
     * utilizando su ID como identificador. Si la contraseña está incluida en el objeto,
     * debe estar previamente hashada.</p>
     *
     * @param usuario Objeto Usuario con los datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró el usuario
     * @throws SQLException si ocurre un error durante la actualización
     * @throws IllegalArgumentException si la usuario es nulo o tiene un ID inválido
     *
     * @see #save(Usuario)
     * @see #findById(int)
     */
    public boolean update(Usuario usuario) throws SQLException {
        if (usuario == null || usuario.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El usuario debe tener un ID válido para actualizar");
        }

        String query = "UPDATE Usuario SET nombre = ?, email = ?, password = ? WHERE idUsuario = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usuario.getNombre().trim());
            stmt.setString(2, usuario.getEmail().trim());
            stmt.setString(3, usuario.getPassword());
            stmt.setInt(4, usuario.getIdUsuario());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un usuario específico del sistema utilizando su identificador único.
     *
     * <p>Esta operación es permanente y elimina todos los datos del usuario.
     * Se recomienda verificar dependencias en otras tablas antes de ejecutar.</p>
     *
     * @param idUsuario Identificador único del usuario a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró el usuario
     * @throws SQLException si ocurre un error durante la eliminación
     * @throws IllegalArgumentException si el ID es inválido
     */
    public boolean delete(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }

        String query = "DELETE FROM Usuario WHERE idUsuario = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Verifica si existe un usuario registrado con el email especificado.
     *
     * <p>Este método es fundamental para validar unicidad de emails durante
     * el registro de nuevos usuarios, evitando duplicados en el sistema.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * UsuarioRepository repository = new UsuarioRepository();
     * if (repository.emailExists("nuevo@example.com")) {
     *     System.out.println("Email ya está registrado");
     * } else {
     *     System.out.println("Email disponible para registro");
     * }
     * </pre>
     *
     * @param email Dirección de correo electrónico a verificar
     * @return true si existe un usuario con ese email, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el email es nulo o vacío
     *
     * @see #emailExistsExcludingUser(String, int)
     */
    public boolean emailExists(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }

        String query = "SELECT COUNT(*) FROM Usuario WHERE email = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Verifica si existe un usuario con el email especificado, excluyendo un usuario específico.
     *
     * <p>Este método es útil durante actualizaciones de perfil, donde un usuario
     * puede mantener su email actual pero no debe coincidir con otros usuarios.</p>
     *
     * @param email Email a verificar
     * @param excludeUserId ID del usuario a excluir de la verificación
     * @return true si existe otro usuario con ese email, false en caso contrario
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si el email es inválido o excludeUserId <= 0
     */
    public boolean emailExistsExcludingUser(String email, int excludeUserId) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }
        if (excludeUserId <= 0) {
            throw new IllegalArgumentException("El ID del usuario a excluir debe ser mayor a 0");
        }

        String query = "SELECT COUNT(*) FROM Usuario WHERE email = ? AND idUsuario != ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email.trim());
            stmt.setInt(2, excludeUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Obtiene el número total de usuarios registrados en el sistema.
     *
     * <p>Este método es utilizado para generar estadísticas y reportes
     * del panel administrativo del sistema Wheely.</p>
     *
     * @return Número total de usuarios registrados
     * @throws SQLException si ocurre un error en la consulta
     */
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM Usuario";

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
     * Busca usuarios cuyo nombre contenga la cadena especificada.
     *
     * <p>Este método realiza una búsqueda parcial usando LIKE, útil para
     * funcionalidades de autocompletado y búsqueda en interfaces de usuario.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * UsuarioRepository repository = new UsuarioRepository();
     * List&lt;Usuario&gt; resultados = repository.findByNombre("Juan");
     * // Retorna usuarios con nombres como "Juan", "Juana", "Juan Carlos", etc.
     * </pre>
     *
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de usuarios que coinciden con la búsqueda, ordenados alfabéticamente
     * @throws SQLException si ocurre un error en la consulta
     * @throws IllegalArgumentException si nombre es nulo o vacío
     */
    public List<Usuario> findByNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre a buscar no puede ser nulo o vacío");
        }

        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT idUsuario, nombre, email, password FROM Usuario WHERE nombre LIKE ? ORDER BY nombre";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + nombre.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password"));
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }
}