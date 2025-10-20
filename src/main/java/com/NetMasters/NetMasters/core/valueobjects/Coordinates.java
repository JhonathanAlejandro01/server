package com.NetMasters.NetMasters.core.valueobjects;

import lombok.Value;

@Value
public class Coordinates {
    private int row;
    private int col;

    public Coordinates(int row, int col) {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }
        this.row = row;
        this.col = col;
    }
}
