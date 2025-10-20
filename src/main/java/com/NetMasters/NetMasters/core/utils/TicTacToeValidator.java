package com.NetMasters.NetMasters.core.utils;

import java.util.Optional;

public class TicTacToeValidator {
    // board: array length 9 with 'X','O' or null
    public static Optional<Long> checkWinner(String[] board, Long player1Id, Long player2Id) {
        int[][] lines = new int[][] {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };
        for (int[] l : lines) {
            String a = board[l[0]];
            String b = board[l[1]];
            String c = board[l[2]];
            if (a != null && a.equals(b) && a.equals(c)) {
                if (a.equals("X")) return Optional.of(player1Id);
                else return Optional.of(player2Id);
            }
        }
        return Optional.empty();
    }
}
