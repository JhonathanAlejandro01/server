package com.NetMasters.NetMasters.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMatchDTO {
    @NotNull
    private Long gameId;
    @NotNull
    private Long player1Id;
    private Long player2Id;
}
