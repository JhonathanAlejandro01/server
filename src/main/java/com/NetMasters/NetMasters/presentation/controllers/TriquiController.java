package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.application.services.TriquiService;
import com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiGameModel;
import com.NetMasters.NetMasters.presentation.dto.BoardDTO;
import com.NetMasters.NetMasters.presentation.dto.MoveDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/triqui")
public class TriquiController {

    private final TriquiService triquiService;

    @Autowired
    public TriquiController(TriquiService triquiService) {
        this.triquiService = triquiService;
    }

    @GetMapping("/{matchId}/board")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long matchId) {
        TriquiGameModel triquiGame = triquiService.getBoard(matchId);
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoard(triquiGame.getBoard());
        if (triquiGame.getCurrentTurn() != null) {
            boardDTO.setCurrentTurn(triquiGame.getCurrentTurn().getId());
        }
        if (triquiGame.getWinner() != null) {
            boardDTO.setWinner(triquiGame.getWinner().getId());
        }
        return ResponseEntity.ok(boardDTO);
    }

    @PostMapping("/{matchId}/move")
    public ResponseEntity<BoardDTO> makeMove(@PathVariable Long matchId, @Valid @RequestBody MoveDTO move) {
        TriquiGameModel updatedGame = triquiService.makeMove(matchId, move);
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoard(updatedGame.getBoard());
        if (updatedGame.getCurrentTurn() != null) {
            boardDTO.setCurrentTurn(updatedGame.getCurrentTurn().getId());
        }
        if (updatedGame.getWinner() != null) {
            boardDTO.setWinner(updatedGame.getWinner().getId());
        }
        return ResponseEntity.ok(boardDTO);
    }
}
