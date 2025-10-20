package com.NetMasters.NetMasters.presentation.dto;

import com.NetMasters.NetMasters.core.entities.GameName;
import lombok.Data;
import java.time.LocalDate;

@Data
public class GameDTO {
    private Long id;
    private GameName name;
    private String genre;
    private LocalDate releaseDate;
}
