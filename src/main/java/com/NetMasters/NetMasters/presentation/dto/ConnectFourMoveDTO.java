package com.NetMasters.NetMasters.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConnectFourMoveDTO {
    @NotNull
    private Long playerId;

    @NotNull
    private int column;
}
