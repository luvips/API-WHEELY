package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.service.RutaFavoritaService;
import com.wheely.util.ApiResponse;

/**
 * Controlador REST para la gestión de rutas favoritas del sistema Wheely.
 *
 * <p>Esta clase maneja todas las peticiones HTTP relacionadas con la gestión de rutas
 * favoritas de los usuarios del sistema de transporte público. Las rutas favoritas
 * permiten a los usuarios guardar y acceder rápidamente a las rutas que utilizan
 * con mayor frecuencia, mejorando la experiencia de usuario y facilitando la
 * planificación de viajes habituales.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión de rutas favoritas por usuario</li>
 * <li>Agregar rutas a favoritos de manera sencilla</li>
 * <li>Eliminar rutas de favoritos cuando ya no sean necesarias</li>
 * <li>Consulta de todas las rutas favoritas de un usuario específico</li>
 * <li>Listado general de relaciones usuario-ruta favorita</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see RutaFavoritaService
 * @see com.wheely.model.RutaFavorita
 * @see com.wheely.util.ApiResponse
 */
public class RutaFavoritaController {
    private final RutaFavoritaService rutaFavoritaService;

    /**
     * Constructor del controlador de rutas favoritas.
     *
     * @param rutaFavoritaService Servicio que contiene la lógica de negocio para rutas favoritas
     */
    public RutaFavoritaController(RutaFavoritaService rutaFavoritaService) {
        this.rutaFavoritaService = rutaFavoritaService;
    }

    /**
     * Obtiene todas las relaciones de rutas favoritas en el sistema.
     *
     * <p>Este método maneja peticiones GET a /rutas-favoritas y retorna una lista completa
     * de todas las relaciones usuario-ruta favorita registradas en el sistema.
     * Principalmente útil para administradores que necesiten analizar patrones
     * de uso y preferencias de rutas de los usuarios.</p>
     *
     * <pre>
     * GET /rutas-favoritas
     * Response: {
     *   "success": true,
     *   "message": "Favoritas obtenidas",
     *   "data": [
     *     {
     *       "idRutaFavorita": 1,
     *       "idUsuario": 5,
     *       "idRuta": 3,
     *       "fechaAgregada": "2025-01-15T10:30:00"
     *     },
     *     {
     *       "idRutaFavorita": 2,
     *       "idUsuario": 8,
     *       "idRuta": 7,
     *       "fechaAgregada": "2025-01-16T08:15:00"
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto de la petición HTTP que contiene parámetros y permite enviar respuesta
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see RutaFavoritaService#getAllRutasFavoritas()
     * @see ApiResponse#success(String, Object)
     */
    public void getAll(Context ctx) {
        try {
            ctx.json(ApiResponse.success("Favoritas obtenidas", rutaFavoritaService.getAllRutasFavoritas()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Obtiene una relación de ruta favorita específica por su identificador único.
     *
     * <p>Busca y retorna los datos de una relación usuario-ruta favorita específica
     * basándose en el ID proporcionado en la URL. Incluye información completa
     * sobre el usuario, la ruta y la fecha cuando se agregó a favoritos.</p>
     *
     * <pre>
     * GET /rutas-favoritas/1
     * Response exitosa: {
     *   "success": true,
     *   "message": "Favorita encontrada",
     *   "data": {
     *     "idRutaFavorita": 1,
     *     "idUsuario": 5,
     *     "idRuta": 3,
     *     "fechaAgregada": "2025-01-15T10:30:00"
     *   }
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Favorita no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {id} en la URL
     * @throws NumberFormatException si el ID no es un número válido
     * @throws Exception si ocurre error en la consulta a la base de datos
     *
     * @see RutaFavoritaService#getRutaFavoritaById(int)
     * @see ApiResponse#notFound(String)
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var favorita = rutaFavoritaService.getRutaFavoritaById(id);
            ctx.json(favorita != null ? ApiResponse.success("Favorita encontrada", favorita) : ApiResponse.notFound("Favorita"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Obtiene todas las rutas favoritas de un usuario específico.
     *
     * <p>Filtra y retorna únicamente las rutas favoritas que pertenecen al usuario
     * especificado. Este endpoint es fundamental para las aplicaciones móviles
     * y web que necesitan mostrar al usuario sus rutas favoritas personales
     * para acceso rápido y planificación de viajes.</p>
     *
     * <pre>
     * GET /usuarios/5/rutas-favoritas
     * Response: {
     *   "success": true,
     *   "message": "Favoritas del usuario",
     *   "data": [
     *     {
     *       "idRutaFavorita": 1,
     *       "idUsuario": 5,
     *       "idRuta": 3,
     *       "fechaAgregada": "2025-01-15T10:30:00"
     *     },
     *     {
     *       "idRutaFavorita": 5,
     *       "idUsuario": 5,
     *       "idRuta": 12,
     *       "fechaAgregada": "2025-01-18T14:20:00"
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene el parámetro {usuarioId} en la URL
     * @throws NumberFormatException si el usuarioId no es un número válido
     * @throws Exception si ocurre error en la consulta
     *
     * @see RutaFavoritaService#getRutasFavoritasByUsuario(int)
     * @see ApiResponse#success(String, Object)
     */
    public void getByUsuario(Context ctx) {
        try {
            int usuarioId = Integer.parseInt(ctx.pathParam("usuarioId"));
            ctx.json(ApiResponse.success("Favoritas del usuario", rutaFavoritaService.getRutasFavoritasByUsuario(usuarioId)));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Agrega una nueva ruta a los favoritos de un usuario.
     *
     * <p>Procesa una petición POST para agregar una ruta específica a la lista
     * de favoritos de un usuario. Valida que tanto el usuario como la ruta
     * existan en el sistema y que no se cree una relación duplicada.
     * La fecha de agregado se establece automáticamente al momento actual.</p>
     *
     * <pre>
     * POST /usuarios/5/rutas-favoritas
     * Body: {
     *   "rutaId": 7
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Favorita agregada",
     *   "data": {
     *     "idRutaFavorita": 15,
     *     "idUsuario": 5,
     *     "idRuta": 7,
     *     "fechaAgregada": "2025-01-20T09:45:00"
     *   }
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene usuarioId en URL y rutaId en el cuerpo JSON
     * @throws NumberFormatException si el usuarioId no es un número válido
     * @throws Exception si los datos son inválidos o ya existe la relación
     *
     * @see RutaFavoritaService#createRutaFavorita(com.wheely.model.RutaFavorita)
     * @see AgregarFavoritaRequest
     */
    public void agregarFavorita(Context ctx) {
        try {
            int usuarioId = Integer.parseInt(ctx.pathParam("usuarioId"));
            var request = ctx.bodyAsClass(AgregarFavoritaRequest.class);

            var favorita = new com.wheely.model.RutaFavorita();
            favorita.setIdUsuario(usuarioId);
            favorita.setIdRuta(request.rutaId);

            int id = rutaFavoritaService.createRutaFavorita(favorita);
            favorita.setIdRutaFavorita(id);
            ctx.status(HttpStatus.CREATED).json(ApiResponse.success("Favorita agregada", favorita));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Elimina una ruta de los favoritos de un usuario.
     *
     * <p>Remueve la relación entre un usuario y una ruta específica de su lista
     * de favoritos. Esta operación es segura y reversible, ya que el usuario
     * puede volver a agregar la ruta como favorita en cualquier momento.
     * Útil cuando las preferencias de transporte del usuario cambian.</p>
     *
     * <pre>
     * DELETE /usuarios/5/rutas-favoritas/7
     * Response exitosa: {
     *   "success": true,
     *   "message": "Favorita eliminada"
     * }
     *
     * Response no encontrada: {
     *   "success": false,
     *   "message": "Favorita no encontrada"
     * }
     * </pre>
     *
     * @param ctx Contexto HTTP que contiene usuarioId y rutaId en la URL
     * @throws NumberFormatException si usuarioId o rutaId no son números válidos
     * @throws Exception si ocurre error en la eliminación
     *
     * @see RutaFavoritaService#deleteRutaFavorita(int, int)
     * @see ApiResponse#success(String)
     */
    public void eliminarFavorita(Context ctx) {
        try {
            int usuarioId = Integer.parseInt(ctx.pathParam("usuarioId"));
            int rutaId = Integer.parseInt(ctx.pathParam("rutaId"));
            ctx.json(rutaFavoritaService.deleteRutaFavorita(usuarioId, rutaId) ?
                    ApiResponse.success("Favorita eliminada") : ApiResponse.notFound("Favorita"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ApiResponse.error("Error"));
        }
    }

    /**
     * Clase interna para solicitudes de agregar ruta favorita.
     *
     * <p>Encapsula los datos necesarios para agregar una nueva ruta a favoritos,
     * proporcionando una estructura clara para las peticiones JSON del frontend.</p>
     *
     * @since 2025
     */
    public static class AgregarFavoritaRequest {
        /** ID de la ruta que se desea agregar a favoritos */
        public int rutaId;

        /**
         * Constructor vacío requerido para deserialización JSON.
         */
        public AgregarFavoritaRequest() {}

        /**
         * Constructor con parámetros para crear solicitudes de agregar favorita.
         *
         * @param rutaId ID de la ruta a agregar a favoritos
         */
        public AgregarFavoritaRequest(int rutaId) {
            this.rutaId = rutaId;
        }
    }
}