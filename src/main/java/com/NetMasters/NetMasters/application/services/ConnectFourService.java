package com.NetMasters.NetMasters.application.services;

import com.NetMasters.NetMasters.core.events.GameStartedEvent;
import com.NetMasters.NetMasters.core.interfaces.ConnectFourGameServiceInterface;
import com.NetMasters.NetMasters.infrastructure.persistence.models.ConnectFourGameModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.ConnectFourGameRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.MatchRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.PlayerRepository;
import com.NetMasters.NetMasters.core.interfaces.EventBus;
import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConnectFourService implements ConnectFourGameServiceInterface {

    private final ConnectFourGameRepository connectFourGameRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final EventBus eventBus;
    private final com.NetMasters.NetMasters.infrastructure.persistence.config.AesEncryptionService encryptionService;
    private final com.NetMasters.NetMasters.infrastructure.persistence.repositories.ConnectFourMoveRepository connectFourMoveRepository;

    @Autowired
    public ConnectFourService(ConnectFourGameRepository connectFourGameRepository,
                              MatchRepository matchRepository,
                              PlayerRepository playerRepository,
                              EventBus eventBus,
                              com.NetMasters.NetMasters.infrastructure.persistence.config.AesEncryptionService encryptionService,
                              com.NetMasters.NetMasters.infrastructure.persistence.repositories.ConnectFourMoveRepository connectFourMoveRepository) {
        this.connectFourGameRepository = connectFourGameRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.eventBus = eventBus;
        this.encryptionService = encryptionService;
        this.connectFourMoveRepository = connectFourMoveRepository;
    }

    @Override
    public Long createConnectFourGame(Long matchId) {
        var matchModel = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalStateException("Match not found: " + matchId));

        int row = 6;
        int column = 4;
        ConnectFourGameModel model = ConnectFourGameModel.builder()
                .match(matchModel)
                .rowConnect4(row)
                .columnConnect4(column)
                .board(createInitialBoardConnect4(row, column))
                .currentTurn(matchModel.getPlayer1())
                .build();

        model = connectFourGameRepository.save(model);

        // Publicar GameStartedEvent
        try {
            eventBus.publish(new GameStartedEvent(
                    this,
                    new MatchId(matchId),
                    new PlayerId(matchModel.getPlayer1().getId()),
                    matchModel.getPlayer2() != null ? new PlayerId(matchModel.getPlayer2().getId()) : null
            ));
        } catch (Exception ignored) {}

        return model.getId();
    }

    private String createInitialBoardConnect4(int row, int column) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < column; c++) {
                sb.append('0');
            }
            if (r < row - 1) sb.append(',');
        }
        return sb.toString();
    }

    @Override
    public Optional<Object> getConnectFourGameById(Long gameId) {
        return Optional.ofNullable(connectFourGameRepository.findById(gameId).orElse(null));
    }

    @Override
    public boolean makeMove(Long gameId, Long playerId, int column) {
        ConnectFourGameModel game = connectFourGameRepository.findById(gameId).orElseThrow(() -> new IllegalStateException("Connect4 game not found: " + gameId));

        if (game.getWinner() != null || game.getMatch().getStatus() == com.NetMasters.NetMasters.core.entities.MatchStatus.FINISHED) {
            throw new IllegalStateException("Game is already finished.");
        }

        if (!game.getCurrentTurn().getId().equals(playerId)) {
            throw new IllegalStateException("It's not your turn.");
        }

        int rows = game.getRowConnect4();
        int cols = game.getColumnConnect4();

        String[] rowsArr = game.getBoard().split(",");
        if (column < 0 || column >= cols) throw new IllegalArgumentException("Invalid column");

        // Find lowest empty row in column
        int targetRow = -1;
        for (int r = rows - 1; r >= 0; r--) {
            if (rowsArr[r].charAt(column) == '0') {
                targetRow = r;
                break;
            }
        }
        if (targetRow == -1) throw new IllegalStateException("Column is full");

        // Determine symbol: '1' for player1, '2' for player2
        long player1Id = game.getMatch().getPlayer1().getId();
        char symbol = (playerId == player1Id) ? '1' : '2';

        // Place the symbol
        StringBuilder rowSb = new StringBuilder(rowsArr[targetRow]);
        rowSb.setCharAt(column, symbol);
        rowsArr[targetRow] = rowSb.toString();

        // Rebuild board
        String newBoard = String.join(",", rowsArr);
        game.setBoard(newBoard);

        // Switch turn
        com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel nextPlayer = (game.getMatch().getPlayer1().getId().equals(playerId)) ? game.getMatch().getPlayer2() : game.getMatch().getPlayer1();
        game.setCurrentTurn(nextPlayer);

        // Save move
        connectFourGameRepository.save(game);

        // Persist move model with encrypted payload
        try {
            com.NetMasters.NetMasters.infrastructure.persistence.models.ConnectFourMoveModel move =
                    new com.NetMasters.NetMasters.infrastructure.persistence.models.ConnectFourMoveModel();
            move.setConnect4Game(game);
            move.setPlayer(playerRepository.findById(playerId).orElse(null));
            long moveNumber = connectFourMoveRepository.countByConnect4GameId(game.getId()) + 1;
            move.setMoveNumber((int) moveNumber);
            move.setColumnPlayed(column);
            move.setRowPlayed(targetRow);
            move.setColor(symbol);

            var payload = new java.util.HashMap<String,Object>();
            payload.put("matchId", game.getMatch().getId());
            payload.put("playerId", playerId);
            payload.put("column", column);
            payload.put("row", targetRow);
            payload.put("symbol", String.valueOf(symbol));
            String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            String enc = encryptionService.encrypt(json);
            move.setEncryptedPayload(enc);

            // Persist move
            try {
                connectFourMoveRepository.save(move);
            } catch (Exception ex) {
                // ignore save errors for now
            }
        } catch (Exception ignored) {}

        // Publish MoveMadeEvent
        try {
            eventBus.publish(new com.NetMasters.NetMasters.core.events.MoveMadeEvent(
                    this,
                    new MatchId(game.getMatch().getId()),
                    new PlayerId(playerId),
                    new com.NetMasters.NetMasters.core.valueobjects.Coordinates(targetRow, column),
                    String.valueOf(symbol)
            ));
        } catch (Exception ignored) {}

        // Check for winner (4 in a row)
        if (checkWinner(rowsArr, targetRow, column, symbol)) {
            game.setWinner(playerRepository.findById(playerId).orElse(null));
            game.getMatch().setStatus(com.NetMasters.NetMasters.core.entities.MatchStatus.FINISHED);
            connectFourGameRepository.save(game);
            try {
                eventBus.publish(new com.NetMasters.NetMasters.core.events.GameFinishedEvent(
                        this,
                        new MatchId(game.getMatch().getId()),
                        new PlayerId(playerId),
                        false
                ));
            } catch (Exception ignored) {}
            return true;
        }

        // Check draw
        if (isBoardFull(gameId)) {
            game.getMatch().setStatus(com.NetMasters.NetMasters.core.entities.MatchStatus.FINISHED);
            connectFourGameRepository.save(game);
            try {
                eventBus.publish(new com.NetMasters.NetMasters.core.events.GameFinishedEvent(
                        this,
                        new MatchId(game.getMatch().getId()),
                        null,
                        true
                ));
            } catch (Exception ignored) {}
        }

        return true;
    }

    private boolean checkWinner(String[] rowsArr, int r, int c, char sym) {
        int rows = rowsArr.length;
        int cols = rowsArr[0].length();

        // Check directions: horizontal, vertical, diag1, diag2
        int[][] dirs = {{0,1},{1,0},{1,1},{1,-1}};
        for (int[] d : dirs) {
            int count = 1;
            // forward
            int rr = r + d[0], cc = c + d[1];
            while (rr >= 0 && rr < rows && cc >= 0 && cc < cols && rowsArr[rr].charAt(cc) == sym) {
                count++; rr += d[0]; cc += d[1];
            }
            // backward
            rr = r - d[0]; cc = c - d[1];
            while (rr >= 0 && rr < rows && cc >= 0 && cc < cols && rowsArr[rr].charAt(cc) == sym) {
                count++; rr -= d[0]; cc -= d[1];
            }
            if (count >= 4) return true;
        }
        return false;
    }

    @Override
    public String getBoardState(Long gameId) {
        ConnectFourGameModel gm = connectFourGameRepository.findById(gameId).orElse(null);
        return gm != null ? gm.getBoard() : "";
    }

    @Override
    public Optional<Long> checkWinner(Long gameId) {
        ConnectFourGameModel gm = connectFourGameRepository.findById(gameId).orElse(null);
        if (gm != null && gm.getWinner() != null) return Optional.of(gm.getWinner().getId());
        return Optional.empty();
    }

    @Override
    public boolean isBoardFull(Long gameId) {
        String board = getBoardState(gameId);
        return board == null || board.isBlank() || !board.contains("0");
    }

    @Override
    public boolean resetGame(Long gameId) {
        // TODO: Implementar reinicio del juego
        return false;
    }

    @Override
    public Long getCurrentTurn(Long gameId) {
        ConnectFourGameModel gm = connectFourGameRepository.findById(gameId).orElse(null);
        return gm != null && gm.getCurrentTurn() != null ? gm.getCurrentTurn().getId() : null;
    }
}
