package com.NetMasters.NetMasters.core.valueobjects;

import lombok.Value;

@Value
public class MatchId {
    private Long value;

    public MatchId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("MatchId must be positive");
        }
        this.value = value;
    }
}
