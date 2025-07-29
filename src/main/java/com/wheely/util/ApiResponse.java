package com.wheely.util;

/**
 * <p>
 * Clase utilitaria para estandarizar las respuestas de la API REST del sistema WHEELY.
 * Proporciona un formato consistente para las respuestas exitosas, de error, validación y estados HTTP,
 * facilitando la integración entre el backend Java (Javalin + MySQL) y el frontend web.
 * </p>
 * <p>
 * <b>Propósito en WHEELY:</b>
 * <ul>
 *   <li>Centraliza la estructura de las respuestas para operaciones de gestión de transporte público.</li>
 *   <li>Permite a usuarios y administradores recibir mensajes claros sobre incidencias, consultas y acciones.</li>
 *   <li>Incluye timestamp para trazabilidad y auditoría de eventos en el sistema.</li>
 *   <li>Soporta respuestas con datos, mensajes descriptivos y detalles de error/validación.</li>
 * </ul>
 * <p>
 * <b>Ejemplo de uso:</b>
 * <pre>
 * // En un controlador de reportes de incidencias
 * ApiResponse respuesta = ApiResponse.success("Reporte registrado exitosamente", reporte);
 * // En caso de error de validación
 * ApiResponse error = ApiResponse.validationError(new String[]{"La ruta no existe", "El usuario no está activo"});
 * </pre>
 * </p>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Reporte
 * @see com.wheely.model.Usuario
 * @see com.wheely.model.Ruta
 */
public class ApiResponse {

    /**
     * Indica si la operación fue exitosa.
     */
    private boolean success;

    /**
     * Mensaje descriptivo de la respuesta (éxito, error, validación, etc.).
     */
    private String message;

    /**
     * Datos asociados a la respuesta (puede ser cualquier entidad del sistema WHEELY).
     */
    private Object data;

    /**
     * Marca de tiempo en milisegundos de la generación de la respuesta.
     */
    private long timestamp;

    /**
     * <p>
     * Constructor vacío. Inicializa la respuesta sin valores.
     * Útil para frameworks que requieren instanciación por reflexión.
     * </p>
     */
    public ApiResponse() {
        // Inicialización por defecto
    }

    /**
     * <p>
     * Constructor completo para crear una respuesta personalizada.
     * </p>
     *
     * @param success <code>boolean</code> indica si la operación fue exitosa
     * @param message <code>String</code> mensaje descriptivo de la respuesta
     * @param data <code>Object</code> datos asociados (puede ser entidad, lista, error, etc.)
     */
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * <p>
     * Constructor para respuestas exitosas con datos.
     * </p>
     *
     * @param message <code>String</code> mensaje descriptivo
     * @param data <code>Object</code> datos a retornar (ejemplo: entidad Usuario, Ruta, Reporte)
     */
    public ApiResponse(String message, Object data) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * <p>
     * Constructor para respuestas exitosas sin datos.
     * </p>
     *
     * @param message <code>String</code> mensaje descriptivo
     */
    public ApiResponse(String message) {
        this.success = true;
        this.message = message;
        this.data = null;
        this.timestamp = System.currentTimeMillis();
    }

    // Métodos estáticos para crear respuestas comunes

    /**
     * <p>
     * Crea una respuesta exitosa con datos.
     * </p>
     * <pre>
     * ApiResponse resp = ApiResponse.success("Usuario creado", usuario);
     * </pre>
     *
     * @param message <code>String</code> mensaje descriptivo
     * @param data <code>Object</code> datos a retornar (entidad, lista, etc.)
     * @return <code>ApiResponse</code> exitosa con datos
     * @see #success(String)
     */
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    /**
     * <p>
     * Crea una respuesta exitosa sin datos.
     * </p>
     * <pre>
     * ApiResponse resp = ApiResponse.success("Operación completada");
     * </pre>
     *
     * @param message <code>String</code> mensaje descriptivo
     * @return <code>ApiResponse</code> exitosa sin datos
     * @see #success(String, Object)
     */
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null);
    }

    /**
     * <p>
     * Crea una respuesta de error genérica.
     * </p>
     * <pre>
     * ApiResponse error = ApiResponse.error("Error al registrar la parada");
     * </pre>
     *
     * @param message <code>String</code> mensaje de error
     * @return <code>ApiResponse</code> de error sin detalles
     * @see #error(String, Object)
     */
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }

    /**
     * <p>
     * Crea una respuesta de error con detalles adicionales.
     * </p>
     * <pre>
     * ApiResponse error = ApiResponse.error("Error de base de datos", ex.getMessage());
     * </pre>
     *
     * @param message <code>String</code> mensaje de error
     * @param errorDetails <code>Object</code> detalles del error (stacktrace, código, etc.)
     * @return <code>ApiResponse</code> de error con detalles
     * @see #error(String)
     */
    public static ApiResponse error(String message, Object errorDetails) {
        return new ApiResponse(false, message, errorDetails);
    }

    /**
     * <p>
     * Crea una respuesta de error de validación con lista de errores específicos.
     * </p>
     * <pre>
     * ApiResponse error = ApiResponse.validationError(new String[]{"Campo ruta requerido", "Usuario inválido"});
     * </pre>
     *
     * @param validationErrors <code>String[]</code> array de mensajes de error de validación
     * @return <code>ApiResponse</code> de error de validación con detalles
     * @see ValidationErrorResponse
     */
    public static ApiResponse validationError(String[] validationErrors) {
        ValidationErrorResponse errorData = new ValidationErrorResponse(validationErrors);
        return new ApiResponse(false, "Error de validación", errorData);
    }

    /**
     * <p>
     * Crea una respuesta de recurso no encontrado (HTTP 404).
     * </p>
     * <pre>
     * ApiResponse nf = ApiResponse.notFound("Ruta");
     * </pre>
     *
     * @param resource <code>String</code> nombre del recurso no encontrado (ejemplo: "Usuario", "Reporte")
     * @return <code>ApiResponse</code> de recurso no encontrado
     */
    public static ApiResponse notFound(String resource) {
        return new ApiResponse(false, resource + " no encontrado", null);
    }

    /**
     * <p>
     * Crea una respuesta de no autorizado (HTTP 401).
     * </p>
     * <pre>
     * ApiResponse unauth = ApiResponse.unauthorized();
     * </pre>
     *
     * @return <code>ApiResponse</code> de no autorizado
     */
    public static ApiResponse unauthorized() {
        return new ApiResponse(false, "No autorizado", null);
    }

    /**
     * <p>
     * Crea una respuesta de acceso prohibido (HTTP 403).
     * </p>
     * <pre>
     * ApiResponse forb = ApiResponse.forbidden();
     * </pre>
     *
     * @return <code>ApiResponse</code> de acceso prohibido
     */
    public static ApiResponse forbidden() {
        return new ApiResponse(false, "Acceso prohibido", null);
    }

    /**
     * <p>
     * Obtiene el estado de éxito de la respuesta.
     * </p>
     *
     * @return <code>boolean</code> <ul>
     *   <li><code>true</code> si la operación fue exitosa</li>
     *   <li><code>false</code> si hubo error, validación o acceso denegado</li>
     * </ul>
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * <p>
     * Establece el estado de éxito de la respuesta.
     * </p>
     *
     * @param success <code>boolean</code> valor a asignar
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * <p>
     * Obtiene el mensaje descriptivo de la respuesta.
     * </p>
     *
     * @return <code>String</code> mensaje de la respuesta
     */
    public String getMessage() {
        return message;
    }

    /**
     * <p>
     * Establece el mensaje descriptivo de la respuesta.
     * </p>
     *
     * @param message <code>String</code> mensaje a asignar
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * <p>
     * Obtiene los datos asociados a la respuesta.
     * </p>
     *
     * @return <code>Object</code> datos retornados (puede ser entidad, lista, error, etc.)
     */
    public Object getData() {
        return data;
    }

    /**
     * <p>
     * Establece los datos asociados a la respuesta.
     * </p>
     *
     * @param data <code>Object</code> datos a asignar
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * <p>
     * Obtiene el timestamp de la respuesta.
     * </p>
     *
     * @return <code>long</code> marca de tiempo en milisegundos
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * <p>
     * Establece el timestamp de la respuesta.
     * </p>
     *
     * @param timestamp <code>long</code> valor de marca de tiempo
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * <p>
     * Representación en cadena de la respuesta para depuración y logging.
     * </p>
     *
     * @return <code>String</code> formato legible de la respuesta
     */
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * <p>
     * Clase interna para encapsular errores de validación en el sistema WHEELY.
     * Permite retornar múltiples errores y su conteo, facilitando la retroalimentación al usuario.
     * </p>
     * <pre>
     * ApiResponse resp = ApiResponse.validationError(new String[]{"Campo ruta requerido", "Usuario inválido"});
     * </pre>
     *
     * @author [Tu Nombre]
     * @version 1.0.0
     * @since 2025
     */
    public static class ValidationErrorResponse {

        /**
         * Array de mensajes de error de validación.
         */
        private String[] errors;

        /**
         * Número total de errores de validación.
         */
        private int errorCount;

        /**
         * <p>
         * Constructor para encapsular errores de validación.
         * </p>
         *
         * @param errors <code>String[]</code> array de mensajes de error
         */
        public ValidationErrorResponse(String[] errors) {
            this.errors = errors;
            this.errorCount = (errors != null) ? errors.length : 0;
        }

        /**
         * <p>
         * Obtiene el array de errores de validación.
         * </p>
         *
         * @return <code>String[]</code> mensajes de error
         */
        public String[] getErrors() {
            return errors;
        }

        /**
         * <p>
         * Establece el array de errores de validación.
         * </p>
         *
         * @param errors <code>String[]</code> mensajes de error
         */
        public void setErrors(String[] errors) {
            this.errors = errors;
            this.errorCount = (errors != null) ? errors.length : 0;
        }

        /**
         * <p>
         * Obtiene el número total de errores de validación.
         * </p>
         *
         * @return <code>int</code> cantidad de errores
         */
        public int getErrorCount() {
            return errorCount;
        }

        /**
         * <p>
         * Establece el número total de errores de validación.
         * </p>
         *
         * @param errorCount <code>int</code> cantidad de errores
         */
        public void setErrorCount(int errorCount) {
            this.errorCount = errorCount;
        }
    }
}