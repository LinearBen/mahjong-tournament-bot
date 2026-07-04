package com.example.mahjong.model;

import java.util.List;

public class MatchTable {
    private final RoundKey roundKey;
    private final String tableCode;
    private final List<Player> players;

    public MatchTable(RoundKey roundKey, String tableCode, List<Player> players) {
        if (players.size() != 4) {
            throw new IllegalArgumentException("一桌必須剛好 4 人");
        }

        this.roundKey = roundKey;
        this.tableCode = tableCode;
        this.players = players;
    }
package com.example.mahjong.model;

package com.example.mahjong.model;

import lombok.Getter;

import java.util.List;

@Getter
public class MatchTable {

    private final RoundKey roundKey;

    private final String tableCode;

    private final List<Player> players;

    public MatchTable(RoundKey roundKey, String tableCode, List<Player> players) {
        if (players.size() != 4) {
            throw new IllegalArgumentException("一桌必須剛好4人");
        }

        this.roundKey = roundKey;
        this.tableCode = tableCode;
        this.players = players;
    }

    @Override
    public String toString() {
        return roundKey + " " + tableCode + "桌: " + players;
    }
}