package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.CoordenadaParadaController;

/**
 * Configuración de rutas REST para la gestión de coordenadas de paradas.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre
 * coordenadas geográficas de paradas del sistema de transporte Wheely,
 * permitiendo gestionar ubicaciones precisas con latitud y longitud.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /coordenadas-parada - Obtiene todas las coordenadas de paradas</li>
 * <li>GET /coordenadas-parada/{id} - Obtiene una coordenada específica</li>
 * <li>POST /coordenadas-parada - Crea nueva coordenada de parada</li>
 * <li>PUT /coordenadas-parada/{id} - Actualiza coordenada existente</li>
 * <li>DELETE /coordenadas-parada/{id} - Elimina coordenada de parada</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see CoordenadaParadaController
 * @see io.javalin.Javalin
 */
public class CoordenadaParadaRoutes {
    private final CoordenadaParadaController coordenadaParadaController;

    /**
     * Constructor para inicializar las rutas de coordenadas de paradas.
     *
     * @param coordenadaParadaController Controlador que maneja la lógica de coordenadas de paradas
     * @throws IllegalArgumentException si el controlador es null
     */
    public CoordenadaParadaRoutes(CoordenadaParadaController coordenadaParadaController) {
        this.coordenadaParadaController = coordenadaParadaController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de coordenadas de paradas,
     * estableciendo el mapeo entre URLs y métodos del controlador correspondiente.</p>
     *
     * <p>Mapeo de rutas:</p>
     * <ul>
     * <li>GET /coordenadas-parada → {@link CoordenadaParadaController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /coordenadas-parada/{id} → {@link CoordenadaParadaController#getById(io.javalin.http.Context)}</li>
     * <li>POST /coordenadas-parada → {@link CoordenadaParadaController#create(io.javalin.http.Context)}</li>
     * <li>PUT /coordenadas-parada/{id} → {@link CoordenadaParadaController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /coordenadas-parada/{id} → {@link CoordenadaParadaController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see CoordenadaParadaController
     */
    public void register(Javalin app) {
        app.get("/coordenadas-parada", coordenadaParadaController::getAll);
        app.get("/coordenadas-parada/{id}", coordenadaParadaController::getById);
        app.post("/coordenadas-parada", coordenadaParadaController::create);
        app.put("/coordenadas-parada/{id}", coordenadaParadaController::update);
        app.delete("/coordenadas-parada/{id}", coordenadaParadaController::delete);
    }
}