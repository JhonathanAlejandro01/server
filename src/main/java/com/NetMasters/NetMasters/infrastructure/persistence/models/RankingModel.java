package com.NetMasters.NetMasters.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rankings")
public class RankingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerModel player;

    @Column(name = "total_score", nullable = false)
    private int totalScore;

    @Column(name = "player_rank")
    private Integer playerRank;
}
