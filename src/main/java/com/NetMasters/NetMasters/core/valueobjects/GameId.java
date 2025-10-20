package com.NetMasters.NetMasters.core.valueobjects;

import lombok.Value;

@Value
public class GameId {
    private Long value;

    public GameId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("GameId must be positive");
        }
        this.value = value;
    }
}
