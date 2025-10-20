package com.NetMasters.NetMasters.core.entities;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConnectFourMove {
    private Long id;
    private Long gameId;
    private Long playerId;
    private int moveNumber;
    private int columnPlayed;
    private int rowPlayed;
    private char color;
    private LocalDateTime moveTime;
}
