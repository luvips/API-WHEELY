package com.wheely.model;

import com.wheely.service.UsuarioService;
import com.wheely.repository.UsuarioRepository;




/**
 * Entidad que representa un usuario del sistema Wheely de transporte público.
 *
 * <p>Esta clase modela a los ciudadanos de Tuxtla Gutiérrez, Chiapas que se registran
 * en la plataforma para reportar incidencias, sugerencias y problemas relacionados
 * con las rutas de transporte público urbano. Los usuarios autenticados pueden crear
 * reportes sobre retrasos, averías, mal servicio o propuestas de mejora.</p>
 *
 * <p>Mapeo de base de datos MySQL:</p>
 * <ul>
 *   <li><strong>Tabla:</strong> Usuario</li>
 *   <li><strong>Clave primaria:</strong> idUsuario (AUTO_INCREMENT)</li>
 *   <li><strong>Campos únicos:</strong> email (UNIQUE constraint)</li>
 *   <li><strong>Relaciones:</strong> One-to-Many con Reporte y RutaFavorita</li>
 * </ul>
 *
 * <p>Casos de uso típicos en el sistema de transporte:</p>
 * <ul>
 *   <li>Registro de nuevos usuarios en la plataforma Wheely</li>
 *   <li>Autenticación para acceso al sistema de reportes</li>
 *   <li>Vinculación con reportes de incidencias en rutas</li>
 *   <li>Gestión de rutas favoritas del usuario</li>
 * </ul>
 *
 * @author Beebop
 * @version 1.0.0
 * @since 2025
 * @see Reporte
 * @see RutaFavorita
 * @see UsuarioService
 * @see UsuarioRepository
 */
public class Usuario {

    /**
     * Identificador único del usuario en el sistema Wheely.
     *
     * <p>Campo auto-generado por MySQL mediante AUTO_INCREMENT.
     * Es null para usuarios nuevos que aún no han sido persistidos
     * en la base de datos. Una vez guardado, recibe un valor único
     * que identifica al usuario en todo el sistema de transporte.</p>
     */
    private int idUsuario;

    /**
     * Nombre completo del usuario registrado en Wheely.
     *
     * <p>Campo obligatorio que almacena el nombre real del ciudadano
     * que reporta incidencias en el transporte público. Máximo 100 caracteres
     * según esquema MySQL. Se utiliza para identificar al usuario en reportes.</p>
     */
    private String nombre;

    /**
     * Email único del usuario para autenticación en el sistema.
     *
     * <p>Funciona como username para login en la plataforma Wheely.
     * Debe ser único en toda la plataforma (constraint UNIQUE en MySQL).
     * Máximo 150 caracteres. Se valida formato antes de persistir.</p>
     */
    private String email;

    /**
     * Contraseña del usuario hasheada con BCrypt.
     *
     * <p>Se almacena hasheada por seguridad. Nunca se retorna en las
     * respuestas de la API. Utilizada para autenticación en el sistema
     * de reportes de transporte público.</p>
     */
    private String password;

    /**
     * Constructor por defecto para mapeo desde base de datos.
     *
     * <p>Utilizado principalmente por Jackson para deserialización JSON
     * y por repositorios para crear instancias vacías que serán populadas
     * con datos de MySQL.</p>
     */
    public Usuario() {
    }

    /**
     * Constructor completo con todos los campos del usuario.
     *
     * <p>Usado principalmente para crear instancias desde resultados
     * de base de datos donde ya se conoce el ID asignado.</p>
     *
     * @param idUsuario Identificador único asignado por la base de datos
     * @param nombre Nombre completo del usuario
     * @param email Email único para autenticación
     * @param password Contraseña hasheada
     */
    public Usuario(int idUsuario, String nombre, String email, String password) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructor para registro de nuevo usuario en Wheely.
     *
     * <p>Crea un usuario listo para ser registrado en el sistema de transporte.
     * El ID será asignado automáticamente al persistir en base de datos.</p>
     *
     * <p>Ejemplo de registro de usuario:</p>
     * <pre>
     * Usuario nuevoUsuario = new Usuario("María González", "maria@gmail.com", "password123");
     * int idGenerado = usuarioService.createUsuario(nuevoUsuario);
     * // El usuario podrá reportar incidencias en rutas de Tuxtla Gutiérrez
     * </pre>
     *
     * @param nombre Nombre completo del ciudadano (máximo 100 caracteres)
     * @param email Email único para autenticación (máximo 150 caracteres)
     * @param password Contraseña en texto plano (será hasheada con BCrypt)
     */
    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    // Getters y Setters con documentación...

    /**
     * Obtiene el identificador único del usuario.
     *
     * @return ID del usuario, 0 si no ha sido persistido aún
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario.
     *
     * <p>Normalmente asignado por la base de datos, no debe ser
     * modificado manualmente excepto en casos específicos de testing.</p>
     *
     * @param idUsuario ID único del usuario
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el nombre completo del usuario.
     *
     * @return Nombre del usuario registrado en Wheely
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre Nombre completo del usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el email del usuario.
     *
     * @return Email usado para autenticación
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del usuario.
     *
     * @param email Email único del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña hasheada del usuario.
     *
     * @return Contraseña hasheada con BCrypt
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param password Contraseña (será hasheada antes de guardar)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Representación en cadena del usuario ocultando la contraseña.
     *
     * <p>Útil para debugging y logging sin exponer información sensible.</p>
     *
     * @return Cadena con información del usuario sin contraseña
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}