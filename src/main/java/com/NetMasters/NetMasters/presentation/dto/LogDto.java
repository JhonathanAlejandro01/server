package com.NetMasters.NetMasters.presentation.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class LogDto {
    private String event;
    private String details;
    private Instant timestamp;

    public LogDto(String event, String details, Instant timestamp) {
        this.event = event;
        this.details = details;
        this.timestamp = timestamp;
    }
}
