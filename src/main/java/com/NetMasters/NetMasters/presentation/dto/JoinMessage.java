package com.NetMasters.NetMasters.presentation.dto;

public class JoinMessage {
    private Long matchId;
    private Long playerId;
    private String game;

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }
}
