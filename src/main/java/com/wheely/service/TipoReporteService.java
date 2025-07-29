package com.wheely.service;

import com.wheely.model.TipoReporte;
import com.wheely.repository.TipoReporteRepository;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la lógica de negocio de tipos de reporte del sistema Wheely.
 *
 * <p>Maneja la gestión de categorías utilizadas para clasificar reportes
 * de usuarios. Incluye validaciones básicas y operaciones CRUD para
 * tipos de reporte en el sistema de transporte.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión CRUD completa de tipos de reporte</li>
 * <li>Búsqueda de tipos por nombre con coincidencia parcial</li>
 * <li>Validación de datos básicos de tipos</li>
 * <li>Soporte para categorización de reportes</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see TipoReporte
 * @see TipoReporteRepository
 */
public class TipoReporteService {
    private final TipoReporteRepository tipoReporteRepository;

    /**
     * Constructor que inicializa el servicio con su repositorio.
     *
     * @param tipoReporteRepository Repositorio para operaciones de tipos de reporte
     */
    public TipoReporteService(TipoReporteRepository tipoReporteRepository) {
        this.tipoReporteRepository = tipoReporteRepository;
    }

    /**
     * Obtiene todos los tipos de reporte del sistema.
     *
     * @return Lista completa de tipos de reporte
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public List<TipoReporte> getAllTiposReporte() throws SQLException {
        return tipoReporteRepository.findAll();
    }

    /**
     * Obtiene un tipo de reporte específico por su ID.
     *
     * @param id ID único del tipo de reporte a buscar
     * @return TipoReporte encontrado o null si no existe
     * @throws SQLException Si hay error en la consulta
     */
    public TipoReporte getTipoReporteById(int id) throws SQLException {
        return tipoReporteRepository.findById(id);
    }

    /**
     * Crea un nuevo tipo de reporte en el sistema.
     *
     * <p>Valida que el nombre del tipo no esté vacío antes de
     * crear el registro en la base de datos.</p>
     *
     * @param tipoReporte Tipo de reporte a crear con datos válidos
     * @return ID único del tipo creado
     * @throws SQLException Si hay error en la inserción
     * @throws IllegalArgumentException Si los datos son inválidos
     */
    public int createTipoReporte(TipoReporte tipoReporte) throws SQLException {
        validateTipoReporte(tipoReporte);
        return tipoReporteRepository.save(tipoReporte);
    }

    /**
     * Actualiza un tipo de reporte existente en el sistema.
     *
     * @param tipoReporte Tipo con datos actualizados
     * @return true si se actualizó correctamente
     * @throws SQLException Si hay error en la operación
     * @throws IllegalArgumentException Si los datos son inválidos
     */
    public boolean updateTipoReporte(TipoReporte tipoReporte) throws SQLException {
        validateTipoReporte(tipoReporte);
        return tipoReporteRepository.update(tipoReporte);
    }

    /**
     * Elimina un tipo de reporte del sistema.
     *
     * @param id ID único del tipo de reporte a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException Si hay error en la operación
     */
    public boolean deleteTipoReporte(int id) throws SQLException {
        return tipoReporteRepository.delete(id);
    }

    /**
     * Busca tipos de reporte por nombre usando coincidencia parcial.
     *
     * <p>Permite búsqueda flexible de tipos usando texto parcial.
     * Útil para funcionalidades de búsqueda y filtrado.</p>
     *
     * @param nombre Texto a buscar en nombres de tipos
     * @return Lista de tipos que coinciden con el criterio
     * @throws SQLException Si hay error en la consulta
     */
    public List<TipoReporte> buscarTiposPorNombre(String nombre) throws SQLException {
        return tipoReporteRepository.findByNombre(nombre);
    }

    /**
     * Valida los datos básicos de un tipo de reporte.
     * Método privado que aplica reglas de negocio de validación.
     *
     * <p>Validaciones aplicadas:</p>
     * <ul>
     * <li>Tipo de reporte no nulo</li>
     * <li>Nombre obligatorio y no vacío</li>
     * </ul>
     *
     * @param tipoReporte Tipo de reporte a validar
     * @throws IllegalArgumentException Si algún dato es inválido
     */
    private void validateTipoReporte(TipoReporte tipoReporte) {
        if (tipoReporte == null || tipoReporte.getNombreTipo() == null || tipoReporte.getNombreTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre del tipo de reporte es requerido");
        }
    }
}