package com.NetMasters.NetMasters.presentation.dto;

import lombok.Data;

@Data
public class BoardDTO {
    private String board;
    private Long currentTurn;
    private Long winner;
}
