package com.NetMasters.NetMasters.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "connect4_moves")
public class ConnectFourMoveModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "connect4_game_id", nullable = false)
    private ConnectFourGameModel connect4Game;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerModel player;

    @Column(name = "move_number", nullable = false)
    private int moveNumber;

    @Column(name = "column_played", nullable = false)
    private int columnPlayed;

    @Column(name = "row_played", nullable = false)
    private int rowPlayed;

    @Column(name = "color", nullable = false)
    private char color;

    @Column(name = "move_time", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime moveTime;
}
