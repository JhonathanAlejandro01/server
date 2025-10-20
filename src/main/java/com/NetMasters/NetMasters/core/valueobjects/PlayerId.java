package com.NetMasters.NetMasters.core.valueobjects;

import lombok.Value;

@Value
public class PlayerId {
    private Long value;

    public PlayerId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("PlayerId must be positive");
        }
        this.value = value;
    }
}
