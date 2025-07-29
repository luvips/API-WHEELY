package com.wheely.service;

import com.wheely.model.Periodo;
import com.wheely.repository.PeriodoRepository;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

/**
 * Servicio para la lógica de negocio de periodos temporales del sistema Wheely.
 *
 * <p>Maneja la gestión completa de periodos del día utilizados para segmentar
 * y analizar patrones de tráfico. Incluye validaciones de solapamiento de horarios,
 * detección de periodo actual y aplicación de reglas de negocio específicas.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión CRUD completa de periodos temporales</li>
 * <li>Validación de solapamiento de horarios entre periodos</li>
 * <li>Detección automática del periodo actual según hora del sistema</li>
 * <li>Prevención de duplicados por nombre de periodo</li>
 * <li>Aplicación de reglas de negocio para horarios válidos</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see Periodo
 * @see PeriodoRepository
 * @see TiempoRutaPeriodoService
 */
public class PeriodoService {
    private final PeriodoRepository periodoRepository;

    /**
     * Constructor que inicializa el servicio con su repositorio.
     *
     * @param periodoRepository Repositorio para operaciones de periodos
     */
    public PeriodoService(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    /**
     * Obtiene todos los periodos ordenados por hora de inicio.
     *
     * <p>Retorna la lista completa de periodos del sistema ordenados
     * cronológicamente, útil para configuración de horarios y análisis
     * temporal del servicio de transporte.</p>
     *
     * @return Lista de periodos ordenados por hora de inicio
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public List<Periodo> getAllPeriodos() throws SQLException {
        return periodoRepository.findAll();
    }

    /**
     * Obtiene un periodo específico por su identificador único.
     *
     * @param id ID único del periodo a buscar
     * @return Periodo encontrado o null si no existe
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public Periodo getPeriodoById(int id) throws SQLException {
        return periodoRepository.findById(id);
    }

    /**
     * Busca un periodo por su nombre específico.
     *
     * <p>Permite localizar periodos usando su identificador textual
     * (ej: "Mañana", "Tarde", "Noche"). Útil para consultas por nombre
     * en interfaces de usuario y APIs.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Periodo mañana = periodoService.getPeriodoByNombre("Mañana");
     * if (mañana != null) {
     *     // Procesar periodo matutino
     * }
     * </pre>
     *
     * @param nombre Nombre del periodo a buscar (no puede estar vacío)
     * @return Periodo encontrado o null si no existe
     * @throws SQLException Si hay error en la consulta
     * @throws IllegalArgumentException Si el nombre es null o vacío
     */
    public Periodo getPeriodoByNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del periodo no puede estar vacío");
        }
        return periodoRepository.findByNombre(nombre.trim());
    }

    /**
     * Crea un nuevo periodo en el sistema.
     *
     * <p>Inserta un nuevo periodo temporal con validaciones completas de
     * integridad y reglas de negocio. Previene solapamiento de horarios
     * y duplicación de nombres en el sistema.</p>
     *
     * <p>Validaciones aplicadas:</p>
     * <ul>
     * <li>Unicidad del nombre del periodo</li>
     * <li>Validez de horarios de inicio y fin</li>
     * <li>No solapamiento con periodos existentes</li>
     * <li>Longitud apropiada de nombre y descripción</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Periodo nuevoPeriodo = new Periodo("Madrugada",
     *     LocalTime.of(0, 0), LocalTime.of(6, 0),
     *     "Periodo nocturno con tráfico mínimo");
     * int id = periodoService.createPeriodo(nuevoPeriodo);
     * </pre>
     *
     * @param periodo Periodo a crear con datos válidos
     * @return ID único del periodo creado (generado por base de datos)
     * @throws SQLException Si hay error en la inserción
     * @throws IllegalArgumentException Si los datos son inválidos o hay conflictos
     * @see #validatePeriodo(Periodo)
     * @see #validateNoOverlap(Periodo)
     */
    public int createPeriodo(Periodo periodo) throws SQLException {
        validatePeriodo(periodo);

        // Verificar que no exista un periodo con el mismo nombre
        if (periodoRepository.nombreExists(periodo.getNombrePeriodo())) {
            throw new IllegalArgumentException("Ya existe un periodo con ese nombre");
        }

        // Validar que no haya solapamiento de horarios
        validateNoOverlap(periodo);

        return periodoRepository.save(periodo);
    }

    /**
     * Actualiza un periodo existente en el sistema.
     *
     * <p>Modifica los datos de un periodo previamente creado, aplicando
     * las mismas validaciones que en la creación pero considerando
     * la exclusión del propio registro en verificaciones de unicidad.</p>
     *
     * @param periodo Periodo con datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si los datos son inválidos o el periodo no existe
     * @see #validatePeriodo(Periodo)
     * @see #validateNoOverlapForUpdate(Periodo)
     */
    public boolean updatePeriodo(Periodo periodo) throws SQLException {
        validatePeriodo(periodo);

        // Verificar que el periodo existe
        Periodo existente = periodoRepository.findById(periodo.getIdPeriodo());
        if (existente == null) {
            throw new IllegalArgumentException("Periodo no encontrado");
        }

        // Verificar que no exista otro periodo con el mismo nombre
        Periodo periodoConMismoNombre = periodoRepository.findByNombre(periodo.getNombrePeriodo());
        if (periodoConMismoNombre != null && periodoConMismoNombre.getIdPeriodo() != periodo.getIdPeriodo()) {
            throw new IllegalArgumentException("Ya existe otro periodo con ese nombre");
        }

        // Validar que no haya solapamiento de horarios con otros periodos
        validateNoOverlapForUpdate(periodo);

        return periodoRepository.update(periodo);
    }

    /**
     * Elimina un periodo del sistema.
     *
     * <p>Remueve permanentemente un periodo del sistema. Esta operación
     * debe usarse con precaución ya que puede afectar registros relacionados
     * de tiempos de ruta que dependan de este periodo.</p>
     *
     * @param id ID único del periodo a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si el periodo no existe
     */
    public boolean deletePeriodo(int id) throws SQLException {
        Periodo periodo = periodoRepository.findById(id);
        if (periodo == null) {
            throw new IllegalArgumentException("Periodo no encontrado");
        }

        return periodoRepository.delete(id);
    }

    /**
     * Obtiene el periodo actual basado en la hora del sistema.
     *
     * <p>Determina automáticamente el periodo temporal correspondiente
     * a la hora actual del sistema. Método fundamental para cálculos
     * de ETA dinámicos y análisis de tráfico en tiempo real.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Periodo actual = periodoService.getPeriodoActual();
     * if (actual != null) {
     *     System.out.println("Periodo actual: " + actual.getNombrePeriodo());
     * }
     * </pre>
     *
     * @return Periodo correspondiente a la hora actual o null si no se encuentra
     * @throws SQLException Si hay error en la consulta de periodos
     */
    public Periodo getPeriodoActual() throws SQLException {
        return periodoRepository.findPeriodoActual();
    }

    /**
     * Valida los datos básicos de un periodo.
     * Método privado que aplica reglas de negocio de validación.
     *
     * <p>Validaciones incluidas:</p>
     * <ul>
     * <li>Periodo no nulo</li>
     * <li>Nombre obligatorio y longitud máxima 20 caracteres</li>
     * <li>Horas de inicio y fin obligatorias</li>
     * <li>Descripción opcional máximo 100 caracteres</li>
     * </ul>
     *
     * @param periodo Periodo a validar
     * @throws IllegalArgumentException Si algún dato es inválido
     */
    private void validatePeriodo(Periodo periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo no puede ser nulo");
        }

        if (periodo.getNombrePeriodo() == null || periodo.getNombrePeriodo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del periodo es obligatorio");
        }

        if (periodo.getNombrePeriodo().length() > 20) {
            throw new IllegalArgumentException("El nombre del periodo no puede exceder 20 caracteres");
        }

        if (periodo.getHoraInicio() == null) {
            throw new IllegalArgumentException("La hora de inicio es obligatoria");
        }

        if (periodo.getHoraFin() == null) {
            throw new IllegalArgumentException("La hora de fin es obligatoria");
        }

        if (periodo.getDescripcion() != null && periodo.getDescripcion().length() > 100) {
            throw new IllegalArgumentException("La descripción no puede exceder 100 caracteres");
        }
    }

    /**
     * Valida que no haya solapamiento de horarios con otros periodos existentes.
     * Método privado para validación de nuevos periodos.
     *
     * @param nuevoPeriodo Periodo a validar contra solapamientos
     * @throws SQLException Si hay error en consultas de validación
     * @throws IllegalArgumentException Si existe solapamiento de horarios
     */
    private void validateNoOverlap(Periodo nuevoPeriodo) throws SQLException {
        List<Periodo> periodosExistentes = periodoRepository.findAll();

        for (Periodo existente : periodosExistentes) {
            if (hasTimeOverlap(nuevoPeriodo.getHoraInicio(), nuevoPeriodo.getHoraFin(),
                    existente.getHoraInicio(), existente.getHoraFin())) {
                throw new IllegalArgumentException(
                        "El horario del periodo se solapa con el periodo: " + existente.getNombrePeriodo());
            }
        }
    }

    /**
     * Valida solapamiento para actualizaciones de periodos existentes.
     * Método privado que excluye el propio periodo de la validación.
     *
     * @param periodoActualizado Periodo siendo actualizado
     * @throws SQLException Si hay error en consultas de validación
     * @throws IllegalArgumentException Si existe solapamiento con otros periodos
     */
    private void validateNoOverlapForUpdate(Periodo periodoActualizado) throws SQLException {
        List<Periodo> periodosExistentes = periodoRepository.findAll();

        for (Periodo existente : periodosExistentes) {
            // Excluir el propio periodo de la validación
            if (existente.getIdPeriodo() == periodoActualizado.getIdPeriodo()) {
                continue;
            }

            if (hasTimeOverlap(periodoActualizado.getHoraInicio(), periodoActualizado.getHoraFin(),
                    existente.getHoraInicio(), existente.getHoraFin())) {
                throw new IllegalArgumentException(
                        "El horario del periodo se solapa con el periodo: " + existente.getNombrePeriodo());
            }
        }
    }

    /**
     * Verifica si dos rangos de tiempo se solapan.
     * Método utilitario privado para detección de conflictos horarios.
     *
     * @param inicio1 Hora de inicio del primer rango
     * @param fin1 Hora de fin del primer rango
     * @param inicio2 Hora de inicio del segundo rango
     * @param fin2 Hora de fin del segundo rango
     * @return true si los rangos se solapan, false en caso contrario
     */
    private boolean hasTimeOverlap(LocalTime inicio1, LocalTime fin1,
                                   LocalTime inicio2, LocalTime fin2) {
        // Lógica de detección de solapamiento considerando rangos de 24 horas
        return !(fin1.isBefore(inicio2) || fin1.equals(inicio2) ||
                inicio1.isAfter(fin2) || inicio1.equals(fin2));
    }
}