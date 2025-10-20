package com.NetMasters.NetMasters.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriquiGame {
    private Long id;
    private Long matchId;
    private String board;
    private Long currentTurn;
    private Long winner;
}
