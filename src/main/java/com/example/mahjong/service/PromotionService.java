package com.example.mahjong.service;

import com.example.mahjong.model.Player;
import com.example.mahjong.model.PlayerResult;
import com.example.mahjong.model.TableResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 共用的晉級與玩家名單處理。
 */
public class PromotionService {

    public List<Player> topTwo(TableResult result) {
        return toPlayers(result.topTwo());
    }

    public List<PlayerResult> highestScore(List<PlayerResult> results, int count) {
        if (count > results.size()) {
            throw new IllegalArgumentException("選取人數不可超過候選人數");
        }

        List<PlayerResult> sortedResults = new ArrayList<>(results);
        sortedResults.sort(Comparator.comparingInt(PlayerResult::getScore).reversed());
        return new ArrayList<>(sortedResults.subList(0, count));
    }

    public List<Player> toPlayers(List<PlayerResult> results) {
        List<Player> players = new ArrayList<>();
        for (PlayerResult result : results) {
            players.add(result.getPlayer());
        }
        return players;
    }

    public void ensureUniquePlayers(List<Player> players, String message) {
        Set<String> ids = new HashSet<>();
        for (Player player : players) {
            if (!ids.add(player.getDiscordId())) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
