package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.service.CoordenadaParadaService;
import com.wheely.util.ApiResponse;

/**
 * Controlador REST para la gestión de coordenadas de paradas del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de coordenadas
 * geográficas específicas de las paradas de autobús. Las coordenadas de parada definen
 * la ubicación exacta donde los usuarios pueden abordar o descender de los vehículos,
 * proporcionando información precisa para sistemas de navegación y aplicaciones móviles
 * de transporte público.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de coordenadas de paradas (crear, leer, actualizar, eliminar)</li>
 * <li>Manejo de ubicaciones geográficas precisas para paradas de autobús</li>
 * <li>Ordenamiento secuencial de múltiples coordenadas por parada</li>
 * <li>Asociación de coordenadas con paradas específicas</li>
 * <li>Soporte para aplicaciones de geolocalización y navegación</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see CoordenadaParadaService
 * @see com.wheely.model.CoordenadaParada
 * @see com.wheely.util.ApiResponse
 */
public class CoordenadaParadaController {
    private final CoordenadaParadaService coordenadaParadaService;

    /**
     * Constructor del controlador de coordenadas de parada.
     *
     * @param coordenadaParadaService Servicio que contiene la lógica de negocio para coordenadas de parada
     */
    public CoordenadaParadaController(CoordenadaParadaService coordenadaParadaService) {
        this.coordenadaParadaService = coordenadaParadaService;
    }

    /**
     * Obtiene todas las coordenadas de paradas registradas en el sistema.
     *
     * <p>Este método maneja peticiones GET a /coordenadas-parada y retorna una lista completa
     * de todas las coordenadas geográficas de paradas, ordenadas por parada y orden de coordenada.
     * Cada coordenada incluye información precisa de latitud, longitud y su posición
     * secuencial dentro de la parada correspondiente.</p>
     *
     * <pre>
     * GET /coordenadas-parada
     * Response: {
     *   "success": true,
     *   "message": "Coordenadas parada obtenidas",
     *   "data": [
     *     {
     *       "idCoordenadaParada": 1,
     *       "idParada": 3,
     *       "latitud": 16.7545000,
     *       "longitud": -93.1315000,
     *       "ordenParada": 1
     *     },
     *     {
     *       "idCoordenadaParada": 2,
     *       "idParada": 3,
     *       "latitud": 16.7547000,
     *       "longitud": -93.1318000,
     *       "ordenParada": 2
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see CoordenadaParadaService#getAllCoordenadaParadas()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            ctx.json(ApiResponse.success("Coordenadas parada obtenidas", coordenadaParadaService.getAllCoordenadaParadas()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Obtiene una coordenada de parada específica por su identificador único.
     *
     * <p>Busca y retorna los datos de una coordenada de parada específica basándose en el ID
     * proporcionado en la URL. Incluye información completa como la parada asociada,
     * coordenadas geográficas precisas y orden secuencial en la ubicación de la parada.</p>
     *
     * <pre>
     * GET /coordenadas-parada/1
     * Response exitosa: {
     *   "success": true,
     *   "message": "Coordenada parada encontrada",
     *   "data": {
     *     "idCoordenadaParada": 1,
     *     "idParada": 3,
     *     "latitud": 16.7545000,
     *     "longitud": -93.1315000,
     *     "ordenParada": 1
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Coordenada parada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see CoordenadaParadaService#getCoordenadaParadaById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var coordenadaParada = coordenadaParadaService.getCoordenadaParadaById(id);
            ctx.json(coordenadaParada != null ? ApiResponse.success("Coordenada parada encontrada", coordenadaParada) : ApiResponse.notFound("Coordenada parada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Crea una nueva coordenada de parada en el sistema.
     *
     * <p>Procesa una petición POST para crear una nueva coordenada geográfica de parada con los datos
     * proporcionados en el cuerpo de la petición JSON. Valida que la parada asociada
     * exista, que las coordenadas estén en formato decimal válido y que el orden de parada
     * sea secuencial. Útil para definir múltiples puntos de acceso en paradas grandes.</p>
     *
     * <pre>
     * POST /coordenadas-parada
     * Body: {
     *   "idParada": 3,
     *   "latitud": 16.7550000,
     *   "longitud": -93.1320000,
     *   "ordenParada": 3
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Coordenada parada creada",
     *   "data": {
     *     "idCoordenadaParada": 25,
     *     "idParada": 3,
     *     "latitud": 16.7550000,
     *     "longitud": -93.1320000,
     *     "ordenParada": 3
     *   }
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos de la coordenada de parada en el cuerpo JSON
     * @throws Exception si los datos son inválidos o ocurre error en la base de datos
     *
     * @see CoordenadaParadaService#createCoordenadaParada(com.wheely.model.CoordenadaParada)
     * @see ApiResponse#success(String, Object)
     */
    public void create(Context ctx) {
        try {
            var coordenadaParada = ctx.bodyAsClass(com.wheely.model.CoordenadaParada.class);
            int id = coordenadaParadaService.createCoordenadaParada(coordenadaParada);
            coordenadaParada.setIdCoordenadaParada(id);
            ctx.status(HttpStatus.CREATED).json(ApiResponse.success("Coordenada parada creada", coordenadaParada));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Actualiza una coordenada de parada existente con nuevos datos.
     *
     * <p>Modifica los datos de una coordenada de parada específica identificada por su ID.
     * Permite actualizar las coordenadas geográficas, parada asociada y orden
     * de parada. Útil para correcciones de ubicación, reubicación de paradas o
     * actualización de datos GPS más precisos para mejorar la experiencia del usuario.</p>
     *
     * <pre>
     * PUT /coordenadas-parada/25
     * Body: {
     *   "idParada": 3,
     *   "latitud": 16.7552000,
     *   "longitud": -93.1322000,
     *   "ordenParada": 3
     * }
     *
     * Response exitosa: {
     *   "success": true,
     *   "message": "Coordenada parada actualizada",
     *   "data": {
     *     "idCoordenadaParada": 25,
     *     "idParada": 3,
     *     "latitud": 16.7552000,
     *     "longitud": -93.1322000,
     *     "ordenParada": 3
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Coordenada parada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID en la URL y datos actualizados en el cuerpo
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la actualización
     *
     * @see CoordenadaParadaService#updateCoordenadaParada(com.wheely.model.CoordenadaParada)
     * @see ApiResponse#success(String, Object)
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var coordenadaParada = ctx.bodyAsClass(com.wheely.model.CoordenadaParada.class);
            coordenadaParada.setIdCoordenadaParada(id);
            ctx.json(coordenadaParadaService.updateCoordenadaParada(coordenadaParada) ?
                    ApiResponse.success("Coordenada parada actualizada", coordenadaParada) : ApiResponse.notFound("Coordenada parada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Elimina una coordenada de parada del sistema por su ID.
     *
     * <p>Remueve permanentemente una coordenada de parada del sistema. Esta operación debe
     * realizarse con cuidado ya que puede afectar la accesibilidad a la parada desde
     * diferentes puntos. Se recomienda verificar que existan otras coordenadas
     * alternativas antes de eliminar para mantener la funcionalidad de la parada.</p>
     *
     * <pre>
     * DELETE /coordenadas-parada/25
     * Response exitosa: {
     *   "success": true,
     *   "message": "Coordenada parada eliminada"
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Coordenada parada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID de la coordenada de parada a eliminar
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la eliminación
     *
     * @see CoordenadaParadaService#deleteCoordenadaParada(int)
     * @see ApiResponse#success(String)
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(coordenadaParadaService.deleteCoordenadaParada(id) ?
                    ApiResponse.success("Coordenada parada eliminada") : ApiResponse.notFound("Coordenada parada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }
}