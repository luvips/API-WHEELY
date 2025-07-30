package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.RutaController;

/**
 * Configuración de rutas REST para la gestión de rutas del sistema de transporte.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre rutas
 * del sistema Wheely, que representan las líneas principales de transporte público
 * con origen y destino específicos que conectan diferentes ubicaciones de la ciudad.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /rutas - Obtiene todas las rutas del sistema</li>
 * <li>GET /rutas/{id} - Obtiene una ruta específica</li>
 * <li>POST /rutas - Crea nueva ruta</li>
 * <li>PUT /rutas/{id} - Actualiza ruta existente</li>
 * <li>DELETE /rutas/{id} - Elimina ruta</li>
 * <li>GET /rutas/buscar - Busca rutas por origen y destino</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see RutaController
 * @see io.javalin.Javalin
 */
public class RutaRoutes {
    private final RutaController rutaController;

    /**
     * Constructor para inicializar las rutas de rutas.
     *
     * @param rutaController Controlador que maneja la lógica de rutas
     * @throws IllegalArgumentException si el controlador es null
     */
    public RutaRoutes(RutaController rutaController) {
        this.rutaController = rutaController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de rutas del sistema,
     * incluyendo funcionalidad de búsqueda por origen y destino para facilitar
     * la consulta de rutas específicas a los usuarios.</p>
     *
     * <p>Mapeo de rutas:</p>
     * <ul>
     * <li>GET /rutas → {@link RutaController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /rutas/{id} → {@link RutaController#getById(io.javalin.http.Context)}</li>
     * <li>POST /rutas → {@link RutaController#create(io.javalin.http.Context)}</li>
     * <li>PUT /rutas/{id} → {@link RutaController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /rutas/{id} → {@link RutaController#delete(io.javalin.http.Context)}</li>
     * <li>GET /rutas/buscar → {@link RutaController#buscarPorOrigenDestino(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see RutaController
     */
    public void register(Javalin app) {
        app.get("/rutas", rutaController::getAll);
        app.get("/rutas/{id}", rutaController::getById);
        app.post("/rutas", rutaController::create);
        app.put("/rutas/{id}", rutaController::update);
        app.delete("/rutas/{id}", rutaController::delete);
        app.get("/rutas/buscar", rutaController::buscarPorOrigenDestino);
    }
}