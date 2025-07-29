package com.wheely.di;

import com.wheely.controller.*;
import com.wheely.repository.*;
import com.wheely.routes.*;
import com.wheely.service.*;

/**
 * <p>
 * Módulo de configuración de dependencias para la aplicación WHEELY.
 * Centraliza la inicialización y el ensamblaje de los componentes (repositorios, servicios, controladores y rutas)
 * de cada entidad del sistema de transporte público de Tuxtla Gutiérrez, Chiapas.
 * </p>
 * <p>
 * <b>Propósito en WHEELY:</b>
 * <ul>
 *   <li>Facilita la inyección de dependencias y el desacoplamiento entre capas.</li>
 *   <li>Permite registrar rutas REST de forma modular y escalable.</li>
 *   <li>Optimiza el mantenimiento y la extensión del sistema al centralizar la configuración.</li>
 *   <li>Soporta nuevas entidades y funcionalidades agregando métodos de inicialización.</li>
 * </ul>
 * <p>
 * <b>Ejemplo de uso:</b>
 * <pre>
 * // En la clase principal Main
 * Javalin app = Javalin.create().start(7000);
 * AppModule.initUsuarios().register(app);
 * AppModule.initReportes().register(app);
 * </pre>
 * </p>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.routes.UsuarioRoutes
 * @see com.wheely.routes.ReporteRoutes
 * @see com.wheely.routes.RutaRoutes
 * @see com.wheely.routes.ParadaRoutes
 * @see com.wheely.routes.CoordenadaRoutes
 * @see com.wheely.routes.PeriodoRoutes
 * @see com.wheely.routes.TiempoRutaPeriodoRoutes
 */
public class AppModule {

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad Usuario.
     * Ensambla repositorio, servicio y controlador, y retorna el objeto de rutas.
     * </p>
     *
     * @return {@code UsuarioRoutes} Rutas REST para gestión de usuarios.
     * @see com.wheely.controller.UsuarioController
     * @see com.wheely.service.UsuarioService
     * @see com.wheely.repository.UsuarioRepository
     */
    public static UsuarioRoutes initUsuarios() {
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        UsuarioController usuarioController = new UsuarioController(usuarioService);
        return new UsuarioRoutes(usuarioController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad Reporte.
     * Ensambla repositorios, servicio y controlador, y retorna el objeto de rutas.
     * </p>
     *
     * @return {@code ReporteRoutes} Rutas REST para gestión de reportes de incidencias.
     * @see com.wheely.controller.ReporteController
     * @see com.wheely.service.ReporteService
     * @see com.wheely.repository.ReporteRepository
     * @see com.wheely.repository.UsuarioRepository
     */
    public static ReporteRoutes initReportes() {
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        ReporteRepository reporteRepository = new ReporteRepository();
        ReporteService reporteService = new ReporteService(reporteRepository, usuarioRepository);
        ReporteController reporteController = new ReporteController(reporteService);
        return new ReporteRoutes(reporteController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad TipoReporte.
     * </p>
     *
     * @return {@code TipoReporteRoutes} Rutas REST para gestión de tipos de reporte.
     * @see com.wheely.controller.TipoReporteController
     * @see com.wheely.service.TipoReporteService
     * @see com.wheely.repository.TipoReporteRepository
     */
    public static TipoReporteRoutes initTiposReporte() {
        TipoReporteRepository tipoReporteRepository = new TipoReporteRepository();
        TipoReporteService tipoReporteService = new TipoReporteService(tipoReporteRepository);
        TipoReporteController tipoReporteController = new TipoReporteController(tipoReporteService);
        return new TipoReporteRoutes(tipoReporteController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad Ruta.
     * </p>
     *
     * @return {@code RutaRoutes} Rutas REST para gestión de rutas de transporte.
     * @see com.wheely.controller.RutaController
     * @see com.wheely.service.RutaService
     * @see com.wheely.repository.RutaRepository
     */
    public static RutaRoutes initRutas() {
        RutaRepository rutaRepository = new RutaRepository();
        RutaService rutaService = new RutaService(rutaRepository);
        RutaController rutaController = new RutaController(rutaService);
        return new RutaRoutes(rutaController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad Recorrido.
     * </p>
     *
     * @return {@code RecorridoRoutes} Rutas REST para gestión de recorridos de rutas.
     * @see com.wheely.controller.RecorridoController
     * @see com.wheely.service.RecorridoService
     * @see com.wheely.repository.RecorridoRepository
     * @see com.wheely.repository.RutaRepository
     */
    public static RecorridoRoutes initRecorridos() {
        RutaRepository rutaRepository = new RutaRepository();
        RecorridoRepository recorridoRepository = new RecorridoRepository();
        RecorridoService recorridoService = new RecorridoService(recorridoRepository, rutaRepository);
        RecorridoController recorridoController = new RecorridoController(recorridoService);
        return new RecorridoRoutes(recorridoController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad Coordenada.
     * </p>
     *
     * @return {@code CoordenadaRoutes} Rutas REST para gestión de coordenadas geográficas.
     * @see com.wheely.controller.CoordenadaController
     * @see com.wheely.service.CoordenadaService
     * @see com.wheely.repository.CoordenadaRepository
     * @see com.wheely.repository.RecorridoRepository
     */
    public static CoordenadaRoutes initCoordenadas() {
        RecorridoRepository recorridoRepository = new RecorridoRepository();
        CoordenadaRepository coordenadaRepository = new CoordenadaRepository();
        CoordenadaService coordenadaService = new CoordenadaService(coordenadaRepository, recorridoRepository);
        CoordenadaController coordenadaController = new CoordenadaController(coordenadaService);
        return new CoordenadaRoutes(coordenadaController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad Parada.
     * </p>
     *
     * @return {@code ParadaRoutes} Rutas REST para gestión de paradas de transporte.
     * @see com.wheely.controller.ParadaController
     * @see com.wheely.service.ParadaService
     * @see com.wheely.repository.ParadaRepository
     * @see com.wheely.repository.RecorridoRepository
     */
    public static ParadaRoutes initParadas() {
        RecorridoRepository recorridoRepository = new RecorridoRepository();
        ParadaRepository paradaRepository = new ParadaRepository();
        ParadaService paradaService = new ParadaService(paradaRepository, recorridoRepository);
        ParadaController paradaController = new ParadaController(paradaService);
        return new ParadaRoutes(paradaController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad CoordenadaParada.
     * </p>
     *
     * @return {@code CoordenadaParadaRoutes} Rutas REST para gestión de coordenadas de paradas.
     * @see com.wheely.controller.CoordenadaParadaController
     * @see com.wheely.service.CoordenadaParadaService
     * @see com.wheely.repository.CoordenadaParadaRepository
     * @see com.wheely.repository.ParadaRepository
     */
    public static CoordenadaParadaRoutes initCoordenadasParada() {
        ParadaRepository paradaRepository = new ParadaRepository();
        CoordenadaParadaRepository coordenadaParadaRepository = new CoordenadaParadaRepository();
        CoordenadaParadaService coordenadaParadaService = new CoordenadaParadaService(coordenadaParadaRepository, paradaRepository);
        CoordenadaParadaController coordenadaParadaController = new CoordenadaParadaController(coordenadaParadaService);
        return new CoordenadaParadaRoutes(coordenadaParadaController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad RutaFavorita.
     * </p>
     *
     * @return {@code RutaFavoritaRoutes} Rutas REST para gestión de rutas favoritas de usuarios.
     * @see com.wheely.controller.RutaFavoritaController
     * @see com.wheely.service.RutaFavoritaService
     * @see com.wheely.repository.RutaFavoritaRepository
     * @see com.wheely.repository.UsuarioRepository
     * @see com.wheely.repository.RutaRepository
     */
    public static RutaFavoritaRoutes initRutasFavoritas() {
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        RutaRepository rutaRepository = new RutaRepository();
        RutaFavoritaRepository rutaFavoritaRepository = new RutaFavoritaRepository();
        RutaFavoritaService rutaFavoritaService = new RutaFavoritaService(rutaFavoritaRepository, usuarioRepository, rutaRepository);
        RutaFavoritaController rutaFavoritaController = new RutaFavoritaController(rutaFavoritaService);
        return new RutaFavoritaRoutes(rutaFavoritaController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad Periodo.
     * </p>
     *
     * @return {@code PeriodoRoutes} Rutas REST para gestión de periodos de operación.
     * @see com.wheely.controller.PeriodoController
     * @see com.wheely.service.PeriodoService
     * @see com.wheely.repository.PeriodoRepository
     */
    public static PeriodoRoutes initPeriodos() {
        PeriodoRepository periodoRepository = new PeriodoRepository();
        PeriodoService periodoService = new PeriodoService(periodoRepository);
        PeriodoController periodoController = new PeriodoController(periodoService);
        return new PeriodoRoutes(periodoController);
    }

    /**
     * <p>
     * Inicializa el módulo de rutas para la entidad TiempoRutaPeriodo.
     * </p>
     *
     * @return {@code TiempoRutaPeriodoRoutes} Rutas REST para gestión de tiempos de ruta por periodo.
     * @see com.wheely.controller.TiempoRutaPeriodoController
     * @see com.wheely.service.TiempoRutaPeriodoService
     * @see com.wheely.repository.TiempoRutaPeriodoRepository
     * @see com.wheely.repository.RutaRepository
     * @see com.wheely.repository.PeriodoRepository
     */
    public static TiempoRutaPeriodoRoutes initTiemposRutaPeriodo() {
        TiempoRutaPeriodoRepository tiempoRutaPeriodoRepository = new TiempoRutaPeriodoRepository();
        RutaRepository rutaRepository = new RutaRepository();
        PeriodoRepository periodoRepository = new PeriodoRepository();
        TiempoRutaPeriodoService tiempoRutaPeriodoService = new TiempoRutaPeriodoService(
                tiempoRutaPeriodoRepository,
                rutaRepository,
                periodoRepository
        );
        TiempoRutaPeriodoController tiempoRutaPeriodoController = new TiempoRutaPeriodoController(tiempoRutaPeriodoService);
        return new TiempoRutaPeriodoRoutes(tiempoRutaPeriodoController);
    }
}