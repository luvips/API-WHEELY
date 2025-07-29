package com.wheely;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wheely.di.AppModule;

/**
 * <p>
 * Clase principal del sistema WHEELY para la gestión de transporte público en Tuxtla Gutiérrez, Chiapas.
 * Inicializa el servidor REST con Javalin, configura el mapeo JSON con Jackson y registra todos los módulos de rutas,
 * permitiendo la interacción entre usuarios, administradores y el backend para reportes, consultas y gestión de entidades.
 * </p>
 * <p>
 * <b>Propósito en WHEELY:</b>
 * <ul>
 *   <li>Arranca el servidor HTTP en el puerto 7000 para la API REST.</li>
 *   <li>Configura el mapeo de fechas y horas (LocalTime, LocalDateTime) para compatibilidad con frontend y base de datos.</li>
 *   <li>Habilita CORS para permitir peticiones desde cualquier origen, facilitando la integración con el frontend web.</li>
 *   <li>Registra todos los endpoints de las entidades principales: Usuarios, Rutas, Reportes, Paradas, Coordenadas, Periodos, Tiempos, etc.</li>
 *   <li>Proporciona una ruta raíz de prueba para verificar el estado de la API.</li>
 *   <li>Muestra en consola los endpoints disponibles para facilitar el desarrollo y pruebas.</li>
 * </ul>
 * <p>
 * <b>Ejemplo de uso:</b>
 * <pre>
 * // Ejecutar la aplicación desde la línea de comandos
 * java -jar wheely.jar
 * // Acceder a la API desde el navegador o Postman
 * GET http://localhost:7000/api/usuarios
 * POST http://localhost:7000/api/reportes
 * </pre>
 * </p>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.di.AppModule
 * @see io.javalin.Javalin
 * @see com.fasterxml.jackson.databind.ObjectMapper
 */
public class Main {

    /**
     * <p>
     * Método principal que inicia la aplicación WHEELY.
     * <ul>
     *   <li>Configura el mapeador JSON para soportar tipos de fecha y hora modernos.</li>
     *   <li>Inicializa el servidor Javalin en el puerto 7000.</li>
     *   <li>Habilita CORS para permitir peticiones desde cualquier origen.</li>
     *   <li>Registra todos los módulos de rutas mediante {@link AppModule}.</li>
     *   <li>Agrega una ruta raíz para verificación rápida del estado de la API.</li>
     *   <li>Imprime en consola los endpoints disponibles para consulta y pruebas.</li>
     * </ul>
     * </p>
     * <p>
     * <b>Parámetros:</b>
     * <ul>
     *   <li>{@code args} <code>String[]</code>: Argumentos de línea de comandos (no utilizados en esta versión).</li>
     * </ul>
     * </p>
     * <p>
     * <b>Excepciones:</b>
     * <ul>
     *   <li>{@code Exception} Si ocurre un error al iniciar el servidor o registrar rutas.</li>
     * </ul>
     * </p>
     * <p>
     * <b>Ejemplo de uso:</b>
     * <pre>
     * // Desde la terminal
     * java -jar wheely.jar
     * // Verificar funcionamiento
     * curl http://localhost:7000/
     * </pre>
     * </p>
     *
     * @param args <code>String[]</code> Argumentos de línea de comandos para la aplicación (opcional).
     */
    public static void main(String[] args) {
        // Configurar Jackson para manejar LocalTime y LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson());
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                    it.allowCredentials = true;
                });
            });
        }).start(7000);

        // Registrar todas las rutas
        AppModule.initUsuarios().register(app);
        AppModule.initReportes().register(app);
        AppModule.initTiposReporte().register(app);
        AppModule.initRutas().register(app);
        AppModule.initRecorridos().register(app);
        AppModule.initCoordenadas().register(app);
        AppModule.initParadas().register(app);
        AppModule.initCoordenadasParada().register(app);
        AppModule.initRutasFavoritas().register(app);

        // NUEVAS RUTAS
        AppModule.initPeriodos().register(app);
        AppModule.initTiemposRutaPeriodo().register(app);

        // Ruta de prueba
        app.get("/", ctx -> ctx.result("API Wheely funcionando correctamente"));

        System.out.println("Servidor iniciado en http://localhost:7000");
        System.out.println("Endpoints disponibles:");
        System.out.println("- Usuarios: /api/usuarios");
        System.out.println("- Reportes: /api/reportes");
        System.out.println("- Tipos de Reporte: /api/tipos-reporte");
        System.out.println("- Rutas: /api/rutas");
        System.out.println("- Recorridos: /api/recorridos");
        System.out.println("- Coordenadas: /api/coordenadas");
        System.out.println("- Paradas: /api/paradas");
        System.out.println("- Coordenadas Parada: /api/coordenadas-parada");
        System.out.println("- Rutas Favoritas: /api/rutas-favoritas");
        System.out.println("- Periodos: /api/periodos");
        System.out.println("- Tiempos Ruta-Periodo: /api/tiempos-ruta-periodo");
    }
}