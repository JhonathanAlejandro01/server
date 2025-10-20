package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.presentation.dto.MoveDTO;
import com.NetMasters.NetMasters.presentation.dto.ChatDTO;
import com.NetMasters.NetMasters.presentation.dto.EncryptedMoveDTO;
import com.NetMasters.NetMasters.presentation.dto.MatchStateDTO;
import com.NetMasters.NetMasters.application.services.MatchService;
import com.NetMasters.NetMasters.core.utils.TicTacToeValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MatchWebSocketController {

    private final Logger log = LoggerFactory.getLogger(MatchWebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MatchService matchService;

    @Autowired
    private com.NetMasters.NetMasters.application.services.TriquiService triquiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Maneja jugadas: cliente envía a /app/match/{id}/move
    @MessageMapping("/match/{matchId}/move")
    public void handleMove(@DestinationVariable("matchId") Long matchId, @Payload Object payload) {
        // payload can be MoveDTO or EncryptedMoveDTO
        try {
            String json = null;

            // Normalize common binary payload types to a JSON string
            if (payload instanceof byte[]) {
                json = new String((byte[]) payload, StandardCharsets.UTF_8);
            } else if (payload instanceof java.nio.ByteBuffer) {
                java.nio.ByteBuffer bb = (java.nio.ByteBuffer) payload;
                byte[] bytes = new byte[bb.remaining()];
                bb.get(bytes);
                json = new String(bytes, StandardCharsets.UTF_8);
            } else if (payload instanceof String) {
                json = (String) payload;
            }

            if (json != null) {
                java.util.Map<String, Object> map = objectMapper.readValue(json, java.util.Map.class);
                if (map.containsKey("encryptedBoard")) {
                    EncryptedMoveDTO em = objectMapper.convertValue(map, EncryptedMoveDTO.class);
                    handleEncryptedBoard(matchId, em);
                    return;
                } else {
                    MoveDTO mv = objectMapper.convertValue(map, MoveDTO.class);
                    messagingTemplate.convertAndSend("/topic/match/" + matchId + "/move", mv);
                    return;
                }
            }

            // If not a known text/binary form, try Map or convert directly
            if (payload instanceof java.util.Map) {
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) payload;
                if (map.containsKey("encryptedBoard")) {
                    EncryptedMoveDTO em = objectMapper.convertValue(map, EncryptedMoveDTO.class);
                    handleEncryptedBoard(matchId, em);
                    return;
                } else {
                    MoveDTO mv = objectMapper.convertValue(map, MoveDTO.class);
                    messagingTemplate.convertAndSend("/topic/match/" + matchId + "/move", mv);
                    return;
                }
            }

            // Last resort: try to convert payload to MoveDTO
            MoveDTO mv = objectMapper.convertValue(payload, MoveDTO.class);
            messagingTemplate.convertAndSend("/topic/match/" + matchId + "/move", mv);

        } catch (Exception ex) {
            log.error("Failed to handle move payload for match {}: {}", matchId, ex.toString());
        }
    }

    private void handleEncryptedBoard(Long matchId, EncryptedMoveDTO em) {
        try {
            Optional<String> keyOpt = matchService.getKeyForMatch(matchId);
            if (keyOpt.isEmpty()) {
                log.warn("No key for match {}", matchId);
                return;
            }
            String keyBase64 = keyOpt.get();
            String plain = decryptBoard(keyBase64, em.getEncryptedBoard());

            // parse incoming board to String[] where empty cells are ""
            String[] incoming = objectMapper.readValue(plain, String[].class);

            // Get current Triqui game model (throws if not found)
            com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiGameModel currentGame;
            try {
                currentGame = triquiService.getBoard(matchId);
            } catch (Exception e) {
                log.warn("Triqui game not found for match {}: {}", matchId, e.getMessage());
                return;
            }

            // Normalize current board (server stored as '-' for empty)
            String curBoardStr = currentGame.getBoard();
            String[] current = new String[9];
            for (int i = 0; i < 9; i++) {
                char c = curBoardStr.charAt(i);
                current[i] = c == '-' ? "" : String.valueOf(c);
            }

            // Compute diffs
            int diffs = 0;
            int changedPos = -1;
            for (int i = 0; i < 9; i++) {
                String a = current[i] == null ? "" : current[i];
                String b = incoming[i] == null ? "" : incoming[i];
                if (!a.equals(b)) {
                    diffs++;
                    changedPos = i;
                }
            }

            if (diffs == 0) {
                // Nothing changed: re-broadcast current state
                MatchStateDTO state = new MatchStateDTO();
                state.setWinnerId(currentGame.getWinner() != null ? currentGame.getWinner().getId() : null);
                state.setBoard(current);
                state.setStatus(currentGame.getMatch().getStatus().name());
                messagingTemplate.convertAndSend("/topic/match/" + matchId + "/state", state);
                messagingTemplate.convertAndSend("/topic/match/" + matchId + "/move", state);
                return;
            }

            if (diffs != 1) {
                log.warn("Invalid board update for match {}: diffs={}", matchId, diffs);
                return;
            }

            int position = changedPos;
            String newVal = incoming[position] == null ? "" : incoming[position];
            if (newVal.isEmpty()) {
                log.warn("Invalid empty move at {} for match {}", position, matchId);
                return;
            }
            char symbol = Character.toUpperCase(newVal.charAt(0));

            Long playerId = em.getPlayerId();
            if (playerId == null) {
                log.warn("Encrypted move missing playerId for match {}", matchId);
                return;
            }

            // Ensure it's player's turn
            if (!currentGame.getCurrentTurn().getId().equals(playerId)) {
                log.warn("Player {} attempted to move out of turn on match {} (currentTurn={})", playerId, matchId, currentGame.getCurrentTurn().getId());
                return;
            }

            // Validate symbol mapping (player1 -> X)
            Long p1 = currentGame.getMatch().getPlayer1().getId();
            char expected = p1.equals(playerId) ? 'X' : 'O';
            if (symbol != expected) {
                log.warn("Player {} sent symbol {} but expected {} on match {}", playerId, symbol, expected, matchId);
                return;
            }

            // Build MoveDTO and delegate to TriquiService to apply move transactionally
            MoveDTO moveDTO = new MoveDTO();
            moveDTO.setPlayerId(playerId);
            moveDTO.setPosition(position);

            try {
                com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiGameModel updated = triquiService.makeMove(matchId, moveDTO);

                // Normalize updated board
                String[] updatedArr = new String[9];
                String ub = updated.getBoard();
                for (int i = 0; i < 9; i++) {
                    char c = ub.charAt(i);
                    updatedArr[i] = c == '-' ? "" : String.valueOf(c);
                }

                MatchStateDTO state = new MatchStateDTO();
                state.setWinnerId(updated.getWinner() != null ? updated.getWinner().getId() : null);
                state.setBoard(updatedArr);
                state.setStatus(updated.getMatch().getStatus().name());

                // persist also into matchService.board for compatibility (JSON array of strings)
                String boardJson = objectMapper.writeValueAsString(updatedArr);
                matchService.saveBoardForMatch(matchId, boardJson);

                messagingTemplate.convertAndSend("/topic/match/" + matchId + "/state", state);
                messagingTemplate.convertAndSend("/topic/match/" + matchId + "/move", state);

            } catch (Exception e) {
                log.error("Error applying move via TriquiService for match {}: {}", matchId, e.getMessage());
            }

        } catch (Exception ex) {
            log.error("Failed to handle encrypted board for match {}: {}", matchId, ex.getMessage());
        }
    }

    private String decryptBoard(String keyBase64, String cipherBase64) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherBase64);
        byte[] iv = Arrays.copyOfRange(combined, 0, 12);
        byte[] cipher = Arrays.copyOfRange(combined, 12, combined.length);

        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(keyBase64), "AES");
        Cipher cipherInstance = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipherInstance.init(Cipher.DECRYPT_MODE, keySpec, spec);
        byte[] plain = cipherInstance.doFinal(cipher);
        return new String(plain, StandardCharsets.UTF_8);
    }

    // Maneja chat: cliente envía a /app/match/{id}/chat
    @MessageMapping("/match/{matchId}/chat")
    public void handleChat(@DestinationVariable("matchId") Long matchId, @Payload ChatDTO chat) {
        log.info("Received chat for match {}: {}", matchId, chat);

        messagingTemplate.convertAndSend("/topic/match/" + matchId + "/chat", chat);
        log.info("Broadcasted chat to /topic/match/{}/chat", matchId);
    }
}
