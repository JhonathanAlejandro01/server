package com.NetMasters.NetMasters.presentation.dto;

public class GameStartedDTO {
    private Long matchId;
    private String message;

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
