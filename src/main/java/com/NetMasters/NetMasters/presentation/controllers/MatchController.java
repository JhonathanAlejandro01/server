package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.application.services.MatchService;
import com.NetMasters.NetMasters.presentation.dto.CreateMatchDTO;
import com.NetMasters.NetMasters.presentation.dto.MatchDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.NetMasters.NetMasters.infrastructure.persistence.config.JwtUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final JwtUtil jwtUtil;

    @Autowired
    public MatchController(MatchService matchService, JwtUtil jwtUtil) {
        this.matchService = matchService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<MatchDTO> createMatch(@Valid @RequestBody CreateMatchDTO dto) {
        Long id = matchService.createMatch(dto.getGameId(), dto.getPlayer1Id(), dto.getPlayer2Id());
        Optional<Object> created = matchService.getMatchById(id);
        if (created.isPresent() && created.get() instanceof com.NetMasters.NetMasters.core.entities.Match) {
            com.NetMasters.NetMasters.core.entities.Match match = (com.NetMasters.NetMasters.core.entities.Match) created.get();
            MatchDTO matchDTO = new MatchDTO();
            matchDTO.setId(match.getId());
            matchDTO.setGameId(match.getGameId());
            matchDTO.setPlayer1Id(match.getPlayer1Id());
            matchDTO.setPlayer2Id(match.getPlayer2Id());
            matchDTO.setStartTime(match.getStartTime());
            matchDTO.setEndTime(match.getEndTime());
            matchDTO.setStatus(match.getStatus());
            return ResponseEntity.status(201).body(matchDTO);
        }
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public ResponseEntity<List<MatchDTO>> getAll() {
        List<Object> matches = matchService.getAllMatches();
        List<MatchDTO> dtos = matches.stream()
                .filter(m -> m instanceof com.NetMasters.NetMasters.core.entities.Match)
                .map(m -> (com.NetMasters.NetMasters.core.entities.Match)m)
                .map(match -> {
                    MatchDTO dto = new MatchDTO();
                    dto.setId(match.getId());
                    dto.setGameId(match.getGameId());
                    dto.setPlayer1Id(match.getPlayer1Id());
                    dto.setPlayer2Id(match.getPlayer2Id());
                    dto.setStartTime(match.getStartTime());
                    dto.setEndTime(match.getEndTime());
                    dto.setStatus(match.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getById(@PathVariable Long id) {
        Optional<Object> opt = matchService.getMatchById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Object m = opt.get();
        if (m instanceof com.NetMasters.NetMasters.core.entities.Match) {
            com.NetMasters.NetMasters.core.entities.Match match = (com.NetMasters.NetMasters.core.entities.Match) m;
            MatchDTO dto = new MatchDTO();
            dto.setId(match.getId());
            dto.setGameId(match.getGameId());
            dto.setPlayer1Id(match.getPlayer1Id());
            dto.setPlayer2Id(match.getPlayer2Id());
            dto.setStartTime(match.getStartTime());
            dto.setEndTime(match.getEndTime());
            dto.setStatus(match.getStatus());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/key")
    public ResponseEntity<Object> getMatchKey(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Extract token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        Long requesterId;
        try {
            requesterId = jwtUtil.extractUserId(token);
        } catch (Exception ex) {
            return ResponseEntity.status(401).build();
        }

        // check match ownership
        var opt = matchService.getMatchById(id);
        if (opt.isEmpty() || !(opt.get() instanceof com.NetMasters.NetMasters.core.entities.Match)) {
            return ResponseEntity.notFound().build();
        }
        com.NetMasters.NetMasters.core.entities.Match match = (com.NetMasters.NetMasters.core.entities.Match) opt.get();
        if (!requesterId.equals(match.getPlayer1Id()) && !(match.getPlayer2Id() != null && requesterId.equals(match.getPlayer2Id()))) {
            return ResponseEntity.status(403).build();
        }

        var keyOpt = matchService.getKeyForMatch(id);
        if (keyOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(java.util.Map.of("key", keyOpt.get()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MatchDTO>> getByStatus(@PathVariable String status) {
        List<Object> matches = matchService.getMatchesByStatus(status);
        List<MatchDTO> dtos = matches.stream()
                .filter(m -> m instanceof com.NetMasters.NetMasters.core.entities.Match)
                .map(m -> (com.NetMasters.NetMasters.core.entities.Match)m)
                .map(match -> {
                    MatchDTO dto = new MatchDTO();
                    dto.setId(match.getId());
                    dto.setGameId(match.getGameId());
                    dto.setPlayer1Id(match.getPlayer1Id());
                    dto.setPlayer2Id(match.getPlayer2Id());
                    dto.setStartTime(match.getStartTime());
                    dto.setEndTime(match.getEndTime());
                    dto.setStatus(match.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<MatchDTO>> getByPlayer(@PathVariable Long playerId) {
        List<Object> matches = matchService.getMatchesByPlayer(playerId);
        List<MatchDTO> dtos = matches.stream()
                .filter(m -> m instanceof com.NetMasters.NetMasters.core.entities.Match)
                .map(m -> (com.NetMasters.NetMasters.core.entities.Match)m)
                .map(match -> {
                    MatchDTO dto = new MatchDTO();
                    dto.setId(match.getId());
                    dto.setGameId(match.getGameId());
                    dto.setPlayer1Id(match.getPlayer1Id());
                    dto.setPlayer2Id(match.getPlayer2Id());
                    dto.setStartTime(match.getStartTime());
                    dto.setEndTime(match.getEndTime());
                    dto.setStatus(match.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean ok = matchService.updateMatchStatus(id, status);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<Void> finishMatch(@PathVariable Long id, @RequestParam(required = false) Long winnerId) {
        boolean ok = matchService.finishMatch(id, winnerId);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean ok = matchService.deleteMatch(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
