package com.NetMasters.NetMasters.presentation.controllers;

import com.NetMasters.NetMasters.application.services.ConnectFourService;
import com.NetMasters.NetMasters.infrastructure.persistence.models.ConnectFourGameModel;
import com.NetMasters.NetMasters.presentation.dto.ConnectFourBoardDTO;
import com.NetMasters.NetMasters.presentation.dto.ConnectFourMoveDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connect4")
public class ConnectFourController {

    private final ConnectFourService connectFourService;

    @Autowired
    public ConnectFourController(ConnectFourService connectFourService) {
        this.connectFourService = connectFourService;
    }

    @GetMapping("/{matchId}/board")
    public ResponseEntity<ConnectFourBoardDTO> getBoard(@PathVariable Long matchId) {
        // TODO: Adaptar para usar la nueva interfaz
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{matchId}/move")
    public ResponseEntity<ConnectFourBoardDTO> makeMove(@PathVariable Long matchId, @Valid @RequestBody ConnectFourMoveDTO moveDTO) {
        // TODO: Adaptar para usar la nueva interfaz
        return ResponseEntity.notFound().build();
    }

    // Mapper simple para convertir el modelo de persistencia a DTO
    private ConnectFourBoardDTO mapModelToDto(ConnectFourGameModel model) {
        ConnectFourBoardDTO dto = new ConnectFourBoardDTO();
        dto.setBoard(model.getBoard());
        dto.setRows(model.getRowConnect4());
        dto.setColumns(model.getColumnConnect4());
        if (model.getCurrentTurn() != null) {
            dto.setCurrentTurnPlayerId(model.getCurrentTurn().getId());
        }
        if (model.getWinner() != null) {
            dto.setWinnerPlayerId(model.getWinner().getId());
        }
        return dto;
    }
}
