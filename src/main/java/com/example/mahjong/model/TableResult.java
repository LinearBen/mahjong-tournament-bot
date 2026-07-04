package com.example.mahjong.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 一桌打完後的結果。
 *
 * ranking 必須依照場務輸入的名次排序：
 * ranking[0] = 第 1 名
 * ranking[1] = 第 2 名
 * ranking[2] = 第 3 名
 * ranking[3] = 第 4 名
 *
 * Bot 不判斷同分或名次，只相信遊戲系統與場務輸入的順序。
 */
@Getter
public class TableResult {

    /** 這份賽果屬於哪一桌。 */
    private final MatchTable table;

    /** 依照名次排序的 4 位玩家成績。 */
    private final List<PlayerResult> ranking;

    public TableResult(MatchTable table, List<PlayerResult> ranking) {
        if (ranking.size() != 4) {
            throw new IllegalArgumentException("賽果必須剛好4人");
        }

        this.table = table;
        this.ranking = new ArrayList<>(ranking);
    }

    public List<PlayerResult> getRanking() {
        // 回傳複製品，避免外部程式改到這桌原始排名。
        return new ArrayList<>(ranking);
    }

    public List<PlayerResult> topTwo() {
        // 前兩名通常代表晉級，實際要去哪一輪由賽制服務決定。
        return new ArrayList<>(ranking.subList(0, 2));
    }

    public List<PlayerResult> bottomTwo() {
        // 後兩名通常代表掉入敗部或淘汰，依照不同輪次會有不同處理。
        return new ArrayList<>(ranking.subList(2, 4));
    }
}
