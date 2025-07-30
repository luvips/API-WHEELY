package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.CoordenadaController;

/**
 * Configuración de rutas REST para la gestión de coordenadas de recorridos.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre
 * coordenadas geográficas que conforman los recorridos del sistema Wheely,
 * representando puntos GPS ordenados que trazan la ruta del transporte.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /coordenadas - Obtiene todas las coordenadas</li>
 * <li>GET /coordenadas/{id} - Obtiene una coordenada específica</li>
 * <li>POST /coordenadas - Crea nueva coordenada</li>
 * <li>PUT /coordenadas/{id} - Actualiza coordenada existente</li>
 * <li>DELETE /coordenadas/{id} - Elimina coordenada</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see CoordenadaController
 * @see io.javalin.Javalin
 */
public class CoordenadaRoutes {
    private final CoordenadaController coordenadaController;

    /**
     * Constructor para inicializar las rutas de coordenadas.
     *
     * @param coordenadaController Controlador que maneja la lógica de coordenadas
     * @throws IllegalArgumentException si el controlador es null
     */
    public CoordenadaRoutes(CoordenadaController coordenadaController) {
        this.coordenadaController = coordenadaController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de coordenadas de recorridos,
     * estableciendo el mapeo entre URLs y métodos del controlador correspondiente.</p>
     *
     * <p>Mapeo de rutas:</p>
     * <ul>
     * <li>GET /coordenadas → {@link CoordenadaController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /coordenadas/{id} → {@link CoordenadaController#getById(io.javalin.http.Context)}</li>
     * <li>POST /coordenadas → {@link CoordenadaController#create(io.javalin.http.Context)}</li>
     * <li>PUT /coordenadas/{id} → {@link CoordenadaController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /coordenadas/{id} → {@link CoordenadaController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see CoordenadaController
     */
    public void register(Javalin app) {
        app.get("/coordenadas", coordenadaController::getAll);
        app.get("/coordenadas/{id}", coordenadaController::getById);
        app.post("/coordenadas", coordenadaController::create);
        app.put("/coordenadas/{id}", coordenadaController::update);
        app.delete("/coordenadas/{id}", coordenadaController::delete);
    }
}