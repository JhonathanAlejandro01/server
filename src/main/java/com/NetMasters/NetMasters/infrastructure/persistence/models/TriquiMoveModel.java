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
@Table(name = "triqui_moves")
public class TriquiMoveModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "triqui_game_id", nullable = false)
    private TriquiGameModel triquiGame;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerModel player;

    @Column(name = "move_number", nullable = false)
    private int moveNumber;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private char symbol;

    @Column(name = "move_time", insertable = false, updatable = false)
    private Timestamp moveTime;
}
