package com.NetMasters.NetMasters.application.services;

import com.NetMasters.NetMasters.core.events.RankingUpdatedEvent;
import com.NetMasters.NetMasters.core.interfaces.EventBus;
import com.NetMasters.NetMasters.core.interfaces.RankingServiceInterface;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import com.NetMasters.NetMasters.infrastructure.persistence.models.MatchModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.RankingModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.MatchRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.PlayerRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.RankingRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.TriquiGameRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.ConnectFourGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RankingService implements RankingServiceInterface {

    private final EventBus eventBus;
    private final RankingRepository rankingRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final TriquiGameRepository triquiGameRepository;
    private final ConnectFourGameRepository connectFourGameRepository;

    @Autowired
    public RankingService(EventBus eventBus, RankingRepository rankingRepository, PlayerRepository playerRepository, MatchRepository matchRepository,
                          TriquiGameRepository triquiGameRepository, ConnectFourGameRepository connectFourGameRepository) {
        this.eventBus = eventBus;
        this.rankingRepository = rankingRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.triquiGameRepository = triquiGameRepository;
        this.connectFourGameRepository = connectFourGameRepository;
    }
    @Override
    public List<Object> getGlobalRanking() {
        return rankingRepository.findAll().stream()
                .sorted((a,b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()))
                .map(r -> {
                    var map = new java.util.HashMap<String,Object>();
                    map.put("playerId", r.getPlayer().getId());
                    map.put("username", r.getPlayer().getUsername());
                    map.put("totalScore", r.getTotalScore());
                    map.put("rank", r.getPlayerRank());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Object> getPlayerRanking(Long playerId) {
        return rankingRepository.findAll().stream()
                .filter(r -> r.getPlayer().getId().equals(playerId))
                .findFirst()
                .map(r -> {
                    var map = new java.util.HashMap<String,Object>();
                    map.put("playerId", r.getPlayer().getId());
                    map.put("username", r.getPlayer().getUsername());
                    map.put("totalScore", r.getTotalScore());
                    map.put("rank", r.getPlayerRank());
                    return (Object) map;
                });
    }

    @Override
    public boolean updatePlayerScore(Long playerId, int points) {
        PlayerModel player = playerRepository.findById(playerId).orElse(null);
        if (player == null) return false;

        RankingModel ranking = rankingRepository.findAll().stream()
                .filter(r -> r.getPlayer().getId().equals(playerId))
                .findFirst().orElse(null);

        int previousScore = 0;
        if (ranking == null) {
            ranking = RankingModel.builder().player(player).totalScore(points).playerRank(null).build();
            ranking = rankingRepository.save(ranking);
            previousScore = 0;
        } else {
            previousScore = ranking.getTotalScore();
            ranking.setTotalScore(previousScore + points);
            rankingRepository.save(ranking);
        }

        int newScore = ranking.getTotalScore();

        eventBus.publish(new RankingUpdatedEvent(
                this,
                new PlayerId(playerId),
                "GENERAL",
                newScore,
                previousScore
        ));
        return true;
    }

    @Override
    public List<Object> getTopPlayers(int limit) {
        return rankingRepository.findAll().stream()
                .sorted((a,b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()))
                .limit(limit)
                .map(r -> {
                    var map = new java.util.HashMap<String,Object>();
                    map.put("playerId", r.getPlayer().getId());
                    map.put("username", r.getPlayer().getUsername());
                    map.put("totalScore", r.getTotalScore());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> getRankingByGameType(String gameType) {
        // No hay separación por juego en el modelo actual; devolver ranking global como fallback
        return getGlobalRanking();
    }

    @Override
    public boolean resetRankings() {
        rankingRepository.deleteAll();
        return true;
    }

    @Override
    public boolean calculateRankingsFromMatch(Long matchId) {
        MatchModel match = matchRepository.findById(matchId).orElse(null);
        if (match == null) return false;

        PlayerModel p1 = match.getPlayer1();
        PlayerModel p2 = match.getPlayer2();

        if (match.getStatus() == com.NetMasters.NetMasters.core.entities.MatchStatus.FINISHED) {
            // Determine winner by querying game repositories
            Long winnerId = null;
            var tOpt = triquiGameRepository.findByMatchId(matchId);
            if (tOpt.isPresent() && tOpt.get().getWinner() != null) winnerId = tOpt.get().getWinner().getId();
            var cOpt = connectFourGameRepository.findByMatchId(matchId);
            if (cOpt.isPresent() && cOpt.get().getWinner() != null) winnerId = cOpt.get().getWinner().getId();

            if (winnerId == null) {
                // draw
                if (p1 != null) updatePlayerScore(p1.getId(), 1);
                if (p2 != null) updatePlayerScore(p2.getId(), 1);
            } else {
                // winner +3, loser +0
                updatePlayerScore(winnerId, 3);
                if (p1 != null && !p1.getId().equals(winnerId)) updatePlayerScore(p1.getId(), 0);
                if (p2 != null && !p2.getId().equals(winnerId)) updatePlayerScore(p2.getId(), 0);
            }
            return true;
        }
        return false;
    }
}