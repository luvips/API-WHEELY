package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.service.TipoReporteService;
import com.wheely.util.ApiResponse;

/**
 * Controlador REST para la gestión de tipos de reporte del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de
 * tipos de reporte que clasifican las diferentes categorías de reportes que pueden
 * crear los usuarios. Los tipos de reporte definen categorías como "Incidencia",
 * "Sugerencia", "Queja", "Elogio", etc., proporcionando estructura y organización
 * al sistema de reportes ciudadanos.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de tipos de reporte (crear, leer, actualizar, eliminar)</li>
 * <li>Listado de todos los tipos disponibles para selección en formularios</li>
 * <li>Consulta individual de tipos específicos</li>
 * <li>Validación de datos de entrada</li>
 * <li>Manejo de respuestas HTTP estandarizadas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see TipoReporteService
 * @see com.wheely.model.TipoReporte
 * @see com.wheely.util.ApiResponse
 */
public class TipoReporteController {
    private final TipoReporteService tipoReporteService;

    /**
     * Constructor del controlador de tipos de reporte.
     *
     * @param tipoReporteService Servicio que contiene la lógica de negocio para tipos de reporte
     */
    public TipoReporteController(TipoReporteService tipoReporteService) {
        this.tipoReporteService = tipoReporteService;
    }

    /**
     * Obtiene todos los tipos de reporte disponibles en el sistema.
     *
     * <p>Este método maneja peticiones GET a /tipos-reporte y retorna una lista completa
     * de todos los tipos de reporte configurados. Esta información es utilizada principalmente
     * por el frontend para poblar selectores y formularios donde los usuarios eligen
     * la categoría de su reporte.</p>
     *
     * <pre>
     * GET /tipos-reporte
     * Response: {
     *   "success": true,
     *   "message": "Tipos obtenidos",
     *   "data": [
     *     {
     *       "idTipoReporte": 1,
     *       "nombreTipo": "Incidencia",
     *       "descripcion": "Problemas o fallas en el servicio"
     *     },
     *     {
     *       "idTipoReporte": 2,
     *       "nombreTipo": "Sugerencia",
     *       "descripcion": "Propuestas de mejora al servicio"
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see TipoReporteService#getAllTiposReporte()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            ctx.json(ApiResponse.success("Tipos obtenidos", tipoReporteService.getAllTiposReporte()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Obtiene un tipo de reporte específico por su identificador único.
     *
     * <p>Busca y retorna los datos de un tipo de reporte específico basándose en el ID
     * proporcionado en la URL. Incluye el nombre del tipo y su descripción detallada.</p>
     *
     * <pre>
     * GET /tipos-reporte/1
     * Response exitosa: {
     *   "success": true,
     *   "message": "Tipo encontrado",
     *   "data": {
     *     "idTipoReporte": 1,
     *     "nombreTipo": "Incidencia",
     *     "descripcion": "Reportes sobre problemas o fallas en el servicio de transporte"
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Tipo no encontrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see TipoReporteService#getTipoReporteById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var tipo = tipoReporteService.getTipoReporteById(id);
            ctx.json(tipo != null ? ApiResponse.success("Tipo encontrado", tipo) : ApiResponse.notFound("Tipo"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Crea un nuevo tipo de reporte en el sistema.
     *
     * <p>Procesa una petición POST para crear un nuevo tipo de reporte con los datos
     * proporcionados en el cuerpo de la petición JSON. Valida que el nombre del tipo
     * sea único y que contenga una descripción clara para los usuarios.</p>
     *
     * <pre>
     * POST /tipos-reporte
     * Body: {
     *   "nombreTipo": "Elogio",
     *   "descripcion": "Comentarios positivos sobre el servicio de transporte"
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Tipo creado",
     *   "data": {
     *     "idTipoReporte": 5,
     *     "nombreTipo": "Elogio",
     *     "descripcion": "Comentarios positivos sobre el servicio de transporte"
     *   }
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos del tipo de reporte en el cuerpo JSON
     * @throws Exception si los datos son inválidos o ocurre error en la base de datos
     *
     * @see TipoReporteService#createTipoReporte(com.wheely.model.TipoReporte)
     * @see ApiResponse#success(String, Object)
     */
    public void create(Context ctx) {
        try {
            var tipo = ctx.bodyAsClass(com.wheely.model.TipoReporte.class);
            int id = tipoReporteService.createTipoReporte(tipo);
            tipo.setIdTipoReporte(id);
            ctx.status(HttpStatus.CREATED).json(ApiResponse.success("Tipo creado", tipo));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Actualiza un tipo de reporte existente con nuevos datos.
     *
     * <p>Modifica los datos de un tipo de reporte específico identificado por su ID.
     * Permite actualizar el nombre del tipo y su descripción. Útil para refinar
     * las categorías de reportes basándose en el uso y feedback de los usuarios.</p>
     *
     * <pre>
     * PUT /tipos-reporte/3
     * Body: {
     *   "nombreTipo": "Queja Actualizada",
     *   "descripcion": "Descripción más detallada sobre quejas del servicio"
     * }
     *
     * Response exitosa: {
     *   "success": true,
     *   "message": "Tipo actualizado",
     *   "data": {...}
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Tipo no encontrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID en la URL y datos actualizados en el cuerpo
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la actualización
     *
     * @see TipoReporteService#updateTipoReporte(com.wheely.model.TipoReporte)
     * @see ApiResponse#success(String, Object)
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var tipo = ctx.bodyAsClass(com.wheely.model.TipoReporte.class);
            tipo.setIdTipoReporte(id);
            ctx.json(tipoReporteService.updateTipoReporte(tipo) ?
                    ApiResponse.success("Tipo actualizado", tipo) : ApiResponse.notFound("Tipo"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Elimina un tipo de reporte del sistema por su ID.
     *
     * <p>Remueve permanentemente un tipo de reporte del sistema. Esta operación debe
     * realizarse con cuidado ya que puede afectar reportes existentes que usen este tipo.
     * Se recomienda verificar que no existan reportes asociados antes de eliminar.</p>
     *
     * <pre>
     * DELETE /tipos-reporte/4
     * Response exitosa: {
     *   "success": true,
     *   "message": "Tipo eliminado"
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Tipo no encontrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID del tipo de reporte a eliminar
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la eliminación o existen reportes asociados
     *
     * @see TipoReporteService#deleteTipoReporte(int)
     * @see ApiResponse#success(String)
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(tipoReporteService.deleteTipoReporte(id) ?
                    ApiResponse.success("Tipo eliminado") : ApiResponse.notFound("Tipo"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }
}