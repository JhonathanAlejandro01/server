package com.NetMasters.NetMasters.presentation.dto;

import lombok.Data;

@Data
public class MatchStateDTO {
    private Long winnerId;
    private String[] board;
    private String status;
}
