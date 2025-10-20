package com.NetMasters.NetMasters.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MoveDTO {
    @NotNull
    private Long playerId; // este campo se debe eliminar cuando se implemente el token (sacar el id del token)
    @NotNull
    private Integer position;
}
