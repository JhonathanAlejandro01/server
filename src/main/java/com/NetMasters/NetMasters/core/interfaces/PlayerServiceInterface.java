package com.NetMasters.NetMasters.core.interfaces;

import com.NetMasters.NetMasters.core.entities.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerServiceInterface extends UserService {
    /**
     * Crea un nuevo player.
     * @param player Player a crear.
     * @return Player creado.
     */
    Player createPlayer(Player player);

    /**
     * Actualiza un player existente.
     * @param id ID del player.
     * @param player Datos actualizados.
     * @return Optional del Player actualizado.
     */
    Optional<Player> updatePlayer(Long id, Player player);

    /**
     * Elimina un player.
     * @param id ID del player.
     * @return true si se eliminó, false otherwise.
     */
    boolean deletePlayer(Long id);

    /**
     * Obtiene todos los players.
     * @return Lista de players.
     */
    List<Player> getAllPlayers();

    /**
     * Obtiene un player por ID.
     * @param id ID del player.
     * @return Optional del Player.
     */
    Optional<Player> getPlayerById(Long id);

    // Métodos heredados de UserService:
    // - Player register(String username, String email, String password)
    // - Optional<Player> authenticate(String email, String password)
    // - void logoutByEmail(String email)
    // - void logoutByUsername(String username)
    // - List<Player> getOnlineUsers()
    // - Optional<Player> getPlayerById(Long playerId)
    // - Optional<Player> getPlayerByEmail(String email)
    // - Optional<Player> updatePlayer(Long playerId, Player updatedPlayer)
    // - boolean deletePlayer(Long playerId)
    // - List<Player> getAllPlayers()
}