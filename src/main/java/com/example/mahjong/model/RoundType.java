package com.example.mahjong.model;

/**
 * 比賽的大分類。
 *
 * RoundType 只表示「預賽 / 勝部 / 敗部 / 總決賽」這種類型；
 * 第幾輪會交給 RoundKey 的 roundNumber 處理。
 *
 * 注意：這裡不是把 WINNER_R1、WINNER_R2 全部列成 enum。
 * 那種設計之後會需要寫很多 switch / mapping。
 * 目前的設計是「RoundType + roundNumber」，讓下一輪可以用 roundNumber + 1 算出來。
 */
public enum RoundType {
    PRELIMINARY, // 預賽
    WINNER,      // 勝部，例如勝部R1、勝部R2
    LOSER,       // 敗部，例如敗部R1、敗部R2
    FINAL        // 總決賽
}
