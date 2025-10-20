package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.application.services.MatchService;
import com.NetMasters.NetMasters.presentation.dto.JoinMessage;
import com.NetMasters.NetMasters.presentation.dto.GameStartedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class GameWebSocketController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/join")
    public void handleJoin(@Payload JoinMessage join) {
        Long matchId = join.getMatchId();
        Long playerId = join.getPlayerId();

        Optional<com.NetMasters.NetMasters.core.entities.Match> updated = matchService.assignPlayerToMatch(matchId, playerId);

        if (updated.isPresent()) {
            GameStartedDTO dto = new GameStartedDTO();
            dto.setMatchId(matchId);
            dto.setMessage("player joined");
            // enviar al topic de partida
            messagingTemplate.convertAndSend("/topic/match/" + matchId + "/started", dto);
        } else {
            // opcional: podrías enviar un mensaje de error a un queue específico
        }
    }
}
