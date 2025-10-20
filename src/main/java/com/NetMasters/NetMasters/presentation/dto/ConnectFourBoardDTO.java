package com.NetMasters.NetMasters.presentation.dto;

import lombok.Data;

@Data
public class ConnectFourBoardDTO {
    private String board;
    private int rows;
    private int columns;
    private Long currentTurnPlayerId;
    private Long winnerPlayerId;
}
