package com.NetMasters.NetMasters.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "connect4_games")
public class ConnectFourGameModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "match_id", nullable = false)
    private MatchModel match;

    @Column(name = "row_connect4", nullable = false)
    private int rowConnect4;

    @Column(name = "column_connect4", nullable = false)
    private int columnConnect4;

    @Lob
    @Column(name = "board", nullable = false)
    private String board;

    @ManyToOne
    @JoinColumn(name = "current_turn", nullable = false)
    private PlayerModel currentTurn;

    @ManyToOne
    @JoinColumn(name = "winner")
    private PlayerModel winner;
}
