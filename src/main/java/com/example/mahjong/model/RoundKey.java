package com.example.mahjong.model;

import lombok.Getter;

@Getter
public class RoundKey {

    private final RoundType type;

    private final int roundNumber;

    public RoundKey(RoundType type, int roundNumber) {
        this.type = type;
        this.roundNumber = roundNumber;
    }

    @Override
    public String toString() {
        return switch (type) {
            case PRELIMINARY -> "預賽";
            case WINNER -> "勝部R" + roundNumber;
            case LOSER -> "敗部R" + roundNumber;
            case FINAL -> "總決賽";
        };
    }
}