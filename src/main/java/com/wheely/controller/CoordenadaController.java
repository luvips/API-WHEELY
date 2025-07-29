package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.service.CoordenadaService;
import com.wheely.util.ApiResponse;

/**
 * Controlador REST para la gestión de coordenadas del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de coordenadas
 * geográficas que componen los recorridos de las rutas de transporte público. Las coordenadas
 * son puntos específicos de latitud y longitud que definen el trazado exacto que siguen
 * los vehículos, permitiendo la visualización precisa de rutas en mapas digitales.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de coordenadas (crear, leer, actualizar, eliminar)</li>
 * <li>Manejo de datos geográficos con precisión decimal</li>
 * <li>Ordenamiento secuencial de puntos en recorridos</li>
 * <li>Asociación de coordenadas con recorridos específicos</li>
 * <li>Soporte para sistemas de información geográfica (SIG)</li>
 * </ul>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see CoordenadaService
 * @see com.wheely.model.Coordenada
 * @see com.wheely.util.ApiResponse
 */
public class CoordenadaController {
    private final CoordenadaService coordenadaService;

    /**
     * Constructor del controlador de coordenadas.
     *
     * @param coordenadaService Servicio que contiene la lógica de negocio para coordenadas
     */
    public CoordenadaController(CoordenadaService coordenadaService) {
        this.coordenadaService = coordenadaService;
    }

    /**
     * Obtiene todas las coordenadas registradas en el sistema.
     *
     * <p>Este método maneja peticiones GET a /coordenadas y retorna una lista completa
     * de todas las coordenadas geográficas, ordenadas por recorrido y orden de punto.
     * Cada coordenada incluye información precisa de latitud, longitud y su posición
     * secuencial dentro del recorrido correspondiente.</p>
     *
     * <pre>
     * GET /coordenadas
     * Response: {
     *   "success": true,
     *   "message": "Coordenadas obtenidas",
     *   "data": [
     *     {
     *       "idCoordenada": 1,
     *       "idRecorrido": 5,
     *       "latitud": 16.7569444,
     *       "longitud": -93.1292778,
     *       "ordenPunto": 1
     *     },
     *     {
     *       "idCoordenada": 2,
     *       "idRecorrido": 5,
     *       "latitud": 16.7575000,
     *       "longitud": -93.1285000,
     *       "ordenPunto": 2
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see CoordenadaService#getAllCoordenadas()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            ctx.json(ApiResponse.success("Coordenadas obtenidas", coordenadaService.getAllCoordenadas()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Obtiene una coordenada específica por su identificador único.
     *
     * <p>Busca y retorna los datos de una coordenada específica basándose en el ID
     * proporcionado en la URL. Incluye información completa como el recorrido asociado,
     * coordenadas geográficas precisas y orden secuencial en el trazado.</p>
     *
     * <pre>
     * GET /coordenadas/1
     * Response exitosa: {
     *   "success": true,
     *   "message": "Coordenada encontrada",
     *   "data": {
     *     "idCoordenada": 1,
     *     "idRecorrido": 5,
     *     "latitud": 16.7569444,
     *     "longitud": -93.1292778,
     *     "ordenPunto": 1
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Coordenada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see CoordenadaService#getCoordenadaById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var coordenada = coordenadaService.getCoordenadaById(id);
            ctx.json(coordenada != null ? ApiResponse.success("Coordenada encontrada", coordenada) : ApiResponse.notFound("Coordenada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Crea una nueva coordenada en el sistema.
     *
     * <p>Procesa una petición POST para crear una nueva coordenada geográfica con los datos
     * proporcionados en el cuerpo de la petición JSON. Valida que el recorrido asociado
     * exista, que las coordenadas estén en formato decimal válido y que el orden de punto
     * sea secuencial dentro del recorrido.</p>
     *
     * <pre>
     * POST /coordenadas
     * Body: {
     *   "idRecorrido": 5,
     *   "latitud": 16.7580000,
     *   "longitud": -93.1275000,
     *   "ordenPunto": 15
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Coordenada creada",
     *   "data": {
     *     "idCoordenada": 45,
     *     "idRecorrido": 5,
     *     "latitud": 16.7580000,
     *     "longitud": -93.1275000,
     *     "ordenPunto": 15
     *   }
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos de la coordenada en el cuerpo JSON
     * @throws Exception si los datos son inválidos o ocurre error en la base de datos
     *
     * @see CoordenadaService#createCoordenada(com.wheely.model.Coordenada)
     * @see ApiResponse#success(String, Object)
     */
    public void create(Context ctx) {
        try {
            var coordenada = ctx.bodyAsClass(com.wheely.model.Coordenada.class);
            int id = coordenadaService.createCoordenada(coordenada);
            coordenada.setIdCoordenada(id);
            ctx.status(HttpStatus.CREATED).json(ApiResponse.success("Coordenada creada", coordenada));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Actualiza una coordenada existente con nuevos datos.
     *
     * <p>Modifica los datos de una coordenada específica identificada por su ID.
     * Permite actualizar las coordenadas geográficas, recorrido asociado y orden
     * de punto. Útil para correcciones de trazado, optimización de rutas o
     * actualización de datos GPS más precisos.</p>
     *
     * <pre>
     * PUT /coordenadas/45
     * Body: {
     *   "idRecorrido": 5,
     *   "latitud": 16.7582000,
     *   "longitud": -93.1273000,
     *   "ordenPunto": 15
     * }
     *
     * Response exitosa: {
     *   "success": true,
     *   "message": "Coordenada actualizada",
     *   "data": {
     *     "idCoordenada": 45,
     *     "idRecorrido": 5,
     *     "latitud": 16.7582000,
     *     "longitud": -93.1273000,
     *     "ordenPunto": 15
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Coordenada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID en la URL y datos actualizados en el cuerpo
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la actualización
     *
     * @see CoordenadaService#updateCoordenada(com.wheely.model.Coordenada)
     * @see ApiResponse#success(String, Object)
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var coordenada = ctx.bodyAsClass(com.wheely.model.Coordenada.class);
            coordenada.setIdCoordenada(id);
            ctx.json(coordenadaService.updateCoordenada(coordenada) ?
                    ApiResponse.success("Coordenada actualizada", coordenada) : ApiResponse.notFound("Coordenada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Elimina una coordenada del sistema por su ID.
     *
     * <p>Remueve permanentemente una coordenada del sistema. Esta operación debe
     * realizarse con cuidado ya que puede afectar la continuidad del trazado
     * del recorrido. Se recomienda verificar el impacto en la secuencia de
     * puntos antes de eliminar para mantener la coherencia geográfica.</p>
     *
     * <pre>
     * DELETE /coordenadas/45
     * Response exitosa: {
     *   "success": true,
     *   "message": "Coordenada eliminada"
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Coordenada no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID de la coordenada a eliminar
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la eliminación
     *
     * @see CoordenadaService#deleteCoordenada(int)
     * @see ApiResponse#success(String)
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(coordenadaService.deleteCoordenada(id) ?
                    ApiResponse.success("Coordenada eliminada") : ApiResponse.notFound("Coordenada"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }
}