package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.model.Reporte;
import com.wheely.service.ReporteService;
import com.wheely.util.ApiResponse;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador REST para la gestión de reportes del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de reportes
 * de incidencias y estado de las rutas de transporte público. Los reportes permiten a
 * los usuarios informar problemas, sugerencias o estado de las rutas, facilitando el
 * monitoreo y mejora del servicio de transporte.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de reportes (crear, leer, actualizar, eliminar)</li>
 * <li>Consulta de reportes por usuario específico</li>
 * <li>Consulta de reportes por ruta específica</li>
 * <li>Validación de permisos para eliminar reportes</li>
 * <li>Manejo de respuestas HTTP estandarizadas</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see ReporteService
 * @see com.wheely.model.Reporte
 * @see com.wheely.util.ApiResponse
 */
public class ReporteController {
    private final ReporteService reporteService;

    /**
     * Constructor del controlador de reportes.
     *
     * @param reporteService Servicio que contiene la lógica de negocio para reportes
     */
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Obtiene todos los reportes registrados en el sistema.
     *
     * <p>Este método maneja peticiones GET a /reportes y retorna una lista completa
     * de todos los reportes ordenados por fecha de creación (más recientes primero).
     * Útil para administradores que necesitan ver el estado general del sistema.</p>
     *
     * <pre>
     * GET /reportes
     * Response: {
     *   "success": true,
     *   "message": "Reportes obtenidos",
     *   "data": [...]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws SQLException si ocurre error en la consulta a la base de datos
     *
     * @see ReporteService#getAllReportes()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            List<Reporte> reportes = reporteService.getAllReportes();
            ctx.json(ApiResponse.success("Reportes obtenidos", reportes));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener reportes"));
        }
    }

    /**
     * Obtiene un reporte específico por su identificador único.
     *
     * <p>Busca y retorna los datos de un reporte específico basándose en el ID
     * proporcionado en la URL. Incluye información completa del reporte como
     * título, descripción, fecha y referencias a usuario, ruta y tipo.</p>
     *
     * <pre>
     * GET /reportes/123
     * Response exitosa: {
     *   "success": true,
     *   "message": "Reporte encontrado",
     *   "data": {
     *     "idReporte": 123,
     *     "titulo": "Retraso en ruta",
     *     "descripcion": "La ruta presenta retrasos...",
     *     "fechaReporte": "2025-01-15T10:30:00"
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Reporte no encontrado"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws SQLException si ocurre error en la consulta a la base de datos
     *
     * @see ReporteService#getReporteById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Reporte reporte = reporteService.getReporteById(id);
            if (reporte != null) {
                ctx.json(ApiResponse.success("Reporte encontrado", reporte));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Reporte"));
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error("ID no válido"));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener reporte"));
        }
    }

    /**
     * Crea un nuevo reporte en el sistema.
     *
     * <p>Procesa una petición POST para crear un nuevo reporte con los datos
     * proporcionados en el cuerpo de la petición JSON. Valida que existan
     * el usuario, ruta y tipo de reporte antes de crear el registro.
     * La fecha se asigna automáticamente al momento actual.</p>
     *
     * <pre>
     * POST /reportes
     * Body: {
     *   "idUsuario": 5,
     *   "idRuta": 3,
     *   "idTipoReporte": 1,
     *   "titulo": "Autobús con falla mecánica",
     *   "descripcion": "El autobús de la ruta Centro-Norte presenta problemas..."
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Reporte creado",
     *   "data": {...}
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene los datos del reporte en el cuerpo JSON
     * @throws IllegalArgumentException si los datos del reporte son inválidos
     * @throws SQLException si ocurre error en la base de datos
     *
     * @see ReporteService#createReporte(Reporte)
     * @see ApiResponse#success(String, Object)
     */
    public void create(Context ctx) {
        try {
            Reporte reporte = ctx.bodyAsClass(Reporte.class);
            int id = reporteService.createReporte(reporte);
            reporte.setIdReporte(id);
            ctx.status(HttpStatus.CREATED)
                    .json(ApiResponse.success("Reporte creado", reporte));
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al crear reporte"));
        }
    }

    /**
     * Actualiza un reporte existente con nuevos datos.
     *
     * <p>Modifica los datos de un reporte específico identificado por su ID.
     * Permite actualizar título, descripción y tipo de reporte. La fecha
     * de creación original se mantiene sin cambios para preservar el historial.</p>
     *
     * <pre>
     * PUT /reportes/123
     * Body: {
     *   "titulo": "Título actualizado",
     *   "descripcion": "Descripción actualizada con más detalles",
     *   "idTipoReporte": 2
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID en la URL y datos actualizados en el cuerpo
     * @throws NumberFormatException si el ID no es un número válido
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws SQLException si ocurre error en la actualización
     *
     * @see ReporteService#updateReporte(Reporte)
     * @see ApiResponse#success(String, Object)
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Reporte reporte = ctx.bodyAsClass(Reporte.class);
            reporte.setIdReporte(id);

            boolean updated = reporteService.updateReporte(reporte);
            if (updated) {
                ctx.json(ApiResponse.success("Reporte actualizado", reporte));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Reporte"));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al actualizar reporte"));
        }
    }

    /**
     * Elimina un reporte del sistema con validación de permisos.
     *
     * <p>Remueve permanentemente un reporte del sistema verificando que el usuario
     * que hace la petición sea el propietario del reporte. Esta validación de
     * seguridad previene que usuarios eliminen reportes ajenos.</p>
     *
     * <pre>
     * DELETE /reportes/123?usuarioId=5
     * Response exitosa: {
     *   "success": true,
     *   "message": "Reporte eliminado"
     * }
     *
     * Response sin permisos: {
     *   "success": false,
     *   "message": "No tienes permisos para eliminar este reporte"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el ID del reporte en la URL y usuarioId como query param
     * @throws NumberFormatException si el ID o usuarioId no son números válidos
     * @throws IllegalArgumentException si falta el parámetro usuarioId
     * @throws SQLException si ocurre error en la eliminación
     *
     * @see ReporteService#deleteReporte(int, int)
     * @see ApiResponse#success(String)
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            String usuarioIdParam = ctx.queryParam("usuarioId");

            if (usuarioIdParam == null) {
                ctx.status(HttpStatus.BAD_REQUEST)
                        .json(ApiResponse.error("usuarioId requerido"));
                return;
            }

            int usuarioId = Integer.parseInt(usuarioIdParam);
            boolean deleted = reporteService.deleteReporte(id, usuarioId);

            if (deleted) {
                ctx.json(ApiResponse.success("Reporte eliminado"));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Reporte"));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al eliminar reporte"));
        }
    }

    /**
     * Obtiene todos los reportes creados por un usuario específico.
     *
     * <p>Filtra y retorna únicamente los reportes que han sido creados por
     * el usuario especificado. Útil para que los usuarios vean su historial
     * de reportes y para estadísticas de participación ciudadana.</p>
     *
     * <pre>
     * GET /reportes/usuario/5
     * Response: {
     *   "success": true,
     *   "message": "Reportes del usuario",
     *   "data": [
     *     {
     *       "idReporte": 123,
     *       "titulo": "Reporte del usuario 5",
     *       "fechaReporte": "2025-01-15T10:30:00"
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {usuarioId} en la URL
     * @throws NumberFormatException si el usuarioId no es un número válido
     * @throws SQLException si ocurre error en la consulta
     *
     * @see ReporteService#getReportesByUsuario(int)
     * @see ApiResponse#success(String, Object)
     */
    public void getByUsuario(Context ctx) {
        try {
            int usuarioId = Integer.parseInt(ctx.pathParam("usuarioId"));
            List<Reporte> reportes = reporteService.getReportesByUsuario(usuarioId);
            ctx.json(ApiResponse.success("Reportes del usuario", reportes));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error("usuarioId no válido"));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener reportes"));
        }
    }

    /**
     * Obtiene todos los reportes relacionados con una ruta específica.
     *
     * <p>Filtra y retorna únicamente los reportes que están asociados con
     * la ruta especificada. Permite a administradores y usuarios ver el
     * historial de incidencias de una ruta particular para identificar
     * patrones y tomar decisiones operativas.</p>
     *
     * <pre>
     * GET /reportes/ruta/3
     * Response: {
     *   "success": true,
     *   "message": "Reportes de la ruta",
     *   "data": [
     *     {
     *       "idReporte": 124,
     *       "titulo": "Problema en ruta Centro-Norte",
     *       "descripcion": "Retraso constante en esta ruta...",
     *       "fechaReporte": "2025-01-15T14:20:00"
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {rutaId} en la URL
     * @throws NumberFormatException si el rutaId no es un número válido
     * @throws SQLException si ocurre error en la consulta
     *
     * @see ReporteService#getReportesByRuta(int)
     * @see ApiResponse#success(String, Object)
     */
    public void getByRuta(Context ctx) {
        try {
            int rutaId = Integer.parseInt(ctx.pathParam("rutaId"));
            List<Reporte> reportes = reporteService.getReportesByRuta(rutaId);
            ctx.json(ApiResponse.success("Reportes de la ruta", reportes));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error("rutaId no válido"));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener reportes"));
        }
    }
}