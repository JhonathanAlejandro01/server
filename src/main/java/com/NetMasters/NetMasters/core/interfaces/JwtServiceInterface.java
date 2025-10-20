package com.NetMasters.NetMasters.core.interfaces;

public interface JwtServiceInterface {
    /**
     * Genera un token JWT para un usuario.
     * @param username Nombre de usuario.
     * @param userId ID del usuario.
     * @return Token JWT.
     */
    String generateToken(String username, Long userId);

    /**
     * Extrae el username del token.
     * @param token JWT token.
     * @return Username.
     */
    String extractUsername(String token);

    /**
     * Extrae el userId del token.
     * @param token JWT token.
     * @return UserId.
     */
    Long extractUserId(String token);

    /**
     * Valida si un token es válido para un usuario.
     * @param token JWT token.
     * @param username Username a validar.
     * @return true si válido.
     */
    boolean validateToken(String token, String username);

    /**
     * Verifica si un token ha expirado.
     * @param token JWT token.
     * @return true si expirado.
     */
    boolean isTokenExpired(String token);
}