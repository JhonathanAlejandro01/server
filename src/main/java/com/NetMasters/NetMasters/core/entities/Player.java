package com.NetMasters.NetMasters.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private String password;
    private int gamesPlayed;
    private int gamesWinner;
    private int gamesTied;
    private boolean onlineState;
}
