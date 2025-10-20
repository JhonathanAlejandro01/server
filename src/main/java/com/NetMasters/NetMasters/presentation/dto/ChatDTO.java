package com.NetMasters.NetMasters.presentation.dto;

import lombok.Data;

@Data
public class ChatDTO {
    private String text;
    private String time;
    private String sender;
    private Long playerId;
}
