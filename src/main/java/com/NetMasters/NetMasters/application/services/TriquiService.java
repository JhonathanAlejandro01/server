package com.NetMasters.NetMasters.application.services;

import com.NetMasters.NetMasters.core.entities.MatchStatus;
import com.NetMasters.NetMasters.core.events.GameFinishedEvent;
import com.NetMasters.NetMasters.core.events.GameStartedEvent;
import com.NetMasters.NetMasters.core.events.MoveMadeEvent;
import com.NetMasters.NetMasters.core.interfaces.EventBus;
import com.NetMasters.NetMasters.core.interfaces.TriquiGameServiceInterface;
import com.NetMasters.NetMasters.core.valueobjects.Coordinates;
import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiGameModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiMoveModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.PlayerRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.TriquiGameRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.TriquiMoveRepository;
import com.NetMasters.NetMasters.presentation.dto.MoveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TriquiService implements TriquiGameServiceInterface {

    private final TriquiGameRepository triquiGameRepository;
    private final TriquiMoveRepository triquiMoveRepository;
    private final PlayerRepository playerRepository;
    private final EventBus eventBus;

    @Autowired
    public TriquiService(TriquiGameRepository triquiGameRepository, TriquiMoveRepository triquiMoveRepository,
                          PlayerRepository playerRepository, EventBus eventBus) {
        this.triquiGameRepository = triquiGameRepository;
        this.triquiMoveRepository = triquiMoveRepository;
        this.playerRepository = playerRepository;
        this.eventBus = eventBus;
    }

    @Transactional(readOnly = true)
    public TriquiGameModel getBoard(Long matchId) {
        return triquiGameRepository.findByMatchId(matchId)
                .orElseThrow(() -> new IllegalStateException("Game not found"));
    }

    @Transactional
    public TriquiGameModel makeMove(Long matchId, MoveDTO move) {
        TriquiGameModel triquiGame = getBoard(matchId);

        if (triquiGame.getWinner() != null || triquiGame.getMatch().getStatus() == MatchStatus.FINISHED) {
            throw new IllegalStateException("Game is already finished.");
        }

        if (!triquiGame.getCurrentTurn().getId().equals(move.getPlayerId())) {
            throw new IllegalStateException("It's not your turn.");
        }

        if (move.getPosition() < 0 || move.getPosition() >= 9 || triquiGame.getBoard().charAt(move.getPosition()) != '-') {
            throw new IllegalStateException("Invalid move.");
        }

        PlayerModel player1 = triquiGame.getMatch().getPlayer1();
        PlayerModel player2 = triquiGame.getMatch().getPlayer2();

        if (player2 == null) {
            throw new IllegalStateException("The second player has not joined the game yet.");
        }

        char symbol = getPlayerSymbol(player1, move.getPlayerId());

        StringBuilder board = new StringBuilder(triquiGame.getBoard());
        board.setCharAt(move.getPosition(), symbol);
        triquiGame.setBoard(board.toString());

        saveMove(triquiGame, move.getPlayerId(), move.getPosition(), symbol);

        if (checkWinner(triquiGame.getBoard(), symbol)) {
            PlayerModel winner = playerRepository.findById(move.getPlayerId()).get();
            triquiGame.setWinner(winner);
            triquiGame.getMatch().setStatus(MatchStatus.FINISHED);

            // Publicar evento de juego terminado (con ganador)
            eventBus.publish(new GameFinishedEvent(
                this,
                new MatchId(triquiGame.getMatch().getId()),
                new PlayerId(winner.getId()),
                false // No es empate
            ));
        } else if (isBoardFull(triquiGame.getBoard())) {
            triquiGame.getMatch().setStatus(MatchStatus.FINISHED); // Draw

            // Publicar evento de juego terminado (empate)
            eventBus.publish(new GameFinishedEvent(
                this,
                new MatchId(triquiGame.getMatch().getId()),
                null, // No hay ganador
                true // Es empate
            ));
        } else {
            PlayerModel nextPlayer = getNextPlayer(player1, player2, move.getPlayerId());
            triquiGame.setCurrentTurn(nextPlayer);
        }

        // Publicar evento de movimiento realizado
        eventBus.publish(new MoveMadeEvent(
            this,
            new MatchId(triquiGame.getMatch().getId()),
            new PlayerId(move.getPlayerId()),
            new Coordinates(move.getPosition() / 3, move.getPosition() % 3), // Convertir posición lineal a coordenadas
            String.valueOf(symbol)
        ));

        return triquiGameRepository.save(triquiGame);
    }

    private char getPlayerSymbol(PlayerModel player1, Long currentId) {
        if (player1.getId().equals(currentId)) {
            return 'X';
        }
        return 'O';
    }

    private PlayerModel getNextPlayer(PlayerModel player1, PlayerModel player2, Long currentPlayerId) {
        if (player1.getId().equals(currentPlayerId)) {
            return player2;
        }
        return player1;
    }

    private void saveMove(TriquiGameModel game, Long playerId, int position, char symbol) {
        TriquiMoveModel move = new TriquiMoveModel();
        move.setTriquiGame(game);
        move.setPlayer(playerRepository.findById(playerId).get());
        move.setPosition(position);
        move.setSymbol(symbol);
        // moveNumber can be derived from the number of existing moves for this game
        long moveNumber = triquiMoveRepository.countByTriquiGameId(game.getId()) + 1;
        move.setMoveNumber((int) moveNumber);
        triquiMoveRepository.save(move);
    }

    private boolean isBoardFull(String board) {
        return !board.contains("-");
    }

    private boolean checkWinner(String board, char symbol) {
        // Check rows
        for (int i = 0; i < 9; i += 3) {
            if (board.charAt(i) == symbol && board.charAt(i + 1) == symbol && board.charAt(i + 2) == symbol) {
                return true;
            }
        }
        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board.charAt(i) == symbol && board.charAt(i + 3) == symbol && board.charAt(i + 6) == symbol) {
                return true;
            }
        }
        // Check diagonals
        if (board.charAt(0) == symbol && board.charAt(4) == symbol && board.charAt(8) == symbol) {
            return true;
        }
        if (board.charAt(2) == symbol && board.charAt(4) == symbol && board.charAt(6) == symbol) {
            return true;
        }
        return false;
    }

    // Implementaciones de TriquiGameServiceInterface
    @Override
    public Long createTriquiGame(Long matchId) {
        // TODO: Implementar creación de juego Triqui
        return null;
    }

    @Override
    public Optional<Object> getTriquiGameById(Long gameId) {
        return Optional.ofNullable(triquiGameRepository.findById(gameId).orElse(null));
    }

    @Override
    public boolean makeMove(Long gameId, Long playerId, int position) {
        // Adaptar el método existente
        MoveDTO moveDTO = new MoveDTO();
        moveDTO.setPlayerId(playerId);
        moveDTO.setPosition(position);
        try {
            makeMove(gameId, moveDTO);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getBoardState(Long gameId) {
        TriquiGameModel game = triquiGameRepository.findById(gameId).orElse(null);
        return game != null ? game.getBoard() : "";
    }

    @Override
    public Optional<Long> checkWinner(Long gameId) {
        TriquiGameModel game = triquiGameRepository.findById(gameId).orElse(null);
        if (game != null && game.getWinner() != null) {
            return Optional.of(game.getWinner().getId());
        }
        return Optional.empty();
    }

    @Override
    public boolean isBoardFull(Long gameId) {
        String board = getBoardState(gameId);
        return !board.contains("-");
    }

    @Override
    public boolean resetGame(Long gameId) {
        // TODO: Implementar reinicio del juego
        return false;
    }

    @Override
    public Long getCurrentTurn(Long gameId) {
        TriquiGameModel game = triquiGameRepository.findById(gameId).orElse(null);
        return game != null && game.getCurrentTurn() != null ? game.getCurrentTurn().getId() : null;
    }

    @Override
    public boolean isPositionAvailable(Long gameId, int position) {
        String board = getBoardState(gameId);
        return position >= 0 && position < 9 && board.charAt(position) == '-';
    }
}
