package com.NetMasters.NetMasters.core.interfaces;

import com.NetMasters.NetMasters.core.entities.Player;

import java.util.List;
import java.util.Optional;

public interface UserService {
    /**
     * Registra un nuevo usuario.
     * @param username Nombre de usuario.
     * @param email Email.
     * @param password Contraseña (se hashea internamente).
     * @return El Player creado.
     */
    Player register(String username, String email, String password);

    /**
     * Autentica un usuario por email.
     * @param email Email del usuario.
     * @param password Contraseña.
     * @return Optional del Player si autenticado.
     */
    Optional<Player> authenticate(String email, String password);

    /**
     * Marca un usuario como desconectado por email.
     * @param email Email del usuario.
     */
    void logoutByEmail(String email);

    /**
     * Marca un usuario como desconectado por username.
     * @param username Username del usuario.
     */
    void logoutByUsername(String username);

    /**
     * Obtiene lista de usuarios en línea.
     * @return Lista de Players online.
     */
    List<Player> getOnlineUsers();

    /**
     * Obtiene un usuario por ID.
     * @param playerId ID del player.
     * @return Optional del Player.
     */
    Optional<Player> getPlayerById(Long playerId);

    /**
     * Obtiene un usuario por email.
     * @param email Email del usuario.
     * @return Optional del Player.
     */
    Optional<Player> getPlayerByEmail(String email);

    /**
     * Actualiza la información de un usuario.
     * @param playerId ID del player.
     * @param updatedPlayer Player con datos actualizados.
     * @return Optional del Player actualizado.
     */
    Optional<Player> updatePlayer(Long playerId, Player updatedPlayer);

    /**
     * Elimina un usuario.
     * @param playerId ID del player.
     * @return true si se eliminó, false otherwise.
     */
    boolean deletePlayer(Long playerId);

    /**
     * Obtiene todos los usuarios.
     * @return Lista de todos los Players.
     */
    List<Player> getAllPlayers();
}
