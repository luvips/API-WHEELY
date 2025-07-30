package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.PeriodoController;

/**
 * Configuración de rutas REST para la gestión de periodos del sistema de transporte.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre periodos
 * del sistema Wheely, que representan los diferentes marcos temporales utilizados
 * para organizar y analizar los tiempos de rutas en distintas épocas del año.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /api/periodos - Obtiene todos los periodos</li>
 * <li>GET /api/periodos/{id} - Obtiene un periodo específico</li>
 * <li>POST /api/periodos - Crea nuevo periodo</li>
 * <li>PUT /api/periodos/{id} - Actualiza periodo existente</li>
 * <li>DELETE /api/periodos/{id} - Elimina periodo</li>
 * <li>GET /api/periodos/nombre/{nombre} - Busca periodo por nombre</li>
 * <li>GET /api/periodos/actual - Obtiene el periodo actual</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see PeriodoController
 * @see io.javalin.Javalin
 */
public class PeriodoRoutes {
    private final PeriodoController periodoController;

    /**
     * Constructor para inicializar las rutas de periodos.
     *
     * @param periodoController Controlador que maneja la lógica de periodos
     * @throws IllegalArgumentException si el controlador es null
     */
    public PeriodoRoutes(PeriodoController periodoController) {
        this.periodoController = periodoController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de periodos del sistema,
     * incluyendo endpoints específicos para búsqueda por nombre y obtención
     * del periodo actualmente vigente en el sistema.</p>
     *
     * <p>Mapeo de rutas principales CRUD:</p>
     * <ul>
     * <li>GET /api/periodos → {@link PeriodoController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /api/periodos/{id} → {@link PeriodoController#getById(io.javalin.http.Context)}</li>
     * <li>POST /api/periodos → {@link PeriodoController#create(io.javalin.http.Context)}</li>
     * <li>PUT /api/periodos/{id} → {@link PeriodoController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /api/periodos/{id} → {@link PeriodoController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * <p>Mapeo de rutas adicionales específicas:</p>
     * <ul>
     * <li>GET /api/periodos/nombre/{nombre} → {@link PeriodoController#getByNombre(io.javalin.http.Context)}</li>
     * <li>GET /api/periodos/actual → {@link PeriodoController#getActual(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see PeriodoController
     */
    public void register(Javalin app) {
        // Rutas principales de CRUD
        app.get("/api/periodos", periodoController::getAll);
        app.get("/api/periodos/{id}", periodoController::getById);
        app.post("/api/periodos", periodoController::create);
        app.put("/api/periodos/{id}", periodoController::update);
        app.delete("/api/periodos/{id}", periodoController::delete);

        // Rutas adicionales específicas
        app.get("/api/periodos/nombre/{nombre}", periodoController::getByNombre);
        app.get("/api/periodos/actual", periodoController::getActual);
    }
}