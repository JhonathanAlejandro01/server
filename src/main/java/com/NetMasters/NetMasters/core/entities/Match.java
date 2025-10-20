package com.NetMasters.NetMasters.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Long id;
    private Long gameId;
    private Long player1Id;
    private Long player2Id;
    private String board; // serialized board JSON (e.g. ["X",null,"O",...])
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MatchStatus status;
}
