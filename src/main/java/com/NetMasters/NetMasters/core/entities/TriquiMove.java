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
public class TriquiMove {
    private Long id;
    private Long triquiGameId;
    private Long playerId;
    private int moveNumber;
    private int position;
    private char symbol;
    private LocalDateTime moveTime;
}
