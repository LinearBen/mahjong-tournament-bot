package com.example.mahjong.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 一張比賽桌。
 *
 * 例如「勝部R1 A桌」就是一個 MatchTable：
 * roundKey = 勝部R1
 * tableCode = A
 * players = 這桌的 4 位玩家
 */
@Getter
public class MatchTable {

    /** 這桌屬於哪個輪次，例如預賽、勝部R1、敗部R2。 */
    private final Round roundKey;

    /** 桌號，規則中使用 A、B、C、D... 這種大寫英文字母。 */
    private final String tableCode;

    /** 這桌的 4 位玩家。 */
    private final List<Player> players;

    public MatchTable(Round roundKey, String tableCode, List<Player> players) {
        if (players.size() != 4) {
            throw new IllegalArgumentException("一桌必須剛好 4 人");
        }

        this.roundKey = roundKey;
        this.tableCode = tableCode;
        this.players = new ArrayList<>(players);
    }

    public List<Player> getPlayers() {
        // 回傳複製品，避免外部程式拿到清單後直接改掉這桌玩家。
        return new ArrayList<>(players);
    }

    @Override
    public String toString() {
        // 讓除錯或 Discord 輸出時，可以直接看到「哪一輪、哪一桌、有哪些玩家」。
        return roundKey + " " + tableCode + "桌: " + players;
    }
}
