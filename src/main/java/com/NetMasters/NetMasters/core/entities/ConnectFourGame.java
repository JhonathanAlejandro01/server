package com.NetMasters.NetMasters.core.entities;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConnectFourGame {
    private Long id;
    private Long matchId;
    private int rowConnect4;
    private int columnConnect4;
    private String board; 
    private Long currentTurnPlayerId;
    private Long winnerPlayerId;
    private List<ConnectFourMove> moves;
}