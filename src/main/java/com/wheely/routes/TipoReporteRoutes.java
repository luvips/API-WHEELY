package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.TipoReporteController;

/**
 * Configuración de rutas REST para la gestión de tipos de reportes del sistema.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre tipos de reportes
 * del sistema Wheely, que categorizan y clasifican los diferentes tipos de retroalimentación
 * que pueden enviar los usuarios (incidencias, sugerencias, quejas, elogios, etc.).</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /tipos-reporte - Obtiene todos los tipos de reportes</li>
 * <li>GET /tipos-reporte/{id} - Obtiene un tipo de reporte específico</li>
 * <li>POST /tipos-reporte - Crea nuevo tipo de reporte</li>
 * <li>PUT /tipos-reporte/{id} - Actualiza tipo de reporte existente</li>
 * <li>DELETE /tipos-reporte/{id} - Elimina tipo de reporte</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see TipoReporteController
 * @see io.javalin.Javalin
 */
public class TipoReporteRoutes {
    private final TipoReporteController tipoReporteController;

    /**
     * Constructor para inicializar las rutas de tipos de reportes.
     *
     * @param tipoReporteController Controlador que maneja la lógica de tipos de reportes
     * @throws IllegalArgumentException si el controlador es null
     */
    public TipoReporteRoutes(TipoReporteController tipoReporteController) {
        this.tipoReporteController = tipoReporteController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de tipos de reportes del sistema,
     * permitiendo la gestión de las categorías utilizadas para clasificar la
     * retroalimentación de los usuarios del transporte público.</p>
     *
     * <p>Mapeo de rutas:</p>
     * <ul>
     * <li>GET /tipos-reporte → {@link TipoReporteController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /tipos-reporte/{id} → {@link TipoReporteController#getById(io.javalin.http.Context)}</li>
     * <li>POST /tipos-reporte → {@link TipoReporteController#create(io.javalin.http.Context)}</li>
     * <li>PUT /tipos-reporte/{id} → {@link TipoReporteController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /tipos-reporte/{id} → {@link TipoReporteController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see TipoReporteController
     */
    public void register(Javalin app) {
        app.get("/tipos-reporte", tipoReporteController::getAll);
        app.get("/tipos-reporte/{id}", tipoReporteController::getById);
        app.post("/tipos-reporte", tipoReporteController::create);
        app.put("/tipos-reporte/{id}", tipoReporteController::update);
        app.delete("/tipos-reporte/{id}", tipoReporteController::delete);
    }
}