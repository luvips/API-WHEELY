package com.wheely.service;

import com.wheely.model.RutaFavorita;
import com.wheely.repository.RutaFavoritaRepository;
import com.wheely.repository.UsuarioRepository;
import com.wheely.repository.RutaRepository;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la lógica de negocio de rutas favoritas del sistema Wheely.
 *
 * <p>Maneja la gestión completa de las rutas marcadas como favoritas por los usuarios,
 * incluyendo validaciones de integridad referencial, prevención de duplicados y
 * facilitación de acceso rápido a rutas preferidas por cada usuario.</p>
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 * <li>Gestión de rutas favoritas por usuario individual</li>
 * <li>Validación de existencia de usuarios y rutas referenciadas</li>
 * <li>Prevención de duplicados en combinaciones usuario-ruta</li>
 * <li>Consultas especializadas por usuario y por ruta</li>
 * <li>Control de unicidad en relaciones de favoritos</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see RutaFavorita
 * @see RutaFavoritaRepository
 * @see UsuarioRepository
 * @see RutaRepository
 */
public class RutaFavoritaService {
    private final RutaFavoritaRepository rutaFavoritaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RutaRepository rutaRepository;

    /**
     * Constructor que inicializa el servicio con sus dependencias.
     *
     * @param rutaFavoritaRepository Repositorio principal para operaciones de rutas favoritas
     * @param usuarioRepository Repositorio de usuarios para validaciones de integridad
     * @param rutaRepository Repositorio de rutas para validaciones de integridad
     */
    public RutaFavoritaService(RutaFavoritaRepository rutaFavoritaRepository,
                               UsuarioRepository usuarioRepository,
                               RutaRepository rutaRepository) {
        this.rutaFavoritaRepository = rutaFavoritaRepository;
        this.usuarioRepository = usuarioRepository;
        this.rutaRepository = rutaRepository;
    }

    /**
     * Obtiene todas las rutas favoritas del sistema.
     *
     * <p>Retorna la lista completa de todas las relaciones usuario-ruta favorita,
     * útil para análisis estadístico de preferencias de usuarios y rutas
     * más populares en el sistema de transporte.</p>
     *
     * @return Lista completa de rutas favoritas del sistema
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public List<RutaFavorita> getAllRutasFavoritas() throws SQLException {
        return rutaFavoritaRepository.findAll();
    }

    /**
     * Obtiene una ruta favorita específica por su identificador único.
     *
     * @param id ID único de la ruta favorita a buscar
     * @return RutaFavorita encontrada o null si no existe
     * @throws SQLException Si hay error en la consulta a base de datos
     */
    public RutaFavorita getRutaFavoritaById(int id) throws SQLException {
        return rutaFavoritaRepository.findById(id);
    }

    /**
     * Obtiene todas las rutas favoritas de un usuario específico.
     *
     * <p>Retorna la lista personalizada de rutas marcadas como favoritas
     * por un usuario particular. Útil para mostrar accesos rápidos
     * personalizados y mejorar la experiencia de usuario.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * List&lt;RutaFavorita&gt; misFavoritas =
     *     rutaFavoritaService.getRutasFavoritasByUsuario(10);
     * System.out.println("Usuario tiene " + misFavoritas.size() + " rutas favoritas");
     * </pre>
     *
     * @param usuarioId ID del usuario para obtener sus rutas favoritas
     * @return Lista de rutas favoritas del usuario ordenadas por ruta
     * @throws SQLException Si hay error en la consulta
     */
    public List<RutaFavorita> getRutasFavoritasByUsuario(int usuarioId) throws SQLException {
        return rutaFavoritaRepository.findByUsuario(usuarioId);
    }

    /**
     * Obtiene todos los usuarios que tienen una ruta específica como favorita.
     *
     * <p>Retorna la lista de usuarios que han marcado una ruta particular
     * como favorita. Útil para análisis de popularidad de rutas y métricas
     * de preferencias de usuarios en el sistema de transporte.</p>
     *
     * @param rutaId ID de la ruta para obtener usuarios que la favorecen
     * @return Lista de favoritas que incluyen la ruta especificada
     * @throws SQLException Si hay error en la consulta
     */
    public List<RutaFavorita> getFavoritasByRuta(int rutaId) throws SQLException {
        return rutaFavoritaRepository.findByRuta(rutaId);
    }

    /**
     * Crea una nueva relación de ruta favorita.
     *
     * <p>Agrega una ruta a la lista de favoritas de un usuario, aplicando
     * validaciones completas de integridad referencial y prevención de
     * duplicados. Garantiza que cada combinación usuario-ruta sea única.</p>
     *
     * <p>Validaciones aplicadas:</p>
     * <ul>
     * <li>Existencia del usuario en el sistema</li>
     * <li>Existencia de la ruta en el sistema</li>
     * <li>Unicidad de la combinación usuario-ruta</li>
     * <li>Integridad referencial completa</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * RutaFavorita nuevaFavorita = new RutaFavorita();
     * nuevaFavorita.setIdUsuario(10);
     * nuevaFavorita.setIdRuta(5);
     * int id = rutaFavoritaService.createRutaFavorita(nuevaFavorita);
     * System.out.println("Favorita creada con ID: " + id);
     * </pre>
     *
     * @param rutaFavorita Ruta favorita a crear con IDs de usuario y ruta válidos
     * @return ID único de la ruta favorita creada (generado por base de datos)
     * @throws SQLException Si hay error en la inserción
     * @throws IllegalArgumentException Si los datos son inválidos, las referencias no existen o ya existe la combinación
     * @see #validateRutaFavorita(RutaFavorita)
     */
    public int createRutaFavorita(RutaFavorita rutaFavorita) throws SQLException {
        validateRutaFavorita(rutaFavorita);

        // Verificar que no exista ya la combinación usuario-ruta
        if (rutaFavoritaRepository.existsByUsuarioAndRuta(rutaFavorita.getIdUsuario(), rutaFavorita.getIdRuta())) {
            throw new IllegalArgumentException("La ruta ya está en favoritos");
        }

        return rutaFavoritaRepository.save(rutaFavorita);
    }

    /**
     * Elimina una relación de ruta favorita por usuario y ruta.
     *
     * <p>Remueve una ruta de la lista de favoritas de un usuario específico
     * usando los identificadores de usuario y ruta. Operación segura que
     * no afecta otros datos del sistema.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * boolean eliminada = rutaFavoritaService.deleteRutaFavorita(10, 5);
     * if (eliminada) {
     *     System.out.println("Ruta removida de favoritos exitosamente");
     * }
     * </pre>
     *
     * @param usuarioId ID del usuario propietario de la lista de favoritos
     * @param rutaId ID de la ruta a remover de favoritos
     * @return true si la eliminación fue exitosa, false si no se encontró la relación
     * @throws SQLException Si hay error en la operación de base de datos
     */
    public boolean deleteRutaFavorita(int usuarioId, int rutaId) throws SQLException {
        return rutaFavoritaRepository.deleteByUsuarioAndRuta(usuarioId, rutaId);
    }

    /**
     * Verifica si existe una relación de favorita para usuario y ruta específicos.
     *
     * <p>Método utilitario para verificar si un usuario ya tiene una ruta
     * marcada como favorita. Útil para validaciones en interfaces de usuario
     * y prevención de operaciones duplicadas.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     * if (rutaFavoritaService.existeFavorita(10, 5)) {
     *     System.out.println("El usuario ya tiene esta ruta como favorita");
     * } else {
     *     // Mostrar opción para agregar a favoritos
     * }
     * </pre>
     *
     * @param usuarioId ID del usuario a verificar
     * @param rutaId ID de la ruta a verificar
     * @return true si ya existe la relación favorita, false en caso contrario
     * @throws SQLException Si hay error en la consulta de verificación
     */
    public boolean existeFavorita(int usuarioId, int rutaId) throws SQLException {
        return rutaFavoritaRepository.existsByUsuarioAndRuta(usuarioId, rutaId);
    }

    /**
     * Valida los datos básicos y la integridad referencial de una ruta favorita.
     * Método privado que aplica reglas de negocio y validaciones del sistema.
     *
     * <p>Validaciones incluidas:</p>
     * <ul>
     * <li>Existencia del usuario referenciado en el sistema</li>
     * <li>Existencia de la ruta referenciada en el sistema</li>
     * <li>Integridad referencial completa</li>
     * <li>Consistencia de datos requeridos</li>
     * </ul>
     *
     * @param rutaFavorita Ruta favorita a validar
     * @throws SQLException Si hay error en las consultas de validación
     * @throws IllegalArgumentException Si el usuario o la ruta no existen
     */
    private void validateRutaFavorita(RutaFavorita rutaFavorita) throws SQLException {
        if (usuarioRepository.findById(rutaFavorita.getIdUsuario()) == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (rutaRepository.findById(rutaFavorita.getIdRuta()) == null) {
            throw new IllegalArgumentException("Ruta no encontrada");
        }
    }
}