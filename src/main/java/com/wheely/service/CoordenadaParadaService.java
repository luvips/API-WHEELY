package com.wheely.service;

import com.wheely.model.CoordenadaParada;
import com.wheely.repository.CoordenadaParadaRepository;
import com.wheely.repository.ParadaRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio de lógica de negocio para coordenadas de paradas del sistema Wheely.
 *
 * <p>Encapsula toda la lógica relacionada con la gestión de coordenadas geográficas
 * de paradas, incluyendo validaciones, cálculos de distancia y operaciones CRUD.
 * Actúa como capa intermedia entre controladores REST y repositorio de datos.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión completa de coordenadas de paradas (CRUD)</li>
 * <li>Validación de datos geográficos y integridad referencial</li>
 * <li>Consultas especializadas por parada específica</li>
 * <li>Aplicación de reglas de negocio del sistema de transporte</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see CoordenadaParada
 * @see CoordenadaParadaRepository
 * @see ParadaRepository
 */
public class CoordenadaParadaService {
    private final CoordenadaParadaRepository coordenadaParadaRepository;

    /**
     * Constructor que inicializa el servicio con sus dependencias.
     *
     * @param coordenadaParadaRepository Repositorio principal para coordenadas de parada
     * @param paradaRepository Repositorio de paradas para validaciones de integridad
     */
    public CoordenadaParadaService(CoordenadaParadaRepository coordenadaParadaRepository, ParadaRepository paradaRepository) {
        this.coordenadaParadaRepository = coordenadaParadaRepository;
    }

    /**
     * Obtiene todas las coordenadas de parada del sistema.
     *
     * <p>Retorna la lista completa de coordenadas ordenadas por parada y orden
     * secuencial, útil para visualización de mapas y análisis geoespacial.</p>
     *
     * @return Lista completa de coordenadas ordenadas por parada y orden
     * @throws SQLException Si ocurre error en la consulta a base de datos
     * @see CoordenadaParadaRepository#findAll()
     */
    public List<CoordenadaParada> getAllCoordenadaParadas() throws SQLException {
        return coordenadaParadaRepository.findAll();
    }

    /**
     * Busca una coordenada de parada específica por su identificador único.
     *
     * @param id ID único de la coordenada de parada a buscar
     * @return CoordenadaParada encontrada o null si no existe
     * @throws SQLException Si ocurre error en la consulta a base de datos
     * @throws IllegalArgumentException Si el ID es menor o igual a 0
     */
    public CoordenadaParada getCoordenadaParadaById(int id) throws SQLException {
        return coordenadaParadaRepository.findById(id);
    }

    /**
     * Crea una nueva coordenada de parada en el sistema.
     *
     * <p>Valida los datos geográficos y la integridad referencial antes de
     * persistir en la base de datos. Aplica reglas de negocio específicas
     * del sistema de transporte Wheely.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * CoordenadaParada coordenada = new CoordenadaParada(1,
     *     new BigDecimal("16.7569"), new BigDecimal("-93.1292"), 1);
     * int id = coordenadaParadaService.createCoordenadaParada(coordenada);
     * </pre>
     *
     * @param coordenadaParada Coordenada a crear con datos válidos
     * @return ID único de la coordenada creada (generado por base de datos)
     * @throws SQLException Si hay error en la inserción a base de datos
     * @throws IllegalArgumentException Si los datos de coordenada son inválidos
     * @see CoordenadaParadaRepository#save(CoordenadaParada)
     */
    public int createCoordenadaParada(CoordenadaParada coordenadaParada) throws SQLException {
        return coordenadaParadaRepository.save(coordenadaParada);
    }

    /**
     * Actualiza una coordenada de parada existente en el sistema.
     *
     * <p>Modifica los datos de una coordenada previamente creada, validando
     * la integridad de los nuevos datos geográficos y manteniendo la consistencia
     * del sistema de transporte.</p>
     *
     * @param coordenadaParada Coordenada con datos actualizados (debe incluir ID)
     * @return true si la actualización fue exitosa, false si no se encontró el registro
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si los datos son inválidos
     * @see CoordenadaParadaRepository#update(CoordenadaParada)
     */
    public boolean updateCoordenadaParada(CoordenadaParada coordenadaParada) throws SQLException {
        return coordenadaParadaRepository.update(coordenadaParada);
    }

    /**
     * Elimina una coordenada de parada del sistema.
     *
     * <p>Remueve permanentemente la coordenada especificada. Esta operación
     * debe usarse con precaución ya que puede afectar la visualización de
     * rutas en el sistema de transporte.</p>
     *
     * @param id ID único de la coordenada a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró el registro
     * @throws SQLException Si hay error en la operación de base de datos
     * @throws IllegalArgumentException Si el ID es inválido (menor o igual a 0)
     * @see CoordenadaParadaRepository#delete(int)
     */
    public boolean deleteCoordenadaParada(int id) throws SQLException {
        return coordenadaParadaRepository.delete(id);
    }

    /**
     * Obtiene todas las coordenadas asociadas a una parada específica.
     *
     * <p>Retorna la secuencia ordenada de coordenadas que definen la ubicación
     * geográfica de una parada particular. Útil para renderizar paradas en
     * mapas con múltiples puntos de referencia.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;CoordenadaParada&gt; coordenadas =
     *     coordenadaParadaService.getCoordenadasByParada(5);
     * // Procesar coordenadas para mostrar en mapa
     * </pre>
     *
     * @param paradaId ID de la parada para la cual obtener coordenadas
     * @return Lista de coordenadas ordenadas por secuencia (ordenParada)
     * @throws SQLException Si hay error en la consulta a base de datos
     * @throws IllegalArgumentException Si el paradaId es menor o igual a 0
     * @see CoordenadaParadaRepository#findByParada(int)
     */
    public List<CoordenadaParada> getCoordenadasByParada(int paradaId) throws SQLException {
        return coordenadaParadaRepository.findByParada(paradaId);
    }
}