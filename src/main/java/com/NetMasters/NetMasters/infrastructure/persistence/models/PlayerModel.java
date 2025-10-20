package com.NetMasters.NetMasters.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "players")
public class PlayerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "games_played", nullable = false)
    private int gamesPlayed;

    @Column(name = "games_winner", nullable = false)
    private int gamesWinner;

    @Column(name = "games_tied", nullable = false)
    private int gamesTied;

    @Column(name = "online_state", nullable = false)
    private boolean onlineState;
}
