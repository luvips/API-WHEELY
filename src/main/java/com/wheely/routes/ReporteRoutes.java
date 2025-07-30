package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.ReporteController;

/**
 * Configuración de rutas REST para la gestión de reportes del sistema de transporte.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre reportes
 * del sistema Wheely, que permiten a los usuarios registrar incidencias, sugerencias,
 * quejas y otros tipos de retroalimentación relacionados con el servicio de transporte.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /reportes - Obtiene todos los reportes del sistema</li>
 * <li>GET /reportes/{id} - Obtiene un reporte específico</li>
 * <li>POST /reportes - Crea nuevo reporte</li>
 * <li>PUT /reportes/{id} - Actualiza reporte existente</li>
 * <li>DELETE /reportes/{id} - Elimina reporte</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see ReporteController
 * @see io.javalin.Javalin
 */
public class ReporteRoutes {
    private final ReporteController reporteController;

    /**
     * Constructor para inicializar las rutas de reportes.
     *
     * @param reporteController Controlador que maneja la lógica de reportes
     * @throws IllegalArgumentException si el controlador es null
     */
    public ReporteRoutes(ReporteController reporteController) {
        this.reporteController = reporteController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de reportes del sistema,
     * permitiendo la gestión completa de la retroalimentación de usuarios sobre
     * el servicio de transporte público.</p>
     *
     * <p>Mapeo de rutas:</p>
     * <ul>
     * <li>GET /reportes → {@link ReporteController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /reportes/{id} → {@link ReporteController#getById(io.javalin.http.Context)}</li>
     * <li>POST /reportes → {@link ReporteController#create(io.javalin.http.Context)}</li>
     * <li>PUT /reportes/{id} → {@link ReporteController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /reportes/{id} → {@link ReporteController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see ReporteController
     */
    public void register(Javalin app) {
        app.get("/reportes", reporteController::getAll);
        app.get("/reportes/{id}", reporteController::getById);
        app.post("/reportes", reporteController::create);
        app.put("/reportes/{id}", reporteController::update);
        app.delete("/reportes/{id}", reporteController::delete);
    }
}