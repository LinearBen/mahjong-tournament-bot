package com.example.mahjong.service;

import com.example.mahjong.model.Player;
import com.example.mahjong.model.PlayerResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PromotionServiceTest {

    @Test
    void selectsHighestScoresWithoutMutatingOriginalResults() {
        PromotionService promotionService = new PromotionService();
        List<PlayerResult> results = List.of(
                result(1, 10000),
                result(2, 50000),
                result(3, 30000)
        );

        List<PlayerResult> selected = promotionService.highestScore(results, 2);

        assertEquals(List.of("discord-2", "discord-3"), discordIds(selected));
        assertEquals(List.of("discord-1", "discord-2", "discord-3"), discordIds(results));
    }

    @Test
    void rejectsDuplicatePlayers() {
        PromotionService promotionService = new PromotionService();
        Player player = new Player("discord-1", "玩家1");

        assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.ensureUniquePlayers(List.of(player, player), "重複玩家")
        );
    }

    private PlayerResult result(int number, int score) {
        return new PlayerResult(new Player("discord-" + number, "玩家" + number), score);
    }

    private List<String> discordIds(List<PlayerResult> results) {
        return results.stream()
                .map(result -> result.getPlayer().getDiscordId())
                .toList();
    }
}
