package com.NetMasters.NetMasters.core.interfaces;

import java.util.Optional;

public interface AuthServiceInterface {
    /**
     * Registra un nuevo usuario y lo marca como online.
     * @param username Nombre de usuario.
     * @param email Email.
     * @param password Contraseña.
     * @return Object creado con token JWT.
     */
    Object register(String username, String email, String password);

    /**
     * Autentica un usuario y genera token JWT.
     * @param email Email.
     * @param password Contraseña.
     * @return Optional del usuario autenticado.
     */
    Optional<Object> login(String email, String password);

    /**
     * Cierra sesión del usuario por token.
     * @param token JWT token.
     */
    void logout(String token);

    /**
     * Valida un token JWT.
     * @param token Token a validar.
     * @return true si válido, false otherwise.
     */
    boolean validateToken(String token);

    /**
     * Extrae el userId de un token JWT.
     * @param token JWT token.
     * @return userId o null si inválido.
     */
    Long extractUserId(String token);

    /**
     * Extrae el username de un token JWT.
     * @param token JWT token.
     * @return username o null si inválido.
     */
    String extractUsername(String token);
}