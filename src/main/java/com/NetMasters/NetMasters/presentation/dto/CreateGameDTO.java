package com.NetMasters.NetMasters.presentation.dto;

import com.NetMasters.NetMasters.core.entities.GameName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateGameDTO {
    @NotNull(message = "Game name cannot be null")
    private GameName name;
    @NotEmpty(message = "Genre cannot be empty")
    private String genre;
    @NotEmpty
    @Size(min = 2, max = 2, message = "A triqui game must have exactly 2 players") //mejor añadir un join en ven de pasar los id de esta manera (solo es provicional)
    private List<Long> playerIds;
}
