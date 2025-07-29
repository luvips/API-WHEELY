package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.model.Periodo;
import com.wheely.service.PeriodoService;
import com.wheely.util.ApiResponse;

import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * Controlador REST para la gestión de periodos en WHEELY, sistema de transporte público de Tuxtla Gutiérrez, Chiapas.
 * Permite consultar, crear, actualizar y eliminar bloques horarios, facilitando la organización de rutas y reportes por periodo.
 * </p>
 * <ul>
 *   <li>Gestiona entidades {@link Periodo} (ejemplo: Mañana, Tarde, Noche).</li>
 *   <li>Expone endpoints CRUD y consulta especializada.</li>
 *   <li>Integra con {@link PeriodoService} para lógica de negocio y persistencia.</li>
 *   <li>Responde usando {@link ApiResponse} para interoperabilidad con el frontend.</li>
 * </ul>
 * <p>
 * Fundamental para segmentar horarios, incidencias y estadísticas en el sistema.
 * </p>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see PeriodoService
 * @see Periodo
 * @see ApiResponse
 */
public class PeriodoController {
    private final PeriodoService periodoService;

    /**
     * <p>
     * Constructor de {@code PeriodoController}.
     * Inicializa el controlador con el servicio de periodos.
     * </p>
     *
     * @param periodoService {@link PeriodoService} Servicio para operaciones sobre periodos.
     * @see PeriodoService
     */
    public PeriodoController(PeriodoService periodoService) {
        this.periodoService = periodoService;
    }

    /**
     * <p>
     * Obtiene todos los periodos registrados en el sistema.
     * Utilizado para mostrar la segmentación de horarios y facilitar la gestión administrativa.
     * </p>
     * <ul>
     *   <li>Endpoint: <b>GET /api/periodos</b></li>
     *   <li>Retorna lista de {@link Periodo} en formato JSON.</li>
     * </ul>
     *
     * @param ctx {@link Context} Contexto HTTP de Javalin, contiene información de la petición y respuesta.
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     * @see Periodo
     * @see ApiResponse
     * <pre>
     * GET /api/periodos
     *
     * Respuesta exitosa:
     * {
     *   "success": true,
     *   "message": "Periodos obtenidos",
     *   "data": [
     *     {
     *       "idPeriodo": 1,
     *       "nombre": "Mañana",
     *       "horaInicio": "06:00",
     *       "horaFin": "12:00"
     *     }
     *   ]
     * }
     *
     * Respuesta error:
     * {
     *   "success": false,
     *   "message": "Error al obtener periodos"
     * }
     * </pre>
     */
    public void getAll(Context ctx) {
        try {
            List<Periodo> periodos = periodoService.getAllPeriodos();
            ctx.json(ApiResponse.success("Periodos obtenidos", periodos));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener periodos"));
        }
    }

    /**
     * <p>
     * Obtiene un periodo específico por su identificador único.
     * Permite consultar detalles de un bloque horario para edición o visualización.
     * </p>
     * <ul>
     *   <li>Endpoint: <b>GET /api/periodos/{id}</b></li>
     *   <li>Valida que el ID sea numérico y existente.</li>
     * </ul>
     *
     * @param ctx {@link Context} Contexto HTTP con parámetro de ruta "id" (int).
     * @throws NumberFormatException Si el parámetro "id" no es un número válido.
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     * @see Periodo
     * @see ApiResponse
     * <pre>
     * GET /api/periodos/1
     *
     * Respuesta exitosa:
     * {
     *   "success": true,
     *   "message": "Periodo encontrado",
     *   "data": {
     *     "idPeriodo": 1,
     *     "nombre": "Mañana",
     *     "horaInicio": "06:00",
     *     "horaFin": "12:00"
     *   }
     * }
     *
     * Respuesta no encontrada:
     * {
     *   "success": false,
     *   "message": "Periodo no encontrado"
     * }
     * </pre>
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Periodo periodo = periodoService.getPeriodoById(id);

            if (periodo != null) {
                ctx.json(ApiResponse.success("Periodo encontrado", periodo));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Periodo"));
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error("ID no válido"));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener periodo"));
        }
    }

    /**
     * <p>
     * Obtiene un periodo por su nombre, útil para búsquedas rápidas y validaciones en la gestión de horarios.
     * </p>
     * <ul>
     *   <li>Endpoint: <b>GET /api/periodos/nombre/{nombre}</b></li>
     *   <li>Valida que el nombre exista en la base de datos.</li>
     * </ul>
     *
     * @param ctx {@link Context} Contexto HTTP con parámetro de ruta "nombre" (String).
     * @throws IllegalArgumentException Si el nombre es inválido o no existe.
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     * @see Periodo
     * @see ApiResponse
     * <pre>
     * GET /api/periodos/nombre/Mañana
     *
     * Respuesta exitosa:
     * {
     *   "success": true,
     *   "message": "Periodo encontrado",
     *   "data": {
     *     "idPeriodo": 1,
     *     "nombre": "Mañana",
     *     "horaInicio": "06:00",
     *     "horaFin": "12:00"
     *   }
     * }
     *
     * Respuesta no encontrada:
     * {
     *   "success": false,
     *   "message": "Periodo no encontrado"
     * }
     * </pre>
     */
    public void getByNombre(Context ctx) {
        try {
            String nombre = ctx.pathParam("nombre");
            Periodo periodo = periodoService.getPeriodoByNombre(nombre);

            if (periodo != null) {
                ctx.json(ApiResponse.success("Periodo encontrado", periodo));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Periodo"));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener periodo"));
        }
    }

    /**
     * <p>
     * Obtiene el periodo actual según la hora del sistema.
     * Esencial para determinar el bloque horario vigente y segmentar reportes/incidencias en tiempo real.
     * </p>
     * <ul>
     *   <li>Endpoint: <b>GET /api/periodos/actual</b></li>
     *   <li>Retorna el {@link Periodo} correspondiente a la hora actual.</li>
     * </ul>
     *
     * @param ctx {@link Context} Contexto HTTP de Javalin.
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     * @see Periodo
     * @see ApiResponse
     * <pre>
     * GET /api/periodos/actual
     *
     * Respuesta exitosa:
     * {
     *   "success": true,
     *   "message": "Periodo actual encontrado",
     *   "data": {
     *     "idPeriodo": 2,
     *     "nombre": "Tarde",
     *     "horaInicio": "12:00",
     *     "horaFin": "18:00"
     *   }
     * }
     *
     * Respuesta no encontrada:
     * {
     *   "success": false,
     *   "message": "No se encontró periodo para la hora actual"
     * }
     * </pre>
     */
    public void getActual(Context ctx) {
        try {
            Periodo periodoActual = periodoService.getPeriodoActual();

            if (periodoActual != null) {
                ctx.json(ApiResponse.success("Periodo actual encontrado", periodoActual));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.error("No se encontró periodo para la hora actual"));
            }
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al obtener periodo actual"));
        }
    }

    /**
     * <p>
     * Crea un nuevo periodo en el sistema.
     * Permite a los administradores definir nuevos bloques horarios para la operación del transporte público.
     * </p>
     * <ul>
     *   <li>Endpoint: <b>POST /api/periodos</b></li>
     *   <li>Valida los datos recibidos y retorna el periodo creado con su ID.</li>
     * </ul>
     *
     * @param ctx {@link Context} Contexto HTTP con cuerpo JSON de {@link Periodo}.
     * @throws IllegalArgumentException Si los datos del periodo son inválidos.
     * @throws SQLException Si ocurre un error al insertar en la base de datos.
     * @see Periodo
     * @see ApiResponse
     * <pre>
     * POST /api/periodos
     * {
     *   "nombre": "Noche",
     *   "horaInicio": "18:00",
     *   "horaFin": "23:59"
     * }
     *
     * Respuesta exitosa:
     * {
     *   "success": true,
     *   "message": "Periodo creado",
     *   "data": {
     *     "idPeriodo": 3,
     *     "nombre": "Noche",
     *     "horaInicio": "18:00",
     *     "horaFin": "23:59"
     *   }
     * }
     *
     * Respuesta error:
     * {
     *   "success": false,
     *   "message": "Error al crear periodo"
     * }
     * </pre>
     */
    public void create(Context ctx) {
        try {
            Periodo periodo = ctx.bodyAsClass(Periodo.class);
            int id = periodoService.createPeriodo(periodo);
            periodo.setIdPeriodo(id);

            ctx.status(HttpStatus.CREATED)
                    .json(ApiResponse.success("Periodo creado", periodo));
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al crear periodo"));
        }
    }

    /**
     * <p>
     * Actualiza los datos de un periodo existente.
     * Permite modificar horarios y nombres para mantener la información actualizada y relevante.
     * </p>
     * <ul>
     *   <li>Endpoint: <b>PUT /api/periodos/{id}</b></li>
     *   <li>Valida existencia y datos recibidos antes de actualizar.</li>
     * </ul>
     *
     * @param ctx {@link Context} Contexto HTTP con parámetro "id" y cuerpo JSON de {@link Periodo}.
     * @throws NumberFormatException Si el ID no es válido.
     * @throws IllegalArgumentException Si los datos del periodo son inválidos.
     * @throws SQLException Si ocurre un error al actualizar en la base de datos.
     * @see Periodo
     * @see ApiResponse
     * <pre>
     * PUT /api/periodos/2
     * {
     *   "nombre": "Tarde Extendida",
     *   "horaInicio": "12:00",
     *   "horaFin": "19:00"
     * }
     *
     * Respuesta exitosa:
     * {
     *   "success": true,
     *   "message": "Periodo actualizado",
     *   "data": {
     *     "idPeriodo": 2,
     *     "nombre": "Tarde Extendida",
     *     "horaInicio": "12:00",
     *     "horaFin": "19:00"
     *   }
     * }
     *
     * Respuesta no encontrada:
     * {
     *   "success": false,
     *   "message": "Periodo no encontrado"
     * }
     * </pre>
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Periodo periodo = ctx.bodyAsClass(Periodo.class);
            periodo.setIdPeriodo(id);

            boolean updated = periodoService.updatePeriodo(periodo);
            if (updated) {
                ctx.json(ApiResponse.success("Periodo actualizado", periodo));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Periodo"));
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error("ID no válido"));
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al actualizar periodo"));
        }
    }

    /**
     * <p>
     * Elimina un periodo por su identificador.
     * Utilizado para depuración y gestión administrativa de bloques horarios obsoletos.
     * </p>
     * <ul>
     *   <li>Endpoint: <b>DELETE /api/periodos/{id}</b></li>
     *   <li>Valida existencia antes de eliminar.</li>
     * </ul>
     *
     * @param ctx {@link Context} Contexto HTTP con parámetro "id" (int).
     * @throws NumberFormatException Si el ID no es válido.
     * @throws IllegalArgumentException Si el ID no corresponde a un periodo existente.
     * @throws SQLException Si ocurre un error al eliminar en la base de datos.
     * @see Periodo
     * @see ApiResponse
     * <pre>
     * DELETE /api/periodos/3
     *
     * Respuesta exitosa:
     * {
     *   "success": true,
     *   "message": "Periodo eliminado"
     * }
     *
     * Respuesta no encontrada:
     * {
     *   "success": false,
     *   "message": "Periodo no encontrado"
     * }
     * </pre>
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean deleted = periodoService.deletePeriodo(id);

            if (deleted) {
                ctx.json(ApiResponse.success("Periodo eliminado"));
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(ApiResponse.notFound("Periodo"));
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error("ID no válido"));
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(ApiResponse.error("Error al eliminar periodo"));
        }
    }
}