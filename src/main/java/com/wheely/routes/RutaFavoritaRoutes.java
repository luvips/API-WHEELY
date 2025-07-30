package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.RutaFavoritaController;

/**
 * Configuración de rutas REST para la gestión de rutas favoritas de usuarios.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD relacionadas
 * con rutas favoritas, permitiendo a los usuarios gestionar sus rutas preferidas
 * del sistema de transporte Wheely.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /rutas-favoritas - Lista todas las rutas favoritas</li>
 * <li>GET /rutas-favoritas/{id} - Obtiene una ruta favorita específica</li>
 * <li>GET /usuarios/{usuarioId}/rutas-favoritas - Obtiene favoritas de un usuario</li>
 * <li>POST /usuarios/{usuarioId}/rutas-favoritas - Agrega una ruta favorita</li>
 * <li>DELETE /usuarios/{usuarioId}/rutas-favoritas/{rutaId} - Elimina una favorita</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see RutaFavoritaController
 * @see io.javalin.Javalin
 */
public class RutaFavoritaRoutes {
    private final RutaFavoritaController rutaFavoritaController;

    /**
     * Constructor para inicializar las rutas de rutas favoritas.
     *
     * @param rutaFavoritaController Controlador que maneja la lógica de rutas favoritas
     * @throws IllegalArgumentException si el controlador es null
     */
    public RutaFavoritaRoutes(RutaFavoritaController rutaFavoritaController) {
        this.rutaFavoritaController = rutaFavoritaController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de rutas favoritas,
     * incluyendo endpoints específicos para usuarios y gestión de favoritos.</p>
     *
     * <p>Mapeo de rutas:</p>
     * <ul>
     * <li>GET /rutas-favoritas → {@link RutaFavoritaController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /rutas-favoritas/{id} → {@link RutaFavoritaController#getById(io.javalin.http.Context)}</li>
     * <li>GET /usuarios/{usuarioId}/rutas-favoritas → {@link RutaFavoritaController#getByUsuario(io.javalin.http.Context)}</li>
     * <li>POST /usuarios/{usuarioId}/rutas-favoritas → {@link RutaFavoritaController#agregarFavorita(io.javalin.http.Context)}</li>
     * <li>DELETE /usuarios/{usuarioId}/rutas-favoritas/{rutaId} → {@link RutaFavoritaController#eliminarFavorita(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see RutaFavoritaController
     */
    public void register(Javalin app) {
        app.get("/rutas-favoritas", rutaFavoritaController::getAll);
        app.get("/rutas-favoritas/{id}", rutaFavoritaController::getById);
        app.get("/usuarios/{usuarioId}/rutas-favoritas", rutaFavoritaController::getByUsuario);
        app.post("/usuarios/{usuarioId}/rutas-favoritas", rutaFavoritaController::agregarFavorita);
        app.delete("/usuarios/{usuarioId}/rutas-favoritas/{rutaId}", rutaFavoritaController::eliminarFavorita);
    }
}