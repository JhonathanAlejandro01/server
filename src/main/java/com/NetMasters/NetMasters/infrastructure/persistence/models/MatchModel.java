package com.NetMasters.NetMasters.infrastructure.persistence.models;

import com.NetMasters.NetMasters.core.entities.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matches")
public class MatchModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private GameModel game;

    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private PlayerModel player1;

    @ManyToOne
    @JoinColumn(name = "player2_id")
    private PlayerModel player2;

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;
}
