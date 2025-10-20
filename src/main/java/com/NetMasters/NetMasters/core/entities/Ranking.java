package com.NetMasters.NetMasters.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ranking {
    private Long id;
    private Long playerId;
    private int totalScore;
    private Integer playerRank;
}
