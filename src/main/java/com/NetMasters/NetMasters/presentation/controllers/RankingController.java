package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.application.services.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public ResponseEntity<List<Object>> getGlobal() {
        return ResponseEntity.ok(rankingService.getGlobalRanking());
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<Object> getPlayer(@PathVariable Long playerId) {
        return rankingService.getPlayerRanking(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/player/{playerId}/add")
    public ResponseEntity<Void> addScore(@PathVariable Long playerId, @RequestParam int points) {
        boolean ok = rankingService.updatePlayerScore(playerId, points);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/top")
    public ResponseEntity<List<Object>> top(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(rankingService.getTopPlayers(limit));
    }

    @GetMapping("/game/{gameType}")
    public ResponseEntity<List<Object>> byGame(@PathVariable String gameType) {
        return ResponseEntity.ok(rankingService.getRankingByGameType(gameType));
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        rankingService.resetRankings();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate/{matchId}")
    public ResponseEntity<Void> calculateFromMatch(@PathVariable Long matchId) {
        boolean ok = rankingService.calculateRankingsFromMatch(matchId);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
