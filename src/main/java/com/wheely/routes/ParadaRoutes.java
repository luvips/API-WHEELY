package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.ParadaController;

/**
 * Configuración de rutas REST para la gestión de paradas del sistema de transporte.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre paradas
 * del sistema Wheely, que representan las estaciones físicas donde los usuarios
 * abordan y descienden del transporte público.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /paradas - Obtiene todas las paradas del sistema</li>
 * <li>GET /paradas/{id} - Obtiene una parada específica</li>
 * <li>POST /paradas - Crea nueva parada</li>
 * <li>PUT /paradas/{id} - Actualiza parada existente</li>
 * <li>DELETE /paradas/{id} - Elimina parada</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see ParadaController
 * @see io.javalin.Javalin
 */
public class ParadaRoutes {
    private final ParadaController paradaController;

    /**
     * Constructor para inicializar las rutas de paradas.
     *
     * @param paradaController Controlador que maneja la lógica de paradas
     * @throws IllegalArgumentException si el controlador es null
     */
    public ParadaRoutes(ParadaController paradaController) {
        this.paradaController = paradaController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de paradas del sistema,
     * estableciendo el mapeo entre URLs y métodos del controlador correspondiente.</p>
     *
     * <p>Mapeo de rutas:</p>
     * <ul>
     * <li>GET /paradas → {@link ParadaController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /paradas/{id} → {@link ParadaController#getById(io.javalin.http.Context)}</li>
     * <li>POST /paradas → {@link ParadaController#create(io.javalin.http.Context)}</li>
     * <li>PUT /paradas/{id} → {@link ParadaController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /paradas/{id} → {@link ParadaController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see ParadaController
     */
    public void register(Javalin app) {
        app.get("/paradas", paradaController::getAll);
        app.get("/paradas/{id}", paradaController::getById);
        app.post("/paradas", paradaController::create);
        app.put("/paradas/{id}", paradaController::update);
        app.delete("/paradas/{id}", paradaController::delete);
    }
}