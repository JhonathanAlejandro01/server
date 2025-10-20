package com.NetMasters.NetMasters.core.interfaces;

import java.util.List;
import java.util.Optional;

public interface MatchServiceInterface {
    /**
     * Crea una nueva partida.
     * @param gameTypeId ID del tipo de juego.
     * @param player1Id ID del primer jugador.
     * @param player2Id ID del segundo jugador (opcional).
     * @return ID de la partida creada.
     */
    Long createMatch(Long gameTypeId, Long player1Id, Long player2Id);

    /**
     * Obtiene una partida por ID.
     * @param matchId ID de la partida.
     * @return Optional de la partida.
     */
    Optional<Object> getMatchById(Long matchId);

    /**
     * Obtiene todas las partidas.
     * @return Lista de partidas.
     */
    List<Object> getAllMatches();

    /**
     * Obtiene partidas por estado.
     * @param status Estado de la partida.
     * @return Lista de partidas con ese estado.
     */
    List<Object> getMatchesByStatus(String status);

    /**
     * Obtiene partidas de un jugador.
     * @param playerId ID del jugador.
     * @return Lista de partidas del jugador.
     */
    List<Object> getMatchesByPlayer(Long playerId);

    /**
     * Actualiza el estado de una partida.
     * @param matchId ID de la partida.
     * @param status Nuevo estado.
     * @return true si se actualizó correctamente.
     */
    boolean updateMatchStatus(Long matchId, String status);

    /**
     * Finaliza una partida.
     * @param matchId ID de la partida.
     * @param winnerId ID del ganador (null para empate).
     * @return true si se finalizó correctamente.
     */
    boolean finishMatch(Long matchId, Long winnerId);

    /**
     * Elimina una partida.
     * @param matchId ID de la partida.
     * @return true si se eliminó correctamente.
     */
    boolean deleteMatch(Long matchId);

    /**
     * Asigna el segundo jugador (player2) a una partida existente y devuelve la partida actualizada.
     * @param matchId ID de la partida.
     * @param player2Id ID del jugador que se une.
     * @return Optional con la entidad Match actualizada o empty si no existe.
     */
    Optional<com.NetMasters.NetMasters.core.entities.Match> assignPlayerToMatch(Long matchId, Long player2Id);
}