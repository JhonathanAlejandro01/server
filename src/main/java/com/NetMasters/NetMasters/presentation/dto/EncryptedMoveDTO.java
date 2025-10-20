package com.NetMasters.NetMasters.presentation.dto;

import lombok.Data;

@Data
public class EncryptedMoveDTO {
    private String encryptedBoard; // base64 combined iv + cipher
    private Long playerId;
}
