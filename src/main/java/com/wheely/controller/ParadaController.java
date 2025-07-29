package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.service.ParadaService;
import com.wheely.util.ApiResponse;

/**
 * Controlador REST para la gestión de paradas del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de paradas
 * de autobús en el sistema de transporte público. Las paradas representan puntos físicos
 * donde los usuarios pueden abordar o descender de los vehículos, y están asociadas a
 * recorridos específicos con datos geoespaciales para su ubicación en mapas.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de paradas (crear, leer, actualizar, eliminar)</li>
 * <li>Administración de archivos GeoJSON para representación geográfica</li>
 * <li>Control de estado activo/inactivo de paradas</li>
 * <li>Asociación de paradas con recorridos específicos</li>
 * <li>Manejo de respuestas HTTP estandarizadas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see ParadaService
 * @see com.wheely.model.Parada
 * @see com.wheely.util.ApiResponse
 */
public class ParadaController {
    private final ParadaService paradaService;

    /**
     * Constructor del controlador de paradas.
     *
     * @param paradaService Servicio que contiene la lógica de negocio para paradas
     */
    public ParadaController(ParadaService paradaService) {
        this.paradaService = paradaService;
    }

    /**
     * Obtiene todas las paradas registradas en el sistema.
     *
     * <p>Este método maneja peticiones GET a /paradas y retorna una lista completa
     * de todas las paradas de autobús disponibles, ordenadas por recorrido y nombre
     * de archivo. Incluye información sobre el recorrido asociado, archivo GeoJSON
     * y estado activo de cada parada.</p>
     *
     * <pre>
     * GET /paradas
     * Response: {
     *   "success": true,
     *   "message": "Paradas obtenidas",
     *   "data": [
     *     {
     *       "idParada": 1,
     *       "idRecorrido": 5,
     *       "nombreArchivoGeojson": "parada_centro_plaza.geojson",
     *       "activo": true
     *     },
     *     {
     *       "idParada": 2,
     *       "idRecorrido": 5,
     *       "nombreArchivoGeojson": "parada_mercado_central.geojson",
     *       "activo": true
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see ParadaService#getAllParadas()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            ctx.json(ApiResponse.success("Paradas obtenidas", paradaService.getAllParadas()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Obtiene una parada específica por su identificador único.
     *
     * <p>Busca y retorna los datos de una parada específica basándose en el ID
     * proporcionado en la URL. Incluye información completa como el recorrido
     * asociado, archivo GeoJSON y estado de activación.</p>
     *
     * <pre>
     * GET /paradas/1
     * Response exitosa: {
     *   "success": true,
     *   "message": "Parada encontrada",
     *   "data": {
     *     "idParada": 1,
     *     "idRecorrido": 5,
     *     "nombreArchivoGeojson": "parada_centro_plaza.geojson",
     *     "activo": true
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Parada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see ParadaService#getParadaById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var parada = paradaService.getParadaById(id);
            ctx.json(parada != null ? ApiResponse.success("Parada encontrada", parada) : ApiResponse.notFound("Parada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Crea una nueva parada en el sistema.
     *
     * <p>Procesa una petición POST para crear una nueva parada de autobús con los datos
     * proporcionados en el cuerpo de la petición JSON. Valida que el recorrido asociado
     * exista y que el archivo GeoJSON tenga un nombre válido. La parada se crea en estado
     * activo por defecto.</p>
     *
     * <pre>
     * POST /paradas
     * Body: {
     *   "idRecorrido": 5,
     *   "nombreArchivoGeojson": "nueva_parada_hospital.geojson",
     *   "activo": true
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Parada creada",
     *   "data": {
     *     "idParada": 15,
     *     "idRecorrido": 5,
     *     "nombreArchivoGeojson": "nueva_parada_hospital.geojson",
     *     "activo": true
     *   }
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos de la parada en el cuerpo JSON
     * @throws Exception si los datos son inválidos o ocurre error en la base de datos
     *
     * @see ParadaService#createParada(com.wheely.model.Parada)
     * @see ApiResponse#success(String, Object)
     */
    public void create(Context ctx) {
        try {
            var parada = ctx.bodyAsClass(com.wheely.model.Parada.class);
            var paradaCreada = paradaService.createParada(parada);
            ctx.status(HttpStatus.CREATED).json(ApiResponse.success("Parada creada", paradaCreada));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Actualiza una parada existente con nuevos datos.
     *
     * <p>Modifica los datos de una parada específica identificada por su ID.
     * Permite actualizar el recorrido asociado, nombre del archivo GeoJSON y
     * estado de activación. Útil para reubicación de paradas o cambios en
     * la configuración de rutas.</p>
     *
     * <pre>
     * PUT /paradas/15
     * Body: {
     *   "idRecorrido": 7,
     *   "nombreArchivoGeojson": "parada_hospital_actualizada.geojson",
     *   "activo": false
     * }
     *
     * Response exitosa: {
     *   "success": true,
     *   "message": "Parada actualizada",
     *   "data": {
     *     "idParada": 15,
     *     "idRecorrido": 7,
     *     "nombreArchivoGeojson": "parada_hospital_actualizada.geojson",
     *     "activo": false
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Parada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID en la URL y datos actualizados en el cuerpo
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la actualización
     *
     * @see ParadaService#updateParada(com.wheely.model.Parada)
     * @see ApiResponse#success(String, Object)
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var parada = ctx.bodyAsClass(com.wheely.model.Parada.class);
            parada.setIdParada(id);
            ctx.json(paradaService.updateParada(parada) ?
                    ApiResponse.success("Parada actualizada", parada) : ApiResponse.notFound("Parada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Elimina una parada del sistema por su ID.
     *
     * <p>Remueve permanentemente una parada del sistema. Esta operación debe
     * realizarse con cuidado ya que puede afectar las coordenadas asociadas y
     * la representación geográfica de los recorridos. Se recomienda verificar
     * que no existan coordenadas activas antes de eliminar.</p>
     *
     * <pre>
     * DELETE /paradas/15
     * Response exitosa: {
     *   "success": true,
     *   "message": "Parada eliminada"
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Parada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID de la parada a eliminar
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la eliminación o existen dependencias
     *
     * @see ParadaService#deleteParada(int)
     * @see ApiResponse#success(String)
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(paradaService.deleteParada(id) ?
                    ApiResponse.success("Parada eliminada") : ApiResponse.notFound("Parada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }
}