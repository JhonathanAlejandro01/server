package com.NetMasters.NetMasters.application.services;

import com.NetMasters.NetMasters.core.events.GameStartedEvent;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.ConnectFourGameRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.TriquiGameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class GameStartedWebSocketListener {

    private static final Logger logger = LoggerFactory.getLogger(GameStartedWebSocketListener.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final TriquiGameRepository triquiGameRepository;
    private final ConnectFourGameRepository connectFourGameRepository;

    public GameStartedWebSocketListener(SimpMessagingTemplate messagingTemplate,
                                        TriquiGameRepository triquiGameRepository,
                                        ConnectFourGameRepository connectFourGameRepository) {
        this.messagingTemplate = messagingTemplate;
        this.triquiGameRepository = triquiGameRepository;
        this.connectFourGameRepository = connectFourGameRepository;
    }

    @EventListener
    public void onGameStarted(GameStartedEvent event) {
        Long matchId = event.getMatchId().getValue();
        logger.info("GameStartedEvent received for match {}", matchId);

        // Build payload with possible initial board state
        var payload = new java.util.HashMap<String, Object>();
        payload.put("matchId", matchId);
        payload.put("player1Id", event.getPlayer1Id() != null ? event.getPlayer1Id().getValue() : null);
        payload.put("player2Id", event.getPlayer2Id() != null ? event.getPlayer2Id().getValue() : null);

        // Try to add Triqui board
        triquiGameRepository.findByMatchId(matchId).ifPresent(triqui -> {
            payload.put("game", "TRIQUI");
            payload.put("board", triqui.getBoard());
            payload.put("currentTurn", triqui.getCurrentTurn() != null ? triqui.getCurrentTurn().getId() : null);
        });

        // Try to add Connect4 board
        connectFourGameRepository.findByMatchId(matchId).ifPresent(cf -> {
            payload.put("game", "CONNECT4");
            payload.put("board", cf.getBoard());
            payload.put("rows", cf.getRowConnect4());
            payload.put("cols", cf.getColumnConnect4());
            payload.put("currentTurn", cf.getCurrentTurn() != null ? cf.getCurrentTurn().getId() : null);
        });

        // Send to a topic for the match
        String destination = "/topic/match/" + matchId + "/started";
        try {
            messagingTemplate.convertAndSend(destination, payload);
            logger.info("Sent GameStarted message to {}", destination);
        } catch (Exception ex) {
            logger.error("Failed to send GameStarted WebSocket message for match {}", matchId, ex);
        }
    }
}
