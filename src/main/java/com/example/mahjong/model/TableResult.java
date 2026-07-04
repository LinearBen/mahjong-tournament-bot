package com.example.mahjong.model;

import lombok.Getter;

import java.util.List;

@Getter
public class TableResult {

    private final MatchTable table;

    private final List<PlayerScore> ranking;

    public TableResult(MatchTable table, List<PlayerScore> ranking) {
        if (ranking.size() != 4) {
            throw new IllegalArgumentException("賽果必須剛好4人");
        }

        this.table = table;
        this.ranking = ranking;
    }

    public List<PlayerScore> topTwo() {
        return ranking.subList(0, 2);
    }

    public List<PlayerScore> bottomTwo() {
        return ranking.subList(2, 4);
    }
}