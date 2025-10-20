package com.NetMasters.NetMasters.core.interfaces;

import com.NetMasters.NetMasters.core.entities.Match;
import com.NetMasters.NetMasters.core.valueobjects.Coordinates;

public interface GameService {
    /**
     * Inicia una nueva partida basada en el tipo de juego y el jugador inicial.
     * @param gameTypeId ID del tipo de juego (e.g., 1 para TicTacToe).
     * @param player1Id ID del jugador que inicia.
     * @return La entidad Match creada.
     */
    Match startGame(Long gameTypeId, Long player1Id);

    /**
     * Realiza un movimiento en una partida existente.
     * @param matchId ID de la partida.
     * @param playerId ID del jugador que mueve.
     * @param coords Coordenadas del movimiento.
     */
    void makeMove(Long matchId, Long playerId, Coordinates coords);

    /**
     * Abandona una partida, otorgando victoria al oponente.
     * @param matchId ID de la partida.
     * @param playerId ID del jugador que abandona.
     */
    void abandonGame(Long matchId, Long playerId);

    /**
     * Invita a un jugador a una partida.
     * @param matchId ID de la partida.
     * @param invitedPlayerId ID del jugador invitado.
     */
    void invitePlayer(Long matchId, Long invitedPlayerId);

    /**
     * Acepta o rechaza una invitación.
     * @param matchId ID de la partida.
     * @param playerId ID del jugador que responde.
     * @param accept True para aceptar, false para rechazar.
     */
    void respondToInvitation(Long matchId, Long playerId, boolean accept);
}
