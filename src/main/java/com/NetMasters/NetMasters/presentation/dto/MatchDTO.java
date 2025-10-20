package com.NetMasters.NetMasters.presentation.dto;

import com.NetMasters.NetMasters.core.entities.MatchStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchDTO {
    private Long id;
    private Long gameId;
    private Long player1Id;
    private Long player2Id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MatchStatus status;
}
