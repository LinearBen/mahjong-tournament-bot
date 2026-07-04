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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WinnerRoundResultProcessorTest {

    @Test
    void advancesWinnerRoundOneAndDropsBottomTwoToLoserRoundTwo() {
        WinnerRoundResultProcessor processor = new WinnerRoundResultProcessor();
        List<TableResult> results = winnerResults(1, 4);

        WinnerRoundResultProcessor.Result result = processor.process(results);

        assertEquals(1, result.getRoundNumber());
        assertEquals(8, result.getWinnerPlayers().size());
        assertEquals(8, result.getDroppedPlayers().size());
        assertEquals(List.of(), result.getFinalPlayers());
        assertEquals(RoundType.LOSER, result.getDroppedToRound().getType());
        assertEquals(2, result.getDroppedToRound().getRoundNumber());

        assertEquals(List.of(
                "discord-1", "discord-2",
                "discord-5", "discord-6",
                "discord-9", "discord-10",
                "discord-13", "discord-14"
        ), discordIds(result.getWinnerPlayers()));
        assertEquals(List.of(
                "discord-3", "discord-4",
                "discord-7", "discord-8",
                "discord-11", "discord-12",
                "discord-15", "discord-16"
        ), discordIds(result.getDroppedPlayers()));

        assertEquals(2, result.getNextWinnerRoundTables().size());
        assertRound(result.getNextWinnerRoundTables(), RoundType.WINNER, 2);
        assertAssignedPlayers(result.getWinnerPlayers(), result.getNextWinnerRoundTables());
    }

    @Test
    void sendsWinnerRoundThreeTopTwoToFinal() {
        WinnerRoundResultProcessor processor = new WinnerRoundResultProcessor();
        List<TableResult> results = winnerResults(3, 1);

        WinnerRoundResultProcessor.Result result = processor.process(results);

        assertEquals(List.of("discord-1", "discord-2"), discordIds(result.getWinnerPlayers()));
        assertEquals(List.of("discord-1", "discord-2"), discordIds(result.getFinalPlayers()));
        assertEquals(List.of("discord-3", "discord-4"), discordIds(result.getDroppedPlayers()));
        assertEquals(RoundType.LOSER, result.getDroppedToRound().getType());
        assertEquals(4, result.getDroppedToRound().getRoundNumber());
        assertTrue(result.getNextWinnerRoundTables().isEmpty());
    }

    @Test
    void rejectsNonWinnerResults() {
        WinnerRoundResultProcessor processor = new WinnerRoundResultProcessor();
        List<TableResult> results = winnerResults(1, 4);
        results.set(0, tableResult(new Round(RoundType.LOSER, 1), "A", 1));

        assertThrows(IllegalArgumentException.class, () -> processor.process(results));
    }

    @Test
    void rejectsWrongTableCountForRound() {
        WinnerRoundResultProcessor processor = new WinnerRoundResultProcessor();
        List<TableResult> results = winnerResults(2, 1);

        assertThrows(IllegalArgumentException.class, () -> processor.process(results));
    }

    @Test
    void rejectsDuplicatePlayers() {
        WinnerRoundResultProcessor processor = new WinnerRoundResultProcessor();
        List<TableResult> results = winnerResults(1, 4);
        results.set(1, tableResult(new Round(RoundType.WINNER, 1), "B", 1));

        assertThrows(IllegalArgumentException.class, () -> processor.process(results));
    }

    private List<TableResult> winnerResults(int roundNumber, int tableCount) {
        List<TableResult> results = new ArrayList<>();
        Round round = new Round(RoundType.WINNER, roundNumber);
        for (int table = 0; table < tableCount; table++) {
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
                new PlayerResult(players.get(2), 20000),
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
    }

    private List<String> discordIds(List<Player> players) {
        List<String> ids = new ArrayList<>();
        for (Player player : players) {
            ids.add(player.getDiscordId());
        }
        return ids;
    }
}
