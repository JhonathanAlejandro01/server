package com.NetMasters.NetMasters.core.interfaces;

import java.util.Optional;

public interface TriquiGameServiceInterface {
    /**
     * Crea un nuevo juego de Triqui (Tres en Raya).
     * @param matchId ID de la partida.
     * @return ID del juego creado.
     */
    Long createTriquiGame(Long matchId);

    /**
     * Obtiene un juego de Triqui por ID.
     * @param gameId ID del juego.
     * @return Optional del juego.
     */
    Optional<Object> getTriquiGameById(Long gameId);

    /**
     * Realiza un movimiento en el tablero.
     * @param gameId ID del juego.
     * @param playerId ID del jugador.
     * @param position Posición (0-8) donde colocar la marca.
     * @return true si el movimiento fue válido.
     */
    boolean makeMove(Long gameId, Long playerId, int position);

    /**
     * Obtiene el estado actual del tablero.
     * @param gameId ID del juego.
     * @return String representando el tablero (9 caracteres).
     */
    String getBoardState(Long gameId);

    /**
     * Verifica si hay un ganador.
     * @param gameId ID del juego.
     * @return Optional del ID del ganador, vacío si no hay ganador.
     */
    Optional<Long> checkWinner(Long gameId);

    /**
     * Verifica si el tablero está lleno (empate).
     * @param gameId ID del juego.
     * @return true si está lleno.
     */
    boolean isBoardFull(Long gameId);

    /**
     * Reinicia el juego.
     * @param gameId ID del juego.
     * @return true si se reinició correctamente.
     */
    boolean resetGame(Long gameId);

    /**
     * Obtiene el turno actual.
     * @param gameId ID del juego.
     * @return ID del jugador del turno actual.
     */
    Long getCurrentTurn(Long gameId);

    /**
     * Valida si una posición está disponible.
     * @param gameId ID del juego.
     * @param position Posición a verificar.
     * @return true si está disponible.
     */
    boolean isPositionAvailable(Long gameId, int position);
}