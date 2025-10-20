package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.application.services.PlayerService;
import com.NetMasters.NetMasters.core.entities.Player;
import com.NetMasters.NetMasters.presentation.dto.CreatePlayerDTO;
import com.NetMasters.NetMasters.presentation.dto.PlayerDTO;
import com.NetMasters.NetMasters.presentation.mappers.PlayerMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    @Autowired
    public PlayerController(PlayerService playerService, PlayerMapper playerMapper) {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
    }

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return ResponseEntity.ok(playerMapper.playersToPlayerDTOs(players));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(playerMapper::playerToPlayerDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id, @RequestBody PlayerDTO playerDTO) {
        Player player = playerMapper.playerDTOToPlayer(playerDTO);
        Optional<Player> updatedPlayerOpt = playerService.updatePlayer(id, player);
        if (updatedPlayerOpt.isPresent()) {
            return ResponseEntity.ok(playerMapper.playerToPlayerDTO(updatedPlayerOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        if (playerService.deletePlayer(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/online")
    public ResponseEntity<List<PlayerDTO>> getOnlinePlayers() {
        List<Player> onlinePlayers = playerService.getAllPlayers().stream()
                .filter(Player::isOnlineState)
                .collect(Collectors.toList());
        return ResponseEntity.ok(playerMapper.playersToPlayerDTOs(onlinePlayers));
    }

    @GetMapping("/me")
    public ResponseEntity<PlayerDTO> getCurrentUser(@RequestAttribute("userId") Long userId) {
        return playerService.getPlayerById(userId)
                .map(playerMapper::playerToPlayerDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
