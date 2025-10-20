package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.application.services.GameService;
import com.NetMasters.NetMasters.core.entities.Game;
import com.NetMasters.NetMasters.presentation.dto.CreateGameDTO;
import com.NetMasters.NetMasters.presentation.dto.GameDTO;
import com.NetMasters.NetMasters.presentation.mappers.GameMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;

    @Autowired
    public GameController(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    @PostMapping
    public ResponseEntity<GameDTO> createGame(@Valid @RequestBody CreateGameDTO createGameDTO) {
        Game game = gameMapper.createGameDTOToGame(createGameDTO);
        Game newGame = gameService.createGame(game, createGameDTO.getPlayerIds());
        return ResponseEntity.ok(gameMapper.gameToGameDTO(newGame));
    }

    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(gameMapper.gamesToGameDTOs(games));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(gameMapper::gameToGameDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> updateGame(@PathVariable Long id, @RequestBody GameDTO gameDTO) {
        Game game = gameMapper.gameDTOToGame(gameDTO);
        Game updatedGame = gameService.updateGame(id, game);
        if (updatedGame != null) {
            return ResponseEntity.ok(gameMapper.gameToGameDTO(updatedGame));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (gameService.deleteGame(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
