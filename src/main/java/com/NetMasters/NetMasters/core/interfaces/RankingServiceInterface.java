package com.NetMasters.NetMasters.core.interfaces;

import java.util.List;
import java.util.Optional;

public interface RankingServiceInterface {
    /**
     * Obtiene el ranking general de jugadores.
     * @return Lista de rankings ordenados por puntaje.
     */
    List<Object> getGlobalRanking();

    /**
     * Obtiene el ranking de un jugador específico.
     * @param playerId ID del jugador.
     * @return Optional del ranking del jugador.
     */
    Optional<Object> getPlayerRanking(Long playerId);

    /**
     * Actualiza el puntaje de un jugador.
     * @param playerId ID del jugador.
     * @param points Puntos a agregar.
     * @return true si se actualizó correctamente.
     */
    boolean updatePlayerScore(Long playerId, int points);

    /**
     * Obtiene el top N de jugadores.
     * @param limit Número de jugadores a obtener.
     * @return Lista del top de jugadores.
     */
    List<Object> getTopPlayers(int limit);

    /**
     * Obtiene rankings por tipo de juego.
     * @param gameType Tipo de juego.
     * @return Lista de rankings para ese tipo de juego.
     */
    List<Object> getRankingByGameType(String gameType);

    /**
     * Reinicia los rankings (para nuevo período).
     * @return true si se reiniciaron correctamente.
     */
    boolean resetRankings();

    /**
     * Calcula y actualiza rankings basados en resultados de partidas.
     * @param matchId ID de la partida finalizada.
     * @return true si se actualizaron correctamente.
     */
    boolean calculateRankingsFromMatch(Long matchId);
}