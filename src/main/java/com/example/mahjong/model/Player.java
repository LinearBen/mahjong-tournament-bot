package com.example.mahjong.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 玩家基本資料。
 *
 * 這個 class 只負責記錄「這個人是誰」，不記錄分數或名次。
 * 分數和名次會放在 PlayerResult，避免玩家資料和比賽結果混在一起。
 */
@Getter
@AllArgsConstructor
public class Player {

    /** Discord 使用者 ID，用來精準辨識玩家。顯示名稱可能改，但 ID 不會隨便改。 */
    private final String discordId;

    /** 玩家在 Discord 或賽事中顯示的名字，主要用來輸出給場務看。 */
    private final String displayName;

    @Override
    public String toString() {
        // 印出玩家清單時，直接顯示名字會比顯示整個物件內容更好讀。
        return displayName;
    }
}
