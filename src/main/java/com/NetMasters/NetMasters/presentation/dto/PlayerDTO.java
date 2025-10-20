package com.NetMasters.NetMasters.presentation.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlayerDTO {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private int gamesPlayed;
    private int gamesWinner;
    private int gamesTied;
    private boolean onlineState;
}
