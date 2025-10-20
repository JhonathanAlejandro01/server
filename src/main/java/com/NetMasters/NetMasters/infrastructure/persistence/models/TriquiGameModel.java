package com.NetMasters.NetMasters.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "triqui_games")
public class TriquiGameModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "match_id", nullable = false)
    private MatchModel match;

    @Column(nullable = false, length = 9)
    private String board;

    @ManyToOne
    @JoinColumn(name = "current_turn", nullable = false)
    private PlayerModel currentTurn;

    @ManyToOne
    @JoinColumn(name = "winner")
    private PlayerModel winner;
}
