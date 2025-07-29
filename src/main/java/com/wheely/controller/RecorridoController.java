package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.service.RecorridoService;
import com.wheely.util.ApiResponse;

/**
 * Controlador REST para la gestión de recorridos del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de recorridos
 * específicos de las rutas de transporte público. Los recorridos representan el trazado
 * geográfico detallado que siguen los vehículos dentro de una ruta, incluyendo
 * coordenadas específicas y datos geoespaciales para visualización en mapas.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de recorridos (crear, leer, actualizar, eliminar)</li>
 * <li>Administración de archivos GeoJSON para trazado de rutas</li>
 * <li>Control de estado activo/inactivo de recorridos</li>
 * <li>Asociación de recorridos con rutas específicas</li>
 * <li>Manejo de datos geoespaciales para sistemas de mapas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see RecorridoService
 * @see com.wheely.model.Recorrido
 * @see com.wheely.util.ApiResponse
 */
public class RecorridoController {
    private final RecorridoService recorridoService;

    /**
     * Constructor del controlador de recorridos.
     *
     * @param recorridoService Servicio que contiene la lógica de negocio para recorridos
     */
    public RecorridoController(RecorridoService recorridoService) {
        this.recorridoService = recorridoService;
    }

    /**
     * Obtiene todos los recorridos registrados en el sistema.
     *
     * <p>Este método maneja peticiones GET a /recorridos y retorna una lista completa
     * de todos los recorridos disponibles, ordenados por ruta y nombre de archivo.
     * Cada recorrido incluye información sobre la ruta asociada, archivo GeoJSON
     * con el trazado geográfico y estado de activación.</p>
     *
     * <pre>
     * GET /recorridos
     * Response: {
     *   "success": true,
     *   "message": "Recorridos obtenidos",
     *   "data": [
     *     {
     *       "idRecorrido": 1,
     *       "idRuta": 3,
     *       "nombreArchivoGeojson": "recorrido_centro_norte_ida.geojson",
     *       "activo": true
     *     },
     *     {
     *       "idRecorrido": 2,
     *       "idRuta": 3,
     *       "nombreArchivoGeojson": "recorrido_centro_norte_vuelta.geojson",
     *       "activo": true
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see RecorridoService#getAllRecorridos()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            ctx.json(ApiResponse.success("Recorridos obtenidos", recorridoService.getAllRecorridos()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Obtiene un recorrido específico por su identificador único.
     *
     * <p>Busca y retorna los datos de un recorrido específico basándose en el ID
     * proporcionado en la URL. Incluye información completa como la ruta asociada,
     * archivo GeoJSON con el trazado geográfico y estado de activación.</p>
     *
     * <pre>
     * GET /recorridos/1
     * Response exitosa: {
     *   "success": true,
     *   "message": "Recorrido encontrado",
     *   "data": {
     *     "idRecorrido": 1,
     *     "idRuta": 3,
     *     "nombreArchivoGeojson": "recorrido_centro_norte_ida.geojson",
     *     "activo": true
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Recorrido no encontrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see RecorridoService#getRecorridoById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var recorrido = recorridoService.getRecorridoById(id);
            ctx.json(recorrido != null ? ApiResponse.success("Recorrido encontrado", recorrido) : ApiResponse.notFound("Recorrido"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Crea un nuevo recorrido en el sistema.
     *
     * <p>Procesa una petición POST para crear un nuevo recorrido con los datos
     * proporcionados en el cuerpo de la petición JSON. Valida que la ruta asociada
     * exista y que el archivo GeoJSON tenga un nombre válido. El recorrido puede
     * representar diferentes direcciones de una misma ruta (ida/vuelta).</p>
     *
     * <pre>
     * POST /recorridos
     * Body: {
     *   "idRuta": 3,
     *   "nombreArchivoGeojson": "nuevo_recorrido_sur_este.geojson",
     *   "activo": true
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Recorrido creado",
     *   "data": {
     *     "idRecorrido": 8,
     *     "idRuta": 3,
     *     "nombreArchivoGeojson": "nuevo_recorrido_sur_este.geojson",
     *     "activo": true
     *   }
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos del recorrido en el cuerpo JSON
     * @throws Exception si los datos son inválidos o ocurre error en la base de datos
     *
     * @see RecorridoService#createRecorrido(com.wheely.model.Recorrido)
     * @see ApiResponse#success(String, Object)
     */
    public void create(Context ctx) {
        try {
            var recorrido = ctx.bodyAsClass(com.wheely.model.Recorrido.class);
            int id = recorridoService.createRecorrido(recorrido);
            recorrido.setIdRecorrido(id);
            ctx.status(HttpStatus.CREATED).json(ApiResponse.success("Recorrido creado", recorrido));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Actualiza un recorrido existente con nuevos datos.
     *
     * <p>Modifica los datos de un recorrido específico identificado por su ID.
     * Permite actualizar la ruta asociada, nombre del archivo GeoJSON y estado
     * de activación. Útil para optimización de rutas, cambios en el trazado
     * o actualización de archivos geoespaciales.</p>
     *
     * <pre>
     * PUT /recorridos/8
     * Body: {
     *   "idRuta": 5,
     *   "nombreArchivoGeojson": "recorrido_sur_este_optimizado.geojson",
     *   "activo": true
     * }
     *
     * Response exitosa: {
     *   "success": true,
     *   "message": "Recorrido actualizado",
     *   "data": {
     *     "idRecorrido": 8,
     *     "idRuta": 5,
     *     "nombreArchivoGeojson": "recorrido_sur_este_optimizado.geojson",
     *     "activo": true
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Recorrido no encontrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID en la URL y datos actualizados en el cuerpo
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la actualización
     *
     * @see RecorridoService#updateRecorrido(com.wheely.model.Recorrido)
     * @see ApiResponse#success(String, Object)
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var recorrido = ctx.bodyAsClass(com.wheely.model.Recorrido.class);
            recorrido.setIdRecorrido(id);
            ctx.json(recorridoService.updateRecorrido(recorrido) ?
                    ApiResponse.success("Recorrido actualizado", recorrido) : ApiResponse.notFound("Recorrido"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Elimina un recorrido del sistema por su ID.
     *
     * <p>Remueve permanentemente un recorrido del sistema. Esta operación debe
     * realizarse con cuidado ya que puede afectar las coordenadas y paradas
     * asociadas al recorrido. Se recomienda verificar dependencias antes
     * de eliminar para mantener la integridad del sistema geoespacial.</p>
     *
     * <pre>
     * DELETE /recorridos/8
     * Response exitosa: {
     *   "success": true,
     *   "message": "Recorrido eliminado"
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Recorrido no encontrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID del recorrido a eliminar
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la eliminación o existen dependencias
     *
     * @see RecorridoService#deleteRecorrido(int)
     * @see ApiResponse#success(String)
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(recorridoService.deleteRecorrido(id) ?
                    ApiResponse.success("Recorrido eliminado") : ApiResponse.notFound("Recorrido"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }
}