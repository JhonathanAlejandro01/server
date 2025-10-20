package com.NetMasters.NetMasters.core.interfaces;

import java.time.Instant;
import java.util.List;

public interface LogServiceInterface {
    /**
     * Registra un evento en el sistema de logs.
     * @param event Tipo de evento.
     * @param message Mensaje descriptivo.
     * @param playerId ID del jugador relacionado (opcional).
     */
    void logEvent(String event, String message, Long playerId);

    /**
     * Registra un evento de login.
     * @param playerId ID del jugador.
     * @param ipAddress Dirección IP (opcional).
     */
    void logLogin(Long playerId, String ipAddress);

    /**
     * Registra un evento de logout.
     * @param playerId ID del jugador.
     */
    void logLogout(Long playerId);

    /**
     * Registra un movimiento en una partida.
     * @param matchId ID de la partida.
     * @param playerId ID del jugador.
     * @param moveData Datos del movimiento.
     */
    void logGameMove(Long matchId, Long playerId, String moveData);

    /**
     * Registra el fin de una partida.
     * @param matchId ID de la partida.
     * @param winnerId ID del ganador (null para empate).
     * @param duration Duración en segundos.
     */
    void logGameEnd(Long matchId, Long winnerId, long duration);

    /**
     * Obtiene logs por tipo de evento.
     * @param event Tipo de evento.
     * @return Lista de logs.
     */
    List<Object> getLogsByEvent(String event);

    /**
     * Obtiene logs en un rango de tiempo.
     * @param start Inicio del período.
     * @param end Fin del período.
     * @return Lista de logs.
     */
    List<Object> getLogsByTimeRange(Instant start, Instant end);

    /**
     * Obtiene logs de un jugador específico.
     * @param playerId ID del jugador.
     * @return Lista de logs del jugador.
     */
    List<Object> getLogsByPlayer(Long playerId);

    /**
     * Obtiene logs de una partida específica.
     * @param matchId ID de la partida.
     * @return Lista de logs de la partida.
     */
    List<Object> getLogsByMatch(Long matchId);

    /**
     * Limpia logs antiguos.
     * @param before Fecha límite.
     * @return Número de logs eliminados.
     */
    int cleanupOldLogs(Instant before);
}