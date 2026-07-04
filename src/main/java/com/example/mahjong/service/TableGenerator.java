package com.example.mahjong.service;

import com.example.mahjong.model.MatchTable;
import com.example.mahjong.model.Player;
import com.example.mahjong.model.Round;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 產生每一輪的桌次。
 */
public class TableGenerator {

    public List<MatchTable> generate(Round round, List<Player> players) {
        if (players.size() % 4 != 0) {
            throw new IllegalArgumentException("玩家人數必須是 4 的倍數");
        }

        List<Player> shuffledPlayers = new ArrayList<>(players);
        Collections.shuffle(shuffledPlayers);

        List<MatchTable> tables = new ArrayList<>();
        for (int i = 0; i < shuffledPlayers.size(); i += 4) {
            String tableCode = String.valueOf((char) ('A' + tables.size()));
            tables.add(new MatchTable(round, tableCode, shuffledPlayers.subList(i, i + 4)));
        }

        return tables;
    }
}
