package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.TiempoRutaPeriodoController;

/**
 * Configuración de rutas REST para la gestión de tiempos de rutas por periodo.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre los registros
 * de tiempos de rutas asociados a periodos específicos del sistema Wheely. Permite
 * gestionar y consultar los tiempos de duración de las rutas en diferentes marcos
 * temporales, facilitando el análisis histórico y estacional del transporte.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /api/tiempos-ruta-periodo - Obtiene todos los registros de tiempos</li>
 * <li>GET /api/tiempos-ruta-periodo/{id} - Obtiene un registro específico</li>
 * <li>POST /api/tiempos-ruta-periodo - Crea nuevo registro de tiempo</li>
 * <li>PUT /api/tiempos-ruta-periodo/{id} - Actualiza registro existente</li>
 * <li>DELETE /api/tiempos-ruta-periodo/{id} - Elimina registro</li>
 * <li>GET /api/tiempos-ruta-periodo/ruta/{idRuta} - Obtiene tiempos por ruta</li>
 * <li>GET /api/tiempos-ruta-periodo/periodo/{idPeriodo} - Obtiene tiempos por periodo</li>
 * <li>GET /api/tiempos-ruta-periodo/ruta/{idRuta}/periodo/{idPeriodo} - Obtiene tiempo específico por ruta y periodo</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see TiempoRutaPeriodoController
 * @see io.javalin.Javalin
 */
public class TiempoRutaPeriodoRoutes {
    private final TiempoRutaPeriodoController tiempoRutaPeriodoController;

    /**
     * Constructor para inicializar las rutas de tiempos de rutas por periodo.
     *
     * @param tiempoRutaPeriodoController Controlador que maneja la lógica de tiempos de rutas por periodo
     * @throws IllegalArgumentException si el controlador es null
     */
    public TiempoRutaPeriodoRoutes(TiempoRutaPeriodoController tiempoRutaPeriodoController) {
        this.tiempoRutaPeriodoController = tiempoRutaPeriodoController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de tiempos de rutas por periodo,
     * incluyendo endpoints especializados para consultas por ruta específica, por periodo
     * específico, y combinaciones de ambos parámetros para análisis detallado.</p>
     *
     * <p>Mapeo de rutas CRUD básicas:</p>
     * <ul>
     * <li>GET /api/tiempos-ruta-periodo → {@link TiempoRutaPeriodoController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /api/tiempos-ruta-periodo/{id} → {@link TiempoRutaPeriodoController#getById(io.javalin.http.Context)}</li>
     * <li>POST /api/tiempos-ruta-periodo → {@link TiempoRutaPeriodoController#create(io.javalin.http.Context)}</li>
     * <li>PUT /api/tiempos-ruta-periodo/{id} → {@link TiempoRutaPeriodoController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /api/tiempos-ruta-periodo/{id} → {@link TiempoRutaPeriodoController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * <p>Mapeo de rutas de consulta especializada:</p>
     * <ul>
     * <li>GET /api/tiempos-ruta-periodo/ruta/{idRuta} → {@link TiempoRutaPeriodoController#getByRuta(io.javalin.http.Context)}</li>
     * <li>GET /api/tiempos-ruta-periodo/periodo/{idPeriodo} → {@link TiempoRutaPeriodoController#getByPeriodo(io.javalin.http.Context)}</li>
     * <li>GET /api/tiempos-ruta-periodo/ruta/{idRuta}/periodo/{idPeriodo} → {@link TiempoRutaPeriodoController#getByRutaAndPeriodo(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see TiempoRutaPeriodoController
     */
    public void register(Javalin app) {
        app.get("/api/tiempos-ruta-periodo", tiempoRutaPeriodoController::getAll);
        app.get("/api/tiempos-ruta-periodo/{id}", tiempoRutaPeriodoController::getById);
        app.post("/api/tiempos-ruta-periodo", tiempoRutaPeriodoController::create);
        app.put("/api/tiempos-ruta-periodo/{id}", tiempoRutaPeriodoController::update);
        app.delete("/api/tiempos-ruta-periodo/{id}", tiempoRutaPeriodoController::delete);
        app.get("/api/tiempos-ruta-periodo/ruta/{idRuta}", tiempoRutaPeriodoController::getByRuta);
        app.get("/api/tiempos-ruta-periodo/periodo/{idPeriodo}", tiempoRutaPeriodoController::getByPeriodo);
        app.get("/api/tiempos-ruta-periodo/ruta/{idRuta}/periodo/{idPeriodo}", tiempoRutaPeriodoController::getByRutaAndPeriodo);
    }
}