package com.wheely.service;

import com.wheely.model.Usuario;
import com.wheely.repository.UsuarioRepository;
import com.wheely.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la lógica de negocio de usuarios del sistema Wheely.
 *
 * <p>Encapsula toda la lógica relacionada con gestión de usuarios, incluyendo
 * operaciones CRUD, autenticación, registro y validaciones de seguridad.
 * Maneja el cifrado de contraseñas y validaciones de integridad de datos
 * para garantizar la seguridad del sistema de transporte.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de usuarios (CRUD) con seguridad</li>
 * <li>Autenticación segura con contraseñas cifradas</li>
 * <li>Registro de nuevos usuarios con validaciones</li>
 * <li>Validación de email único y formato válido</li>
 * <li>Protección de contraseñas mediante hash BCrypt</li>
 * <li>Limpieza automática de contraseñas en respuestas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see Usuario
 * @see UsuarioRepository
 * @see PasswordUtil
 */
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor que inicializa el servicio con su repositorio.
     *
     * @param usuarioRepository Repositorio para operaciones de usuarios
     */
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene todos los usuarios del sistema con contraseñas limpiadas.
     *
     * <p>Retorna la lista completa de usuarios registrados en el sistema,
     * limpiando automáticamente las contraseñas por seguridad. Útil para
     * administración de usuarios y listados del sistema.</p>
     *
     * @return Lista completa de usuarios con contraseñas vacías por seguridad
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public List<Usuario> getAllUsuarios() throws SQLException {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(u -> u.setPassword("")); // Limpiar passwords
        return usuarios;
    }

    /**
     * Obtiene un usuario específico por su identificador único.
     *
     * <p>Busca y retorna un usuario específico, limpiando automáticamente
     * la contraseña por seguridad antes de devolver el resultado.</p>
     *
     * @param id ID único del usuario a buscar
     * @return Usuario encontrado con contraseña limpiada o null si no existe
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public Usuario getUsuarioById(int id) throws SQLException {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario != null) usuario.setPassword("");
        return usuario;
    }

    /**
     * Crea un nuevo usuario en el sistema con validaciones completas.
     *
     * <p>Registra un nuevo usuario aplicando todas las validaciones de seguridad
     * y reglas de negocio. Cifra la contraseña antes de almacenarla y verifica
     * la unicidad del email en el sistema.</p>
     *
     * <p>Validaciones aplicadas:</p>
     * <ul>
     * <li>Datos básicos del usuario (nombre, email)</li>
     * <li>Formato válido de email</li>
     * <li>Unicidad del email en el sistema</li>
     * <li>Fortaleza de contraseña según políticas de seguridad</li>
     * <li>Cifrado seguro de contraseña con BCrypt</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Usuario nuevoUsuario = new Usuario();
     * nuevoUsuario.setNombre("Juan Pérez");
     * nuevoUsuario.setEmail("juan@email.com");
     * nuevoUsuario.setPassword("MiPassword123!");
     * int id = usuarioService.createUsuario(nuevoUsuario);
     * System.out.println("Usuario creado con ID: " + id);
     * </pre>
     *
     * @param usuario Usuario a crear con datos válidos
     * @return ID único del usuario creado (generado por base de datos)
     * @throws SQLException Si hay error en la inserción
     * @throws IllegalArgumentException Si los datos son inválidos, el email ya existe o la contraseña no es válida
     * @see #validateUsuario(Usuario)
     * @see PasswordUtil#isValidPassword(String)
     * @see PasswordUtil#hashPassword(String)
     */
    public int createUsuario(Usuario usuario) throws SQLException {
        validateUsuario(usuario);
        if (usuarioRepository.emailExists(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (!PasswordUtil.isValidPassword(usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña no válida");
        }
        usuario.setPassword(PasswordUtil.hashPassword(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza un usuario existente en el sistema.
     *
     * <p>Modifica los datos de un usuario previamente registrado, aplicando
     * validaciones y verificando la unicidad del email. Si se proporciona
     * nueva contraseña, la cifra antes de almacenarla.</p>
     *
     * @param usuario Usuario con datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si los datos son inválidos o el email ya está en uso
     * @see #validateUsuario(Usuario)
     */
    public boolean updateUsuario(Usuario usuario) throws SQLException {
        validateUsuario(usuario);
        if (usuarioRepository.emailExistsExcludingUser(usuario.getEmail(), usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            if (!PasswordUtil.isValidPassword(usuario.getPassword())) {
                throw new IllegalArgumentException("Contraseña no válida");
            }
            usuario.setPassword(PasswordUtil.hashPassword(usuario.getPassword()));
        }
        return usuarioRepository.update(usuario);
    }

    /**
     * Elimina un usuario del sistema.
     *
     * <p>Remueve permanentemente un usuario del sistema. Esta operación
     * debe usarse con precaución ya que puede afectar datos relacionados
     * como reportes y rutas favoritas del usuario.</p>
     *
     * @param id ID único del usuario a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     */
    public boolean deleteUsuario(int id) throws SQLException {
        return usuarioRepository.delete(id);
    }

    /**
     * Autentica un usuario en el sistema usando email y contraseña.
     *
     * <p>Verifica las credenciales del usuario comparando la contraseña
     * proporcionada con el hash almacenado en la base de datos. Retorna
     * el usuario autenticado con la contraseña limpiada por seguridad.</p>
     *
     * <p>Proceso de autenticación:</p>
     * <ul>
     * <li>Validación de parámetros de entrada</li>
     * <li>Búsqueda del usuario por email</li>
     * <li>Verificación de contraseña cifrada</li>
     * <li>Limpieza de contraseña antes de retornar</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Usuario usuarioAutenticado = usuarioService.login("juan@email.com", "MiPassword123!");
     * if (usuarioAutenticado != null) {
     *     System.out.println("Login exitoso para: " + usuarioAutenticado.getNombre());
     * } else {
     *     System.out.println("Credenciales inválidas");
     * }
     * </pre>
     *
     * @param email Email del usuario para autenticación
     * @param password Contraseña en texto plano para verificación
     * @return Usuario autenticado con contraseña limpiada o null si las credenciales son incorrectas
     * @throws SQLException Si hay error en la consulta de autenticación
     * @throws IllegalArgumentException Si el email o password están vacíos
     * @see PasswordUtil#verifyPassword(String, String)
     */
    public Usuario login(String email, String password) throws SQLException {
        // Validar parámetros
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email no puede estar vacío");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password no puede estar vacío");
        }

        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email.trim());

        if (usuario != null) {
            // Verificar la contraseña encriptada
            if (PasswordUtil.verifyPassword(password, usuario.getPassword())) {
                // Contraseña correcta - limpiar password antes de devolver
                usuario.setPassword("");
                return usuario;
            }
        }

        // Email no encontrado o contraseña incorrecta
        return null;
    }

    /**
     * Registra un nuevo usuario en el sistema con validaciones mejoradas.
     *
     * <p>Método especializado para registro de usuarios que incluye validaciones
     * adicionales de contraseña con mensajes de error detallados. Aplica
     * todas las reglas de seguridad y cifrado del sistema.</p>
     *
     * <p>Diferencias con createUsuario:</p>
     * <ul>
     * <li>Mensajes de error más detallados para contraseñas</li>
     * <li>Validaciones específicas para proceso de registro</li>
     * <li>Manejo especializado de errores de validación</li>
     * </ul>
     *
     * @param usuario Usuario a registrar con datos completos
     * @return ID único del usuario registrado (generado por base de datos)
     * @throws SQLException Si hay error en la inserción
     * @throws IllegalArgumentException Si los datos son inválidos con mensajes detallados
     * @see #validateUsuario(Usuario)
     * @see PasswordUtil#getPasswordValidationErrors(String)
     */
    public int register(Usuario usuario) throws SQLException {
        // Validar datos del usuario
        validateUsuario(usuario);

        // Verificar que el email no exista
        if (usuarioRepository.emailExists(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar contraseña
        if (!PasswordUtil.isValidPassword(usuario.getPassword())) {
            String[] errors = PasswordUtil.getPasswordValidationErrors(usuario.getPassword());
            throw new IllegalArgumentException("Contraseña no válida: " + String.join(", ", errors));
        }

        // Encriptar contraseña
        usuario.setPassword(PasswordUtil.hashPassword(usuario.getPassword()));

        // Guardar usuario
        return usuarioRepository.save(usuario);
    }

    /**
     * Valida los datos básicos de un usuario.
     * Método privado que aplica reglas de negocio de validación del sistema.
     *
     * <p>Validaciones incluidas:</p>
     * <ul>
     * <li>Usuario no nulo</li>
     * <li>Nombre obligatorio y no vacío</li>
     * <li>Email obligatorio con formato válido</li>
     * <li>Limpieza automática de espacios en blanco</li>
     * <li>Validación de formato de email con expresión regular</li>
     * </ul>
     *
     * @param usuario Usuario a validar
     * @throws IllegalArgumentException Si algún dato es inválido
     */
    private void validateUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser nulo");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre es requerido");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email es requerido");
        }
        if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email no válido");
        }

        // Limpiar espacios
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setEmail(usuario.getEmail().trim());
    }
}