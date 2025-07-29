package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.model.Usuario;
import com.wheely.service.UsuarioService;
import com.wheely.util.ApiResponse;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador REST para la gestión de usuarios del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de usuarios
 * del sistema de transporte, incluyendo operaciones CRUD básicas y funcionalidades de
 * autenticación (login y registro). Actúa como intermediario entre las peticiones HTTP
 * y la lógica de negocio del servicio.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de usuarios (crear, leer, actualizar, eliminar)</li>
 * <li>Sistema de autenticación con login y registro</li>
 * <li>Encriptación de contraseñas con BCrypt</li>
 * <li>Validación de datos de entrada</li>
 * <li>Manejo de respuestas HTTP estandarizadas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see UsuarioService
 * @see com.wheely.model.Usuario
 * @see com.wheely.util.ApiResponse
 */
public class UsuarioController {
    private final UsuarioService usuarioService;

    /**
     * Constructor del controlador de usuarios.
     *
     * @param usuarioService Servicio que contiene la lógica de negocio para usuarios
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * <p>Este método maneja peticiones GET a /usuarios y retorna una lista completa
     * de todos los usuarios registrados en el sistema, sin incluir las contraseñas
     * por seguridad.</p>
     *
     * <pre>
     * GET /usuarios
     * Response: {
     *   "success": true,
     *   "message": "Usuarios obtenidos",
     *   "data": [...]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws SQLException si ocurre error en la consulta a la base de datos
     *
     * @see UsuarioService#getAllUsuarios()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            ctx.json(ApiResponse.success("Usuarios obtenidos", usuarios));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener usuarios"));
        }
    }

    /**
     * Obtiene un usuario específico por su identificador único.
     *
     * <p>Busca y retorna los datos de un usuario específico basándose en el ID
     * proporcionado en la URL. La contraseña no se incluye en la respuesta por seguridad.</p>
     *
     * <pre>
     * GET /usuarios/123
     * Response exitosa: {
     *   "success": true,
     *   "message": "Usuario encontrado",
     *   "data": {...}
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Usuario no encontrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws SQLException si ocurre error en la consulta a la base de datos
     *
     * @see UsuarioService#getUsuarioById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = usuarioService.getUsuarioById(id);
            if (usuario != null) {
                ctx.json(ApiResponse.success("Usuario encontrado", usuario));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Usuario"));
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error("ID no válido"));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener usuario"));
        }
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * <p>Procesa una petición POST para crear un nuevo usuario con los datos
     * proporcionados en el cuerpo de la petición JSON. La contraseña se encripta
     * automáticamente antes de almacenarla.</p>
     *
     * <pre>
     * POST /usuarios
     * Body: {
     *   "nombre": "Juan Pérez",
     *   "email": "juan@email.com",
     *   "password": "password123"
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Usuario creado",
     *   "data": {...}
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos del usuario en el cuerpo JSON
     * @throws IllegalArgumentException si los datos del usuario son inválidos
     * @throws SQLException si ocurre error en la base de datos
     *
     * @see UsuarioService#createUsuario(Usuario)
     * @see ApiResponse#success(String, Object)
     */
    public void create(Context ctx) {
        try {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            int id = usuarioService.createUsuario(usuario);
            usuario.setIdUsuario(id);
            usuario.setPassword(""); // Limpiar password
            ctx.status(HttpStatus.CREATED)
                    .json(ApiResponse.success("Usuario creado", usuario));
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al crear usuario"));
        }
    }

    /**
     * Actualiza un usuario existente con nuevos datos.
     *
     * <p>Modifica los datos de un usuario específico identificado por su ID.
     * Si se proporciona una nueva contraseña, se encripta automáticamente.</p>
     *
     * <pre>
     * PUT /usuarios/123
     * Body: {
     *   "nombre": "Juan Pérez Actualizado",
     *   "email": "juan.nuevo@email.com"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID en la URL y datos actualizados en el cuerpo
     * @throws NumberFormatException si el ID no es un número válido
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws SQLException si ocurre error en la actualización
     *
     * @see UsuarioService#updateUsuario(Usuario)
     * @see ApiResponse#success(String, Object)
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            usuario.setIdUsuario(id);

            boolean updated = usuarioService.updateUsuario(usuario);
            if (updated) {
                usuario.setPassword(""); // Limpiar password
                ctx.json(ApiResponse.success("Usuario actualizado", usuario));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Usuario"));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al actualizar usuario"));
        }
    }

    /**
     * Elimina un usuario del sistema por su ID.
     *
     * <p>Remueve permanentemente un usuario del sistema. Esta operación no puede
     * ser revertida. También elimina datos relacionados como reportes y rutas favoritas.</p>
     *
     * <pre>
     * DELETE /usuarios/123
     * Response exitosa: {
     *   "success": true,
     *   "message": "Usuario eliminado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID del usuario a eliminar
     * @throws SQLException si ocurre error en la eliminación
     *
     * @see UsuarioService#deleteUsuario(int)
     * @see ApiResponse#success(String)
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean deleted = usuarioService.deleteUsuario(id);
            if (deleted) {
                ctx.json(ApiResponse.success("Usuario eliminado"));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Usuario"));
            }
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al eliminar usuario"));
        }
    }

    /**
     * Autentica un usuario en el sistema mediante email y contraseña.
     *
     * <p>Valida las credenciales del usuario verificando el email y contraseña
     * encriptada. Si las credenciales son correctas, retorna los datos del usuario
     * (sin la contraseña) para iniciar sesión.</p>
     *
     * <pre>
     * POST /usuarios/login
     * Body: {
     *   "email": "juan@email.com",
     *   "password": "password123"
     * }
     *
     * Response exitosa: {
     *   "success": true,
     *   "message": "Login exitoso",
     *   "data": {...}
     * }
     *
     * Response credenciales incorrectas: {
     *   "success": false,
     *   "message": "Email o contraseña incorrectos"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene email y password en el cuerpo JSON
     * @throws IllegalArgumentException si faltan campos requeridos
     * @throws SQLException si ocurre error en la consulta
     *
     * @see UsuarioService#login(String, String)
     * @see LoginRequest
     */
    public void login(Context ctx) {
        try {
            var loginData = ctx.bodyAsClass(LoginRequest.class);

            // Validar que se proporcionen email y password
            if (loginData.email == null || loginData.email.trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST)
                        .json(ApiResponse.error("Email es requerido"));
                return;
            }

            if (loginData.password == null || loginData.password.trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST)
                        .json(ApiResponse.error("Password es requerido"));
                return;
            }

            // Usar el método login del servicio que verifica contraseñas encriptadas
            Usuario usuario = usuarioService.login(loginData.email.trim(), loginData.password);

            if (usuario != null) {
                // Login exitoso - el password ya fue limpiado en el servicio
                ctx.json(ApiResponse.success("Login exitoso", usuario));
            } else {
                // Credenciales incorrectas
                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(ApiResponse.error("Email o contraseña incorrectos"));
            }

        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error interno del servidor"));
        } catch (Exception e) {
            // Log del error para debugging
            System.err.println("Error inesperado en login: " + e.getMessage());
            e.printStackTrace();
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error inesperado en el servidor"));
        }
    }

    /**
     * Registra un nuevo usuario en el sistema con validaciones completas.
     *
     * <p>Crea un nuevo usuario aplicando todas las validaciones de negocio,
     * verificando que el email sea único y encriptando la contraseña con BCrypt.
     * Diseñado específicamente para el proceso de registro desde el frontend.</p>
     *
     * <pre>
     * POST /usuarios/register
     * Body: {
     *   "nombre": "María García",
     *   "email": "maria@email.com",
     *   "password": "password123"
     * }
     *
     * Response exitosa: {
     *   "success": true,
     *   "message": "Usuario registrado exitosamente",
     *   "data": {...}
     * }
     *
     * Response email duplicado: {
     *   "success": false,
     *   "message": "El email ya está registrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos de registro en el cuerpo JSON
     * @throws IllegalArgumentException si los datos de registro son inválidos
     * @throws SQLException si ocurre error en la base de datos o email duplicado
     *
     * @see UsuarioService#register(Usuario)
     * @see ApiResponse#success(String, Object)
     */
    public void register(Context ctx) {
        try {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);

            // Validar datos básicos
            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST)
                        .json(ApiResponse.error("Nombre es requerido"));
                return;
            }

            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST)
                        .json(ApiResponse.error("Email es requerido"));
                return;
            }

            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST)
                        .json(ApiResponse.error("Password es requerido"));
                return;
            }

            // Usar el método register del servicio que encripta la contraseña
            int id = usuarioService.register(usuario);
            usuario.setIdUsuario(id);
            usuario.setPassword(""); // Limpiar password antes de devolver

            ctx.status(HttpStatus.CREATED)
                    .json(ApiResponse.success("Usuario registrado exitosamente", usuario));

        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            // Verificar si es error de email duplicado
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                ctx.status(HttpStatus.CONFLICT)
                        .json(ApiResponse.error("El email ya está registrado"));
            } else {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .json(ApiResponse.error("Error al registrar usuario"));
            }
        } catch (Exception e) {
            System.err.println("Error inesperado en registro: " + e.getMessage());
            e.printStackTrace();
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error inesperado en el servidor"));
        }
    }

    /**
     * Clase interna para solicitudes de login.
     *
     * <p>Encapsula los datos necesarios para el proceso de autenticación,
     * proporcionando una estructura clara para las peticiones de login.</p>
     *
     * @since 2025
     */
    public static class LoginRequest {
        /** Email del usuario para autenticación */
        public String email;
        /** Contraseña del usuario para autenticación */
        public String password;

        /**
         * Constructor vacío requerido para deserialización JSON.
         */
        public LoginRequest() {}

        /**
         * Constructor con parámetros para crear solicitudes de login.
         *
         * @param email Email del usuario
         * @param password Contraseña del usuario
         */
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}