package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.UsuarioController;

/**
 * Configuración de rutas REST para la gestión de usuarios del sistema de transporte.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre usuarios
 * del sistema Wheely, incluyendo funcionalidades de autenticación y registro
 * para permitir el acceso seguro de los usuarios al sistema de transporte público.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /usuarios - Obtiene todos los usuarios del sistema</li>
 * <li>GET /usuarios/{id} - Obtiene un usuario específico</li>
 * <li>POST /usuarios - Crea nuevo usuario</li>
 * <li>PUT /usuarios/{id} - Actualiza usuario existente</li>
 * <li>DELETE /usuarios/{id} - Elimina usuario</li>
 * <li>POST /usuarios/login - Autentica usuario en el sistema</li>
 * <li>POST /usuarios/register - Registra nuevo usuario</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see UsuarioController
 * @see io.javalin.Javalin
 */
public class UsuarioRoutes {
    private final UsuarioController usuarioController;

    /**
     * Constructor para inicializar las rutas de usuarios.
     *
     * @param usuarioController Controlador que maneja la lógica de usuarios
     * @throws IllegalArgumentException si el controlador es null
     */
    public UsuarioRoutes(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de usuarios del sistema,
     * incluyendo endpoints específicos para autenticación y registro que permiten
     * el manejo seguro de credenciales y sesiones de usuario.</p>
     *
     * <p>Mapeo de rutas CRUD básicas:</p>
     * <ul>
     * <li>GET /usuarios → {@link UsuarioController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /usuarios/{id} → {@link UsuarioController#getById(io.javalin.http.Context)}</li>
     * <li>POST /usuarios → {@link UsuarioController#create(io.javalin.http.Context)}</li>
     * <li>PUT /usuarios/{id} → {@link UsuarioController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /usuarios/{id} → {@link UsuarioController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * <p>Mapeo de rutas de autenticación:</p>
     * <ul>
     * <li>POST /usuarios/login → {@link UsuarioController#login(io.javalin.http.Context)}</li>
     * <li>POST /usuarios/register → {@link UsuarioController#register(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see UsuarioController
     */
    public void register(Javalin app) {
        // Rutas CRUD básicas
        app.get("/usuarios", usuarioController::getAll);
        app.get("/usuarios/{id}", usuarioController::getById);
        app.post("/usuarios", usuarioController::create);
        app.put("/usuarios/{id}", usuarioController::update);
        app.delete("/usuarios/{id}", usuarioController::delete);

        // Rutas de autenticación
        app.post("/usuarios/login", usuarioController::login);
        app.post("/usuarios/register", usuarioController::register);
    }
}