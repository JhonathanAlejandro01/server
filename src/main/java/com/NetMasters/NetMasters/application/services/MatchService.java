package com.NetMasters.NetMasters.application.services;

import com.NetMasters.NetMasters.core.entities.Match;
import com.NetMasters.NetMasters.core.events.GameStartedEvent;
import com.NetMasters.NetMasters.core.events.MatchCreatedEvent;
import com.NetMasters.NetMasters.core.interfaces.EventBus;
import com.NetMasters.NetMasters.core.interfaces.MatchServiceInterface;
import com.NetMasters.NetMasters.core.interfaces.RankingServiceInterface;
import com.NetMasters.NetMasters.core.interfaces.LogServiceInterface;
import com.NetMasters.NetMasters.infrastructure.persistence.models.MatchModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.GameModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.MatchRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.PlayerRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.GameRepository;
import com.NetMasters.NetMasters.presentation.mappers.MatchMapper;
import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import com.NetMasters.NetMasters.core.entities.MatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
public class MatchService implements MatchServiceInterface {

    private final EventBus eventBus;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final MatchMapper matchMapper;
    private final RankingServiceInterface rankingService;
    private final LogServiceInterface logService;
    // in-memory map for match symmetric keys (base64). Replace with secure store in prod.
    private final java.util.concurrent.ConcurrentMap<Long, String> matchKeys = new java.util.concurrent.ConcurrentHashMap<>();

    @Autowired
    public MatchService(EventBus eventBus,
                        MatchRepository matchRepository,
                        PlayerRepository playerRepository,
                        GameRepository gameRepository,
                        MatchMapper matchMapper,
                        RankingServiceInterface rankingService,
                        LogServiceInterface logService) {
        this.eventBus = eventBus;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.matchMapper = matchMapper;
        this.rankingService = rankingService;
        this.logService = logService;
    }

    @Transactional
    @Override
    public Long createMatch(Long gameTypeId, Long player1Id, Long player2Id) {
        PlayerModel player1 = playerRepository.findById(player1Id)
                .orElseThrow(() -> new RuntimeException("Player 1 not found: " + player1Id));

        PlayerModel player2 = null;
        if (player2Id != null) {
            player2 = playerRepository.findById(player2Id)
                    .orElseThrow(() -> new RuntimeException("Player 2 not found: " + player2Id));
        }

        GameModel game = gameRepository.findById(gameTypeId)
                .orElseThrow(() -> new RuntimeException("Game not found: " + gameTypeId));

        MatchModel matchModel = new MatchModel();
        matchModel.setGame(game);
        matchModel.setPlayer1(player1);
        matchModel.setPlayer2(player2);
        matchModel.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        matchModel.setStatus(MatchStatus.PENDING);

        MatchModel saved = matchRepository.save(matchModel);

        // generar key simétrica para la partida (AES-256) y guardarla en memoria (base64)
        try {
            java.security.SecureRandom random = new java.security.SecureRandom();
            byte[] key = new byte[32]; // 256 bits
            random.nextBytes(key);
            String keyBase64 = java.util.Base64.getEncoder().encodeToString(key);
            matchKeys.put(saved.getId(), keyBase64);
        } catch (Exception ignored) {}

        // Publicar evento de partida creada
        eventBus.publish(new MatchCreatedEvent(
                this,
                new MatchId(saved.getId()),
                new PlayerId(player1Id),
                player2Id != null ? new PlayerId(player2Id) : null,
                game.getName() != null ? game.getName().name() : "UNKNOWN"
        ));

        return saved.getId();
    }

    // Obtener key base64 para una partida
    public Optional<String> getKeyForMatch(Long matchId) {
        return Optional.ofNullable(matchKeys.get(matchId));
    }

    // Guardar board JSON en la match
    @Transactional
    public boolean saveBoardForMatch(Long matchId, String boardJson) {
        return matchRepository.findById(matchId).map(mm -> {
            mm.setBoard(boardJson);
            matchRepository.save(mm);
            return true;
        }).orElse(false);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Object> getMatchById(Long matchId) {
        return matchRepository.findById(matchId).map(matchMapper::matchModelToMatch).map(m -> (Object)m);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Object> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(matchMapper::matchModelToMatch)
                .map(m -> (Object)m)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Object> getMatchesByStatus(String status) {
        MatchStatus st;
        try {
            st = MatchStatus.valueOf(status);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid match status: " + status);
        }
        return matchRepository.findAll().stream()
                .filter(mm -> mm.getStatus() == st)
                .map(matchMapper::matchModelToMatch)
                .map(m -> (Object)m)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Object> getMatchesByPlayer(Long playerId) {
        return matchRepository.findAll().stream()
                .filter(mm -> (mm.getPlayer1() != null && mm.getPlayer1().getId().equals(playerId)) ||
                        (mm.getPlayer2() != null && mm.getPlayer2().getId().equals(playerId)))
                .map(matchMapper::matchModelToMatch)
                .map(m -> (Object)m)
                .toList();
    }

    @Transactional
    @Override
    public boolean updateMatchStatus(Long matchId, String status) {
        return matchRepository.findById(matchId).map(mm -> {
            mm.setStatus(MatchStatus.valueOf(status));
            matchRepository.save(mm);
            // Si se empieza el juego, publicar GameStartedEvent
            if (mm.getStatus() == MatchStatus.IN_PROGRESS) {
                eventBus.publish(new GameStartedEvent(
                        this,
                        new MatchId(mm.getId()),
                        new PlayerId(mm.getPlayer1().getId()),
                        mm.getPlayer2() != null ? new PlayerId(mm.getPlayer2().getId()) : null
                ));
            }
            return true;
        }).orElse(false);
    }

    @Transactional
    @Override
    public boolean finishMatch(Long matchId, Long winnerId) {
        return matchRepository.findById(matchId).map(mm -> {
            mm.setStatus(MatchStatus.FINISHED);
            mm.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
            matchRepository.save(mm);

            // Actualizar ranking si hay servicio
            try {
                rankingService.calculateRankingsFromMatch(matchId);
            } catch (Exception ignored) {}

            // Registrar log
            try {
                long duration = 0L;
                if (mm.getStartTime() != null && mm.getEndTime() != null) {
                    duration = mm.getEndTime().getTime() - mm.getStartTime().getTime();
                }
                logService.logGameEnd(matchId, winnerId, duration);
            } catch (Exception ignored) {}

            return true;
        }).orElse(false);
    }

    @Transactional
    @Override
    public boolean deleteMatch(Long matchId) {
        if (!matchRepository.existsById(matchId)) return false;
        matchRepository.deleteById(matchId);
        return true;
    }

    @Transactional
    @Override
    public Optional<com.NetMasters.NetMasters.core.entities.Match> assignPlayerToMatch(Long matchId, Long player2Id) {
        Optional<Object> opt = getMatchById(matchId);
        if (opt.isEmpty() || !(opt.get() instanceof com.NetMasters.NetMasters.core.entities.Match)) {
            return Optional.empty();
        }
        com.NetMasters.NetMasters.core.entities.Match match = (com.NetMasters.NetMasters.core.entities.Match) opt.get();
        if (match.getPlayer2Id() != null) {
            return Optional.of(match);
        }

        // buscar el MatchModel para persistir player2
        Optional<MatchModel> mmOpt = matchRepository.findById(matchId);
        if (mmOpt.isEmpty()) return Optional.empty();
        MatchModel mm = mmOpt.get();

        PlayerModel player2 = playerRepository.findById(player2Id).orElse(null);
        if (Objects.isNull(player2)) return Optional.empty();

        mm.setPlayer2(player2);
        mm.setStatus(com.NetMasters.NetMasters.core.entities.MatchStatus.IN_PROGRESS);
        MatchModel saved = matchRepository.save(mm);

        // Mapear a entidad y publicar evento de inicio
        com.NetMasters.NetMasters.core.entities.Match updated = matchMapper.matchModelToMatch(saved);
        eventBus.publish(new GameStartedEvent(
                this,
                new MatchId(saved.getId()),
                new PlayerId(saved.getPlayer1().getId()),
                saved.getPlayer2() != null ? new PlayerId(saved.getPlayer2().getId()) : null
        ));

        return Optional.of(updated);
    }
}