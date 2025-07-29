package com.wheely.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * <p>
 * Utilidad para el manejo seguro de contraseñas en el sistema WHEELY.
 * Proporciona métodos para el hash, verificación y validación de contraseñas de usuarios,
 * asegurando el cumplimiento de los requisitos de seguridad exigidos en la gestión de transporte público de Tuxtla Gutiérrez, Chiapas.
 * </p>
 * <p>
 * <b>Propósito en WHEELY:</b>
 * <ul>
 *   <li>Garantizar el almacenamiento seguro de contraseñas mediante BCrypt.</li>
 *   <li>Validar que las contraseñas cumplan criterios de robustez para proteger cuentas de usuarios y administradores.</li>
 *   <li>Facilitar la retroalimentación sobre requisitos faltantes en contraseñas durante el registro y cambio de clave.</li>
 * </ul>
 * <p>
 * <b>Ejemplo de uso:</b>
 * <pre>
 * // Registrar usuario
 * if (PasswordUtil.isValidPassword(usuario.getPassword())) {
 *     String hash = PasswordUtil.hashPassword(usuario.getPassword());
 *     usuario.setPasswordHash(hash);
 * }
 * // Verificar login
 * boolean acceso = PasswordUtil.verifyPassword(inputPassword, usuario.getPasswordHash());
 * </pre>
 * </p>
 *
 * @author [Tu Nombre]
 * @version 1.0.0
 * @since 2025
 * @see com.wheely.model.Usuario
 * @see #isValidPassword(String)
 * @see #getPasswordValidationErrors(String)
 */
public class PasswordUtil {

    /**
     * <p>
     * Nivel de complejidad para el hashing BCrypt.
     * <ul>
     *   <li>12: Muy seguro pero más lento</li>
     *   <li>10: Balance recomendado para sistemas web</li>
     * </ul>
     * </p>
     */
    private static final int SALT_ROUNDS = 10;

    /**
     * <p>
     * Genera un hash seguro de una contraseña en texto plano usando BCrypt.
     * <ul>
     *   <li>Utilizado al registrar o actualizar contraseñas de usuarios.</li>
     *   <li>El hash generado se almacena en la base de datos, nunca la contraseña en texto plano.</li>
     * </ul>
     * </p>
     * <pre>
     * String hash = PasswordUtil.hashPassword("MiClaveSegura123!");
     * </pre>
     *
     * @param plainPassword {@code String} Contraseña en texto plano a hashear.
     * @return {@code String} Hash seguro de la contraseña.
     * @throws IllegalArgumentException Si la contraseña es nula o vacía.
     * @see #verifyPassword(String, String)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    /**
     * <p>
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     * <ul>
     *   <li>Utilizado en el proceso de autenticación de usuarios.</li>
     *   <li>Compara la contraseña ingresada con el hash guardado en la base de datos.</li>
     * </ul>
     * </p>
     * <pre>
     * boolean valido = PasswordUtil.verifyPassword("MiClaveSegura123!", usuario.getPasswordHash());
     * </pre>
     *
     * @param plainPassword {@code String} Contraseña en texto plano a verificar.
     * @param hashedPassword {@code String} Hash almacenado en la base de datos.
     * @return {@code boolean} {@code true} si la contraseña coincide, {@code false} en caso contrario.
     * @throws IllegalArgumentException Si alguno de los parámetros es nulo o vacío.
     * @see #hashPassword(String)
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null ||
                plainPassword.trim().isEmpty() || hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Contraseña y hash no pueden estar vacíos");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * <p>
     * Valida si una contraseña cumple con los requisitos de seguridad definidos por WHEELY.
     * <ul>
     *   <li>Mínimo 8 caracteres</li>
     *   <li>Al menos 1 letra mayúscula</li>
     *   <li>Al menos 1 letra minúscula</li>
     *   <li>Al menos 1 número</li>
     *   <li>Al menos 1 símbolo (#%$/"!?¿¡\)</li>
     * </ul>
     * </p>
     * <pre>
     * boolean esValida = PasswordUtil.isValidPassword("Clave#2025");
     * </pre>
     *
     * @param password {@code String} Contraseña a validar.
     * @return {@code boolean} {@code true} si la contraseña es válida, {@code false} en caso contrario.
     * @see #getPasswordValidationErrors(String)
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            return false;
        }
        if (!password.matches(".*[#%$\"/\"!?¿¡\\\\].*")) {
            return false;
        }
        return true;
    }

    /**
     * <p>
     * Obtiene los requisitos de contraseña como texto para mostrar al usuario.
     * <ul>
     *   <li>Útil en formularios de registro y cambio de contraseña.</li>
     * </ul>
     * </p>
     * <pre>
     * String requisitos = PasswordUtil.getPasswordRequirements();
     * </pre>
     *
     * @return {@code String} Texto con los requisitos de contraseña.
     */
    public static String getPasswordRequirements() {
        return "La contraseña debe tener:\n" +
                "- Mínimo 8 caracteres\n" +
                "- Al menos 1 letra mayúscula\n" +
                "- Al menos 1 letra minúscula\n" +
                "- Al menos 1 número\n" +
                "- Al menos 1 símbolo (#%$/\"!?¿¡\\)";
    }

    /**
     * <p>
     * Valida una contraseña y retorna detalles específicos de los requisitos que faltan.
     * <ul>
     *   <li>Permite retroalimentación granular al usuario en el frontend.</li>
     *   <li>Retorna un array vacío si la contraseña es válida.</li>
     * </ul>
     * </p>
     * <pre>
     * String[] errores = PasswordUtil.getPasswordValidationErrors("clave");
     * if (errores.length > 0) {
     *     // Mostrar errores al usuario
     * }
     * </pre>
     *
     * @param password {@code String} Contraseña a validar.
     * @return {@code String[]} Array de requisitos faltantes, vacío si es válida.
     * @see #isValidPassword(String)
     */
    public static String[] getPasswordValidationErrors(String password) {
        if (password == null) {
            return new String[]{"La contraseña no puede ser nula"};
        }
        java.util.List<String> errors = new java.util.ArrayList<>();
        if (password.length() < 8) {
            errors.add("Debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*[A-Z].*")) {
            errors.add("Debe tener al menos 1 letra mayúscula");
        }
        if (!password.matches(".*[a-z].*")) {
            errors.add("Debe tener al menos 1 letra minúscula");
        }
        if (!password.matches(".*[0-9].*")) {
            errors.add("Debe tener al menos 1 número");
        }
        if (!password.matches(".*[#%$\"/\"!?¿¡\\\\].*")) {
            errors.add("Debe tener al menos 1 símbolo (#%$/\"!?¿¡\\)");
        }
        return errors.toArray(new String[0]);
    }
}