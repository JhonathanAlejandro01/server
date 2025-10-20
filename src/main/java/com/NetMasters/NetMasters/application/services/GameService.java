package com.NetMasters.NetMasters.application.services;

import com.NetMasters.NetMasters.core.entities.Game;
import com.NetMasters.NetMasters.core.entities.GameName;
import com.NetMasters.NetMasters.core.entities.MatchStatus;
import com.NetMasters.NetMasters.infrastructure.persistence.models.*;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.*;
import com.NetMasters.NetMasters.presentation.mappers.GameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final MatchRepository matchRepository;
    private final TriquiGameRepository triquiGameRepository;
    private final ConnectFourGameRepository connectFourGameRepository;
    private final PlayerRepository playerRepository;
    private final GameMapper gameMapper;

    @Autowired
    public GameService(GameRepository gameRepository, 
            MatchRepository matchRepository, TriquiGameRepository triquiGameRepository, 
            PlayerRepository playerRepository, 
            GameMapper gameMapper, ConnectFourGameRepository connectFourGameRepository) {
        this.gameRepository = gameRepository;
        this.matchRepository = matchRepository;
        this.triquiGameRepository = triquiGameRepository;
        this.connectFourGameRepository = connectFourGameRepository;
        this.playerRepository = playerRepository;
        this.gameMapper = gameMapper;
    }

    @Transactional
    public Game createGame(Game game, List<Long> playerIds) {
        game.setReleaseDate(java.time.LocalDate.now());
        GameModel gameModel = gameMapper.gameToGameModel(game);
        GameModel savedGameModel = gameRepository.save(gameModel);

        if (playerIds == null || playerIds.isEmpty()) {
            throw new RuntimeException("At least one player must be provided to create a game.");
        }

        PlayerModel player1 = playerRepository.findById(playerIds.get(0))
                .orElseThrow(() -> new RuntimeException("Player 1 not found with id: " + playerIds.get(0)));

        PlayerModel player2 = null;
        if (playerIds.size() > 1) {
            player2 = playerRepository.findById(playerIds.get(1))
                    .orElseThrow(() -> new RuntimeException("Player 2 not found with id: " + playerIds.get(1)));
        }

        MatchModel matchModel = new MatchModel();
        matchModel.setGame(savedGameModel);
        matchModel.setPlayer1(player1);
        matchModel.setPlayer2(player2);
        matchModel.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        matchModel.setStatus(MatchStatus.IN_PROGRESS);
        MatchModel savedMatchModel = matchRepository.save(matchModel);

        switch (savedGameModel.getName()) {
            case GameName.TRIQUI:
                TriquiGameModel triquiGameModel = TriquiGameModel
                    .builder()
                    .match(savedMatchModel)
                    .currentTurn(player1)
                    .board("---------")
                    .build();
                triquiGameRepository.save(triquiGameModel);
                break;

            case GameName.CONNECT4:
                int row = 6;
                int column = 4;
                ConnectFourGameModel connectFourGameModel = ConnectFourGameModel
                    .builder()
                    .match(savedMatchModel)
                    .columnConnect4(column)
                    .rowConnect4(row)
                    .board(createInitialBoardConnect4(row,column))
                    .currentTurn(player1)
                    .build();
                connectFourGameRepository.save(connectFourGameModel);          
                break;
        
            default:
                break;
        } 
        return gameMapper.gameModelToGame(savedGameModel);
    }

    private String createInitialBoardConnect4(int row, int column) {
        return "0000,0000,0000,0000,0000,0000";
    }

    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return gameMapper.gameModelsToGames(gameRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id).map(gameMapper::gameModelToGame);
    }

    @Transactional
    public Game updateGame(Long id, Game game) {
        return gameRepository.findById(id).map(existingGame -> {
            game.setId(id);
            GameModel updatedGameModel = gameMapper.gameToGameModel(game);
            return gameMapper.gameModelToGame(gameRepository.save(updatedGameModel));
        }).orElse(null);
    }

    @Transactional
    public boolean deleteGame(Long id) {
        if (gameRepository.existsById(id)) {
            gameRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
