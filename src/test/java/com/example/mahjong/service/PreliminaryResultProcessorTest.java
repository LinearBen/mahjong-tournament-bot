package com.example.mahjong.service;

import com.example.mahjong.model.MatchTable;
import com.example.mahjong.model.Player;
import com.example.mahjong.model.PlayerResult;
import com.example.mahjong.model.Round;
import com.example.mahjong.model.RoundType;
import com.example.mahjong.model.TableResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreliminaryResultProcessorTest {

    @Test
    void movesTopTwoLoserCandidatesToWinnerRoundOne() {
        PreliminaryResultProcessor processor = new PreliminaryResultProcessor();
        List<TableResult> results = preliminaryResults();

        PreliminaryResultProcessor.Result result = processor.process(results);

        assertEquals(16, result.getWinnerPlayers().size());
        assertEquals(12, result.getLoserPlayers().size());
        assertEquals(2, result.getTransferredPlayers().size());

        assertEquals(List.of("discord-3", "discord-7"), discordIds(result.getTransferredPlayers()));
        assertTrue(discordIds(result.getWinnerPlayers()).containsAll(List.of("discord-3", "discord-7")));
        assertTrue(discordIds(result.getLoserPlayers()).contains("discord-11"));
        assertTrue(discordIds(result.getLoserPlayers()).contains("discord-15"));

        assertEquals(4, result.getWinnerRoundOneTables().size());
        assertEquals(3, result.getLoserRoundOneTables().size());
        assertRound(result.getWinnerRoundOneTables(), RoundType.WINNER, 1);
        assertRound(result.getLoserRoundOneTables(), RoundType.LOSER, 1);
        assertAssignedPlayers(result.getWinnerPlayers(), result.getWinnerRoundOneTables());
        assertAssignedPlayers(result.getLoserPlayers(), result.getLoserRoundOneTables());
    }

    @Test
    void rejectsNonPreliminaryResults() {
        PreliminaryResultProcessor processor = new PreliminaryResultProcessor();
        List<TableResult> results = preliminaryResults();
        results.set(0, tableResult(new Round(RoundType.WINNER, 1), "A", 1));

        assertThrows(IllegalArgumentException.class, () -> processor.process(results));
    }

    @Test
    void rejectsPreliminaryResultsThatAreNotSevenTables() {
        PreliminaryResultProcessor processor = new PreliminaryResultProcessor();
        List<TableResult> results = preliminaryResults();
        results.remove(6);

        assertThrows(IllegalArgumentException.class, () -> processor.process(results));
    }

    @Test
    void rejectsDuplicatePlayers() {
        PreliminaryResultProcessor processor = new PreliminaryResultProcessor();
        List<TableResult> results = preliminaryResults();
        results.set(1, tableResult(new Round(RoundType.PRELIMINARY, 0), "B", 1));

        assertThrows(IllegalArgumentException.class, () -> processor.process(results));
    }

    private List<TableResult> preliminaryResults() {
        List<TableResult> results = new ArrayList<>();
        Round round = new Round(RoundType.PRELIMINARY, 0);
        for (int table = 0; table < 7; table++) {
            int firstPlayerNumber = table * 4 + 1;
            results.add(tableResult(round, String.valueOf((char) ('A' + table)), firstPlayerNumber));
        }
        return results;
    }

    private TableResult tableResult(Round round, String tableCode, int firstPlayerNumber) {
        List<Player> players = List.of(
                player(firstPlayerNumber),
                player(firstPlayerNumber + 1),
                player(firstPlayerNumber + 2),
                player(firstPlayerNumber + 3)
        );
        MatchTable table = new MatchTable(round, tableCode, players);

        return new TableResult(table, List.of(
                new PlayerResult(players.get(0), 40000),
                new PlayerResult(players.get(1), 30000),
                new PlayerResult(players.get(2), 100000 - firstPlayerNumber),
                new PlayerResult(players.get(3), 10000)
        ));
    }

    private Player player(int number) {
        return new Player("discord-" + number, "玩家" + number);
    }

    private void assertRound(List<MatchTable> tables, RoundType type, int roundNumber) {
        for (MatchTable table : tables) {
            assertEquals(type, table.getRoundKey().getType());
            assertEquals(roundNumber, table.getRoundKey().getRoundNumber());
            assertEquals(4, table.getPlayers().size());
        }
    }

    private void assertAssignedPlayers(List<Player> expectedPlayers, List<MatchTable> tables) {
        List<Player> assignedPlayers = new ArrayList<>();
        for (MatchTable table : tables) {
            assignedPlayers.addAll(table.getPlayers());
        }

        assertEquals(expectedPlayers.size(), assignedPlayers.size());
        assertEquals(new HashSet<>(discordIds(expectedPlayers)), new HashSet<>(discordIds(assignedPlayers)));
        assertEquals(expectedPlayers.size(), new HashSet<>(assignedPlayers).size());
    }

    private List<String> discordIds(List<Player> players) {
        List<String> ids = new ArrayList<>();
        for (Player player : players) {
            ids.add(player.getDiscordId());
        }
        return ids;
    }
}
