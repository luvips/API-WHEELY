package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.RecorridoController;

/**
 * Configuración de rutas REST para la gestión de recorridos del sistema de transporte.
 *
 * <p>Esta clase define los endpoints HTTP para operaciones CRUD sobre recorridos
 * del sistema Wheely, que representan las trayectorias específicas que siguen
 * los vehículos de transporte público dentro de una ruta determinada.</p>
 *
 * <p>Endpoints disponibles:</p>
 * <ul>
 * <li>GET /recorridos - Obtiene todos los recorridos del sistema</li>
 * <li>GET /recorridos/{id} - Obtiene un recorrido específico</li>
 * <li>POST /recorridos - Crea nuevo recorrido</li>
 * <li>PUT /recorridos/{id} - Actualiza recorrido existente</li>
 * <li>DELETE /recorridos/{id} - Elimina recorrido</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see RecorridoController
 * @see io.javalin.Javalin
 */
public class RecorridoRoutes {
    private final RecorridoController recorridoController;

    /**
     * Constructor para inicializar las rutas de recorridos.
     *
     * @param recorridoController Controlador que maneja la lógica de recorridos
     * @throws IllegalArgumentException si el controlador es null
     */
    public RecorridoRoutes(RecorridoController recorridoController) {
        this.recorridoController = recorridoController;
    }

    /**
     * Registra todos los endpoints HTTP en la aplicación Javalin.
     *
     * <p>Define las rutas REST para operaciones CRUD de recorridos del sistema,
     * estableciendo el mapeo entre URLs y métodos del controlador correspondiente.</p>
     *
     * <p>Mapeo de rutas:</p>
     * <ul>
     * <li>GET /recorridos → {@link RecorridoController#getAll(io.javalin.http.Context)}</li>
     * <li>GET /recorridos/{id} → {@link RecorridoController#getById(io.javalin.http.Context)}</li>
     * <li>POST /recorridos → {@link RecorridoController#create(io.javalin.http.Context)}</li>
     * <li>PUT /recorridos/{id} → {@link RecorridoController#update(io.javalin.http.Context)}</li>
     * <li>DELETE /recorridos/{id} → {@link RecorridoController#delete(io.javalin.http.Context)}</li>
     * </ul>
     *
     * @param app Instancia de Javalin donde se registran las rutas
     * @throws IllegalArgumentException si app es null
     * @see RecorridoController
     */
    public void register(Javalin app) {
        app.get("/recorridos", recorridoController::getAll);
        app.get("/recorridos/{id}", recorridoController::getById);
        app.post("/recorridos", recorridoController::create);
        app.put("/recorridos/{id}", recorridoController::update);
        app.delete("/recorridos/{id}", recorridoController::delete);
    }
}