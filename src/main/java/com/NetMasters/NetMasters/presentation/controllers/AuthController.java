package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.application.services.PlayerService;
import com.NetMasters.NetMasters.core.entities.Player;
import com.NetMasters.NetMasters.infrastructure.persistence.config.JwtUtil;
import com.NetMasters.NetMasters.presentation.dto.CreatePlayerDTO;
import com.NetMasters.NetMasters.presentation.dto.LoginDTO;
import com.NetMasters.NetMasters.presentation.dto.LoginResponseDTO;
import com.NetMasters.NetMasters.presentation.dto.PlayerDTO;
import com.NetMasters.NetMasters.presentation.mappers.PlayerMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;
    private final JwtUtil jwtUtil;

    public AuthController(PlayerService playerService, PlayerMapper playerMapper, JwtUtil jwtUtil) {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> createPlayer(@Valid @RequestBody CreatePlayerDTO createPlayerDTO) {
        Player player = playerMapper.createPlayerDTOToPlayer(createPlayerDTO);
        Player newPlayer = playerService.createPlayer(player);

        // Crear token JWT para el usuario recién registrado
        String token = jwtUtil.generateToken(newPlayer.getUsername(), newPlayer.getId());

        // Crear response con token incluido
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUsername(newPlayer.getUsername());
        response.setEmail(newPlayer.getEmail());

        // Devolver response con token (similar al login)
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        Optional<Player> playerOpt = playerService.authenticatePlayer(loginDTO.getEmail(), loginDTO.getPassword());
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            String token = jwtUtil.generateToken(player.getUsername(), player.getId());
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            response.setUsername(player.getUsername());
            response.setEmail(player.getEmail());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            playerService.logoutPlayerByUsername(username);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

}
