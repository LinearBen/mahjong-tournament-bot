package com.example.mahjong.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 指定一個明確的比賽輪次。
 *
 * 範例：
 * new Round(RoundType.PRELIMINARY, 0) 代表「預賽」
 * new Round(RoundType.WINNER, 2)      代表「勝部R2」
 * new Round(RoundType.LOSER, 4)       代表「敗部R4」
 * new Round(RoundType.FINAL, 0)       代表「總決賽」
 *
 * 這種設計比「每一輪都做成 enum」更適合寫賽制流程：
 * 勝部R1 到 勝部R2 只要把 roundNumber + 1，不需要額外寫 mapping。
 */
@Getter
@AllArgsConstructor
public class Round {

    /** 比賽輪次類型，例如預賽、勝部、敗部、總決賽。 */
    private final RoundType type;

    /** 勝部/敗部的第幾輪；預賽和總決賽不需要輪數時可填 0。 */
    private final int roundNumber;

    @Override
    public String toString() {
        // 統一把程式內部的資料轉成場務看得懂的文字。
        return switch (type) {
            case PRELIMINARY -> "預賽";
            case WINNER -> "勝部R" + roundNumber;
            case LOSER -> "敗部R" + roundNumber;
            case FINAL -> "總決賽";
        };
    }
}
