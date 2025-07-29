package com.wheely.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;

/**
 * <p>
 * Clase de configuración centralizada para la conexión a la base de datos MySQL en el sistema WHEELY.
 * Utiliza HikariCP como pool de conexiones para optimizar el rendimiento y la escalabilidad de la API REST,
 * permitiendo la gestión eficiente de usuarios, rutas, reportes y demás entidades del transporte público de Tuxtla Gutiérrez, Chiapas.
 * </p>
 * <p>
 * <b>Propósito en WHEELY:</b>
 * <ul>
 *   <li>Centraliza la configuración de acceso a la base de datos usando variables de entorno (.env).</li>
 *   <li>Implementa el patrón Singleton para reutilizar el pool de conexiones y reducir la sobrecarga.</li>
 *   <li>Proporciona métodos para obtener, cerrar y probar la conexión, facilitando la administración y el monitoreo.</li>
 *   <li>Optimiza la gestión de recursos en operaciones concurrentes de la API (reportes, consultas, estadísticas).</li>
 * </ul>
 * <p>
 * <b>Ejemplo de uso:</b>
 * <pre>
 * // Obtener conexión para un repositorio
 * try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
 *     // Operaciones CRUD sobre entidades como Usuario, Ruta, Reporte
 * }
 * // Verificar estado de la base de datos
 * boolean ok = DatabaseConfig.testConnection();
 * </pre>
 * </p>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.repository.UsuarioRepository
 * @see com.wheely.repository.RutaRepository
 * @see com.wheely.repository.ReporteRepository
 */
public class DatabaseConfig {
    /**
     * Instancia única del pool de conexiones HikariCP.
     */
    private static HikariDataSource dataSource;

    /**
     * <p>
     * Obtiene la fuente de datos configurada para la base de datos MySQL.
     * <ul>
     *   <li>Implementa el patrón Singleton para reutilizar el pool de conexiones.</li>
     *   <li>Carga credenciales y parámetros desde el archivo .env para mayor seguridad y flexibilidad.</li>
     *   <li>Configura propiedades avanzadas de HikariCP para optimizar el rendimiento en producción.</li>
     * </ul>
     * </p>
     * <pre>
     * DataSource ds = DatabaseConfig.getDataSource();
     * try (Connection conn = ds.getConnection()) {
     *     // Consultas SQL
     * }
     * </pre>
     *
     * @return {@code DataSource} Fuente de datos configurada y lista para uso en repositorios.
     * @throws RuntimeException Si ocurre un error al cargar la configuración o inicializar el pool.
     * @see #closeDataSource()
     * @see #testConnection()
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            // Cargar variables de entorno desde archivo .env
            Dotenv dotenv = Dotenv.load();

            // Obtener configuración de base de datos
            String host = dotenv.get("DB_HOST");
            String dbName = dotenv.get("DB_SCHEMA");
            String jdbcUrl = String.format("jdbc:mysql://%s:3306/%s", host, dbName);

            // Configurar HikariCP (pool de conexiones)
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dotenv.get("DB_USER"));
            config.setPassword(dotenv.get("DB_PASS"));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Configuraciones adicionales para optimizar el pool
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            // Configuraciones adicionales para MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);
            System.out.println(" Conexión a base de datos configurada: " + dbName);
        }
        return dataSource;
    }

    /**
     * <p>
     * Cierra el pool de conexiones HikariCP, liberando todos los recursos asociados.
     * <ul>
     *   <li>Debe llamarse al finalizar la aplicación o en pruebas para evitar fugas de memoria.</li>
     *   <li>Verifica que el pool esté abierto antes de intentar cerrarlo.</li>
     * </ul>
     * </p>
     * <pre>
     * DatabaseConfig.closeDataSource();
     * </pre>
     *
     * @throws RuntimeException Si ocurre un error al cerrar el pool de conexiones.
     * @see #getDataSource()
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println(" Pool de conexiones cerrado");
        }
    }

    /**
     * <p>
     * Verifica si la conexión a la base de datos funciona correctamente.
     * <ul>
     *   <li>Realiza una conexión de prueba y valida el estado en 5 segundos.</li>
     *   <li>Útil para monitoreo, diagnósticos y pruebas de integración.</li>
     * </ul>
     * </p>
     * <pre>
     * if (!DatabaseConfig.testConnection()) {
     *     // Notificar error de conexión
     * }
     * </pre>
     *
     * @return {@code boolean} <ul>
     *   <li>{@code true} si la conexión es exitosa y válida.</li>
     *   <li>{@code false} si ocurre cualquier error o la conexión no es válida.</li>
     * </ul>
     * @throws Exception Si ocurre un error inesperado al obtener la conexión.
     * @see #getDataSource()
     */
    public static boolean testConnection() {
        try (var connection = getDataSource().getConnection()) {
            return connection.isValid(5);
        } catch (Exception e) {
            System.err.println(" Error al conectar con la base de datos: " + e.getMessage());
            return false;
        }
    }
}