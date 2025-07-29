package com.wheely.service;

import com.wheely.model.TiempoRutaPeriodo;
import com.wheely.repository.TiempoRutaPeriodoRepository;
import com.wheely.repository.RutaRepository;
import com.wheely.repository.PeriodoRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para gestión de tiempos de ruta por periodo en sistema Wheely.
 *
 * <p>Maneja la lógica de negocio para tiempos promedio de viaje según diferentes
 * periodos del día. Permite análisis temporal de rutas y cálculo de ETAs
 * basados en patrones históricos de tráfico del transporte público.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión de tiempos promedio por combinación ruta-periodo</li>
 * <li>Validación de integridad referencial con rutas y periodos</li>
 * <li>Cálculo de ETAs dinámicos según hora actual</li>
 * <li>Prevención de duplicados en combinaciones ruta-periodo</li>
 * <li>Análisis de patrones temporales de tráfico</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see TiempoRutaPeriodo
 * @see TiempoRutaPeriodoRepository
 * @see RutaRepository
 * @see PeriodoRepository
 */
public class TiempoRutaPeriodoService {
    private final TiempoRutaPeriodoRepository tiempoRutaPeriodoRepository;
    private final RutaRepository rutaRepository;
    private final PeriodoRepository periodoRepository;

    /**
     * Constructor que inicializa el servicio con todas sus dependencias.
     *
     * @param tiempoRutaPeriodoRepository Repositorio principal para tiempos
     * @param rutaRepository Repositorio de rutas para validaciones
     * @param periodoRepository Repositorio de periodos para validaciones
     */
    public TiempoRutaPeriodoService(TiempoRutaPeriodoRepository tiempoRutaPeriodoRepository,
                                    RutaRepository rutaRepository,
                                    PeriodoRepository periodoRepository) {
        this.tiempoRutaPeriodoRepository = tiempoRutaPeriodoRepository;
        this.rutaRepository = rutaRepository;
        this.periodoRepository = periodoRepository;
    }

    /**
     * Obtiene todos los tiempos de ruta por periodo del sistema.
     *
     * <p>Retorna la lista completa de tiempos promedio registrados para todas
     * las combinaciones ruta-periodo, útil para análisis estadísticos y
     * reportes de rendimiento del sistema de transporte.</p>
     *
     * @return Lista completa de tiempos ruta-periodo
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public List<TiempoRutaPeriodo> getAllTiempos() throws SQLException {
        return tiempoRutaPeriodoRepository.findAll();
    }

    /**
     * Obtiene un tiempo específico por su identificador único.
     *
     * @param id ID único del tiempo ruta-periodo
     * @return TiempoRutaPeriodo encontrado o null si no existe
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public TiempoRutaPeriodo getTiempoById(int id) throws SQLException {
        return tiempoRutaPeriodoRepository.findById(id);
    }

    /**
     * Obtiene todos los tiempos registrados para una ruta específica.
     *
     * <p>Retorna los tiempos promedio de una ruta en todos los periodos
     * del día, permitiendo analizar variaciones de tráfico y optimizar
     * horarios de servicio.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;TiempoRutaPeriodo&gt; tiempos =
     *     tiempoRutaPeriodoService.getTiemposByRuta(5);
     * // Analizar tiempos por periodo para optimización
     * </pre>
     *
     * @param idRuta ID de la ruta para consultar tiempos
     * @return Lista de tiempos para la ruta en diferentes periodos
     * @throws SQLException Si hay error en la consulta
     * @throws IllegalArgumentException Si el ID de ruta no es válido (≤ 0)
     */
    public List<TiempoRutaPeriodo> getTiemposByRuta(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("ID de ruta no válido");
        }
        return tiempoRutaPeriodoRepository.findByRuta(idRuta);
    }

    /**
     * Obtiene todos los tiempos de un periodo específico del día.
     *
     * <p>Retorna los tiempos promedio de todas las rutas durante un periodo
     * particular (mañana, tarde, noche), útil para análisis de patrones
     * de tráfico temporales en el sistema completo.</p>
     *
     * @param idPeriodo ID del periodo temporal a consultar
     * @return Lista de tiempos para el periodo en todas las rutas
     * @throws SQLException Si hay error en la consulta
     * @throws IllegalArgumentException Si el ID de periodo no es válido (≤ 0)
     */
    public List<TiempoRutaPeriodo> getTiemposByPeriodo(int idPeriodo) throws SQLException {
        if (idPeriodo <= 0) {
            throw new IllegalArgumentException("ID de periodo no válido");
        }
        return tiempoRutaPeriodoRepository.findByPeriodo(idPeriodo);
    }

    /**
     * Obtiene el tiempo específico para una combinación ruta-periodo.
     *
     * <p>Busca el tiempo promedio exacto para una ruta en un periodo
     * específico. Útil para cálculos de ETA precisos y consultas
     * de tiempo de viaje personalizadas.</p>
     *
     * @param idRuta ID de la ruta a consultar
     * @param idPeriodo ID del periodo temporal
     * @return TiempoRutaPeriodo encontrado o null si no existe la combinación
     * @throws SQLException Si hay error en la consulta
     * @throws IllegalArgumentException Si algún ID no es válido (≤ 0)
     */
    public TiempoRutaPeriodo getTiempoByRutaAndPeriodo(int idRuta, int idPeriodo) throws SQLException {
        if (idRuta <= 0 || idPeriodo <= 0) {
            throw new IllegalArgumentException("IDs de ruta y periodo deben ser válidos");
        }
        return tiempoRutaPeriodoRepository.findByRutaAndPeriodo(idRuta, idPeriodo);
    }

    /**
     * Crea un nuevo registro de tiempo ruta-periodo.
     *
     * <p>Inserta un nuevo tiempo promedio para una combinación específica
     * de ruta y periodo. Valida la integridad referencial y previene
     * duplicados en el sistema.</p>
     *
     * <p>Validaciones aplicadas:</p>
     * <ul>
     * <li>Existencia de ruta y periodo referenciados</li>
     * <li>Unicidad de combinación ruta-periodo</li>
     * <li>Rango válido de tiempo promedio (1-300 minutos)</li>
     * </ul>
     *
     * @param tiempoRutaPeriodo Tiempo a crear con datos válidos
     * @return ID único del tiempo creado (generado por base de datos)
     * @throws SQLException Si hay error en la inserción
     * @throws IllegalArgumentException Si los datos son inválidos o ya existe la combinación
     * @see #validateTiempoRutaPeriodo(TiempoRutaPeriodo)
     * @see #validateReferences(TiempoRutaPeriodo)
     */
    public int createTiempo(TiempoRutaPeriodo tiempoRutaPeriodo) throws SQLException {
        validateTiempoRutaPeriodo(tiempoRutaPeriodo);
        validateReferences(tiempoRutaPeriodo);

        // Verificar que no exista ya esta combinación ruta-periodo
        if (tiempoRutaPeriodoRepository.existsRutaPeriodo(
                tiempoRutaPeriodo.getIdRuta(),
                tiempoRutaPeriodo.getIdPeriodo())) {
            throw new IllegalArgumentException("Ya existe un tiempo para esta combinación de ruta y periodo");
        }

        return tiempoRutaPeriodoRepository.save(tiempoRutaPeriodo);
    }

    /**
     * Crea o actualiza el tiempo para una combinación ruta-periodo.
     *
     * <p>Operación inteligente que actualiza un registro existente o crea
     * uno nuevo según la disponibilidad. Ideal para actualización de
     * tiempos promedio basados en datos históricos recientes.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * // Actualizar tiempo matutino de ruta 3 a 25 minutos
     * int id = tiempoRutaPeriodoService.createOrUpdateTiempo(3, 1, 25);
     * </pre>
     *
     * @param idRuta ID de la ruta del sistema
     * @param idPeriodo ID del periodo temporal (1=Mañana, 2=Tarde, 3=Noche)
     * @param tiempoPromedio Tiempo promedio en minutos (1-300 rango válido)
     * @return ID del registro creado o actualizado
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si los parámetros son inválidos
     */
    public int createOrUpdateTiempo(int idRuta, int idPeriodo, int tiempoPromedio) throws SQLException {
        TiempoRutaPeriodo existente = tiempoRutaPeriodoRepository.findByRutaAndPeriodo(idRuta, idPeriodo);

        if (existente != null) {
            // Actualizar tiempo existente
            existente.setTiempoPromedio(tiempoPromedio);
            tiempoRutaPeriodoRepository.update(existente);
            return existente.getIdTiempoRutaPeriodo();
        } else {
            // Crear nuevo tiempo
            TiempoRutaPeriodo nuevo = new TiempoRutaPeriodo(idRuta, idPeriodo, tiempoPromedio);
            return createTiempo(nuevo);
        }
    }

    /**
     * Obtiene el tiempo promedio actual para una ruta basado en la hora actual.
     *
     * <p>Calcula el ETA dinámico determinando el periodo actual según la hora
     * del sistema y retornando el tiempo promedio correspondiente. Método
     * clave para predicciones de tiempo de viaje en tiempo real.</p>
     *
     * @param idRuta ID de la ruta para calcular ETA
     * @return Tiempo promedio en minutos o -1 si no se encuentra información
     * @throws SQLException Si hay error en las consultas
     * @throws IllegalArgumentException Si el ID de ruta no es válido (≤ 0)
     */
    public int getTiempoPromedioActual(int idRuta) throws SQLException {
        if (idRuta <= 0) {
            throw new IllegalArgumentException("ID de ruta no válido");
        }

        // Obtener el periodo actual
        var periodoActual = periodoRepository.findPeriodoActual();
        if (periodoActual == null) {
            return -1; // No hay periodo definido para la hora actual
        }

        // Obtener el tiempo para la ruta en el periodo actual
        TiempoRutaPeriodo tiempo = tiempoRutaPeriodoRepository.findByRutaAndPeriodo(idRuta, periodoActual.getIdPeriodo());
        return tiempo != null ? tiempo.getTiempoPromedio() : -1;
    }

    /**
     * Valida los datos básicos de un tiempo ruta-periodo.
     * Método privado que aplica reglas de negocio de validación.
     *
     * @param tiempoRutaPeriodo Tiempo a validar
     * @throws IllegalArgumentException Si algún dato es inválido
     */
    private void validateTiempoRutaPeriodo(TiempoRutaPeriodo tiempoRutaPeriodo) {
        if (tiempoRutaPeriodo == null) {
            throw new IllegalArgumentException("El tiempo ruta-periodo no puede ser nulo");
        }

        if (tiempoRutaPeriodo.getIdRuta() <= 0) {
            throw new IllegalArgumentException("ID de ruta no válido");
        }

        if (tiempoRutaPeriodo.getIdPeriodo() <= 0) {
            throw new IllegalArgumentException("ID de periodo no válido");
        }

        if (tiempoRutaPeriodo.getTiempoPromedio() <= 0) {
            throw new IllegalArgumentException("El tiempo promedio debe ser mayor a 0 minutos");
        }

        if (tiempoRutaPeriodo.getTiempoPromedio() > 300) { // 5 horas máximo
            throw new IllegalArgumentException("El tiempo promedio no puede exceder 300 minutos");
        }
    }

    /**
     * Valida que existan las referencias a ruta y periodo.
     * Método privado que verifica integridad referencial.
     *
     * @param tiempoRutaPeriodo Tiempo a validar
     * @throws SQLException Si hay error en las consultas de validación
     * @throws IllegalArgumentException Si alguna referencia no existe
     */
    private void validateReferences(TiempoRutaPeriodo tiempoRutaPeriodo) throws SQLException {
        // Verificar que existe la ruta
        if (rutaRepository.findById(tiempoRutaPeriodo.getIdRuta()) == null) {
            throw new IllegalArgumentException("La ruta especificada no existe");
        }

        // Verificar que existe el periodo
        if (periodoRepository.findById(tiempoRutaPeriodo.getIdPeriodo()) == null) {
            throw new IllegalArgumentException("El periodo especificado no existe");
        }
    }

    public boolean updateTiempo(TiempoRutaPeriodo tiempo) {
        return false;
    }

    public boolean createTiempo(int id) {
        return false;
    }
}