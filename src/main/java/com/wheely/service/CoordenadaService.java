package com.wheely.service;

import com.wheely.model.Coordenada;
import com.wheely.repository.CoordenadaRepository;
import com.wheely.repository.RecorridoRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la lógica de negocio de coordenadas geográficas del sistema Wheely.
 *
 * <p>Maneja la gestión completa de coordenadas GPS que definen los recorridos
 * de las rutas de transporte público. Las coordenadas forman secuencias ordenadas
 * que representan el trazado geográfico exacto de cada recorrido en el sistema.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión CRUD completa de coordenadas de recorridos</li>
 * <li>Consultas especializadas por recorrido específico</li>
 * <li>Mantenimiento del orden secuencial de puntos GPS</li>
 * <li>Soporte para visualización de rutas en mapas</li>
 * <li>Facilitación de cálculos geoespaciales del sistema</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see Coordenada
 * @see CoordenadaRepository
 * @see RecorridoRepository
 */
public class CoordenadaService {
    private final CoordenadaRepository coordenadaRepository;

    /**
     * Constructor que inicializa el servicio con sus dependencias.
     *
     * @param coordenadaRepository Repositorio principal para operaciones de coordenadas
     * @param recorridoRepository Repositorio de recorridos para validaciones de integridad
     */
    public CoordenadaService(CoordenadaRepository coordenadaRepository, RecorridoRepository recorridoRepository) {
        this.coordenadaRepository = coordenadaRepository;
    }

    /**
     * Obtiene todas las coordenadas del sistema.
     *
     * <p>Retorna la lista completa de coordenadas GPS almacenadas en el sistema,
     * útil para análisis geoespaciales globales, visualización de todas las rutas
     * y operaciones de mantenimiento del sistema de transporte.</p>
     *
     * @return Lista completa de coordenadas ordenadas por recorrido y orden de punto
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public List<Coordenada> getAllCoordenadas() throws SQLException {
        return coordenadaRepository.findAll();
    }

    /**
     * Obtiene una coordenada específica por su identificador único.
     *
     * @param id ID único de la coordenada a buscar
     * @return Coordenada encontrada o null si no existe
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public Coordenada getCoordenadaById(int id) throws SQLException {
        return coordenadaRepository.findById(id);
    }

    /**
     * Crea una nueva coordenada en el sistema.
     *
     * <p>Inserta una nueva coordenada GPS en un recorrido específico,
     * manteniendo el orden secuencial apropiado para la correcta
     * representación geográfica del trazado de la ruta.</p>
     *
     * <p>La coordenada debe incluir:</p>
     * <ul>
     * <li>ID del recorrido al que pertenece</li>
     * <li>Valores de latitud y longitud válidos</li>
     * <li>Orden del punto en la secuencia del recorrido</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * Coordenada nuevaCoordenada = new Coordenada(1,
     *     new BigDecimal("16.7569"), new BigDecimal("-93.1292"), 5);
     * int id = coordenadaService.createCoordenada(nuevaCoordenada);
     * System.out.println("Coordenada creada con ID: " + id);
     * </pre>
     *
     * @param coordenada Coordenada a crear con datos geográficos válidos
     * @return ID único de la coordenada creada (generado por base de datos)
     * @throws SQLException Si hay error en la inserción
     */
    public int createCoordenada(Coordenada coordenada) throws SQLException {
        return coordenadaRepository.save(coordenada);
    }

    /**
     * Actualiza una coordenada existente en el sistema.
     *
     * <p>Modifica los datos de una coordenada previamente creada, útil para
     * ajustes en el trazado de rutas, correcciones de precisión GPS y
     * mantenimiento de la exactitud geográfica del sistema.</p>
     *
     * @param coordenada Coordenada con datos actualizados (debe incluir ID válido)
     * @return true si la actualización fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     */
    public boolean updateCoordenada(Coordenada coordenada) throws SQLException {
        return coordenadaRepository.update(coordenada);
    }

    /**
     * Elimina una coordenada del sistema.
     *
     * <p>Remueve permanentemente una coordenada del sistema. Esta operación
     * debe usarse con precaución ya que puede afectar la continuidad del
     * trazado geográfico del recorrido al que pertenece.</p>
     *
     * @param id ID único de la coordenada a eliminar
     * @return true si la eliminación fue exitosa, false si no se encontró
     * @throws SQLException Si hay error en la operación de base de datos
     */
    public boolean deleteCoordenada(int id) throws SQLException {
        return coordenadaRepository.delete(id);
    }

    /**
     * Obtiene todas las coordenadas de un recorrido específico.
     *
     * <p>Retorna la secuencia ordenada de coordenadas GPS que definen el
     * trazado geográfico de un recorrido particular. Las coordenadas se
     * retornan ordenadas por su posición secuencial para permitir la
     * representación correcta de la ruta en mapas.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;Coordenada&gt; trazadoRecorrido =
     *     coordenadaService.getCoordenadasByRecorrido(3);
     * // Procesar coordenadas para mostrar ruta en mapa
     * for (Coordenada coord : trazadoRecorrido) {
     *     System.out.println("Punto " + coord.getOrdenPunto() +
     *         ": " + coord.getLatitud() + ", " + coord.getLongitud());
     * }
     * </pre>
     *
     * @param recorridoId ID del recorrido para obtener sus coordenadas
     * @return Lista de coordenadas ordenadas por posición secuencial (ordenPunto)
     * @throws SQLException Si hay error en la consulta
     */
    public List<Coordenada> getCoordenadasByRecorrido(int recorridoId) throws SQLException {
        return coordenadaRepository.findByRecorrido(recorridoId);
    }
}