package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.service.RutaService;
import com.wheely.util.ApiResponse;

/**
 * Controlador REST para la gestión de rutas del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de rutas
 * de transporte público, incluyendo operaciones CRUD básicas y búsquedas específicas.
 * Actúa como intermediario entre las peticiones HTTP y la lógica de negocio del servicio.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de rutas (crear, leer, actualizar, eliminar)</li>
 * <li>Búsqueda de rutas por origen y destino</li>
 * <li>Manejo de respuestas HTTP estandarizadas</li>
 * <li>Gestión de errores y códigos de estado HTTP</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see RutaService
 * @see com.wheely.model.Ruta
 * @see com.wheely.util.ApiResponse
 */
public class RutaController {
    private final RutaService rutaService;

    /**
     * Constructor del controlador de rutas.
     *
     * @param rutaService Servicio que contiene la lógica de negocio para rutas
     */
    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    /**
     * Obtiene todas las rutas registradas en el sistema.
     *
     * <p>Este método maneja peticiones GET a /rutas y retorna una lista completa
     * de todas las rutas disponibles en el sistema de transporte.</p>
     *
     * <pre>
     * GET /rutas
     * Response: {
     *   "success": true,
     *   "message": "Rutas obtenidas",
     *   "data": [...]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see RutaService#getAllRutas()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            ctx.json(ApiResponse.success("Rutas obtenidas", rutaService.getAllRutas()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Obtiene una ruta específica por su identificador único.
     *
     * <p>Busca y retorna los datos de una ruta específica basándose en el ID
     * proporcionado en la URL. Si la ruta no existe, retorna una respuesta 404.</p>
     *
     * <pre>
     * GET /rutas/123
     * Response exitosa: {
     *   "success": true,
     *   "message": "Ruta encontrada",
     *   "data": {...}
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Ruta no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see RutaService#getRutaById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var ruta = rutaService.getRutaById(id);
            ctx.json(ruta != null ? ApiResponse.success("Ruta encontrada", ruta) : ApiResponse.notFound("Ruta"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Crea una nueva ruta en el sistema.
     *
     * <p>Procesa una petición POST para crear una nueva ruta con los datos
     * proporcionados en el cuerpo de la petición JSON. Asigna automáticamente
     * el ID generado por la base de datos.</p>
     *
     * <pre>
     * POST /rutas
     * Body: {
     *   "nombreRuta": "Ruta Centro-Norte",
     *   "origen": "Centro",
     *   "destino": "Norte"
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Ruta creada",
     *   "data": {...}
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos de la ruta en el cuerpo JSON
     * @throws Exception si los datos son inválidos o ocurre error en la base de datos
     *
     * @see RutaService#createRuta(com.wheely.model.Ruta)
     * @see ApiResponse#success(String, Object)
     */
    public void create(Context ctx) {
        try {
            var ruta = ctx.bodyAsClass(com.wheely.model.Ruta.class);
            int id = rutaService.createRuta(ruta);
            ruta.setIdRuta(id);
            ctx.status(HttpStatus.CREATED).json(ApiResponse.success("Ruta creada", ruta));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Actualiza una ruta existente con nuevos datos.
     *
     * <p>Modifica los datos de una ruta específica identificada por su ID.
     * Si la ruta no existe, retorna una respuesta 404.</p>
     *
     * <pre>
     * PUT /rutas/123
     * Body: {
     *   "nombreRuta": "Ruta Centro-Norte Actualizada",
     *   "origen": "Centro Actualizado",
     *   "destino": "Norte Actualizado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID en la URL y datos actualizados en el cuerpo
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la actualización
     *
     * @see RutaService#updateRuta(com.wheely.model.Ruta)
     * @see ApiResponse#success(String, Object)
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var ruta = ctx.bodyAsClass(com.wheely.model.Ruta.class);
            ruta.setIdRuta(id);
            ctx.json(rutaService.updateRuta(ruta) ?
                    ApiResponse.success("Ruta actualizada", ruta) : ApiResponse.notFound("Ruta"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Elimina una ruta del sistema por su ID.
     *
     * <p>Remueve permanentemente una ruta del sistema. Esta operación no puede
     * ser revertida. Si la ruta no existe, retorna una respuesta 404.</p>
     *
     * <pre>
     * DELETE /rutas/123
     * Response exitosa: {
     *   "success": true,
     *   "message": "Ruta eliminada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID de la ruta a eliminar
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la eliminación
     *
     * @see RutaService#deleteRuta(int)
     * @see ApiResponse#success(String)
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(rutaService.deleteRuta(id) ?
                    ApiResponse.success("Ruta eliminada") : ApiResponse.notFound("Ruta"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Busca rutas basándose en criterios de origen y destino.
     *
     * <p>Permite filtrar rutas específicas mediante parámetros de consulta
     * para encontrar rutas que coincidan con el origen y/o destino especificados.
     * Útil para planificación de viajes de usuarios.</p>
     *
     * <pre>
     * GET /rutas/buscar?origen=Centro&destino=Norte
     * Response: {
     *   "success": true,
     *   "message": "Rutas encontradas",
     *   "data": [...]
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene parámetros de consulta 'origen' y 'destino'
     * @throws Exception si ocurre error en la búsqueda
     *
     * @see RutaService#buscarRutasPorOrigenDestino(String, String)
     * @see ApiResponse#success(String, Object)
     */
    public void buscarPorOrigenDestino(Context ctx) {
        try {
            String origen = ctx.queryParam("origen");
            String destino = ctx.queryParam("destino");
            ctx.json(ApiResponse.success("Rutas encontradas", rutaService.buscarRutasPorOrigenDestino(origen, destino)));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }
}