package com.example.mahjong.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 單一玩家在某一桌的比賽結果。
 *
 * TableResult 會用 List<PlayerResult> 依照名次排序：
 * 第 0 筆是第 1 名，第 1 筆是第 2 名，以此類推。
 */
@Getter
@AllArgsConstructor
public class PlayerResult {

    /** 這筆成績屬於哪一位玩家。 */
    private final Player player;

    /** 遊戲系統結算出的分數，Bot 只記錄，不自行判斷同分或名次。 */
    private final int score;
}
