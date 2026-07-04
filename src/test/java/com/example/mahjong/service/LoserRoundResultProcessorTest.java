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

class LoserRoundResultProcessorTest {

    @Test
    void advancesLoserRoundOneWithTwoRevivedPlayersAndWinnerDrops() {
        LoserRoundResultProcessor processor = new LoserRoundResultProcessor();
        List<TableResult> results = loserResults(1, 3);
        List<Player> droppedPlayers = players(101, 8);

        LoserRoundResultProcessor.Result result = processor.process(results, droppedPlayers);

        assertEquals(1, result.getRoundNumber());
        assertEquals(8, result.getPromotedPlayers().size());
        assertEquals(2, result.getRevivedPlayers().size());
        assertEquals(4, result.getEliminatedPlayers().size());
        assertEquals(List.of(), result.getFinalPlayers());

        assertEquals(List.of(
                "discord-1", "discord-2",
                "discord-5", "discord-6",
                "discord-9", "discord-10",
                "discord-3", "discord-7"
        ), discordIds(result.getPromotedPlayers()));
        assertEquals(List.of("discord-3", "discord-7"), discordIds(result.getRevivedPlayers()));
        assertEquals(List.of("discord-4", "discord-8", "discord-11", "discord-12"), discordIds(result.getEliminatedPlayers()));

        assertEquals(4, result.getNextLoserRoundTables().size());
        assertRound(result.getNextLoserRoundTables(), RoundType.LOSER, 2);

        List<Player> expectedNextRoundPlayers = new ArrayList<>(result.getPromotedPlayers());
        expectedNextRoundPlayers.addAll(droppedPlayers);
        assertAssignedPlayers(expectedNextRoundPlayers, result.getNextLoserRoundTables());
    }

    @Test
    void advancesLoserRoundTwoWithWinnerDrops() {
        LoserRoundResultProcessor processor = new LoserRoundResultProcessor();
        List<TableResult> results = loserResults(2, 4);
        List<Player> droppedPlayers = players(101, 4);

        LoserRoundResultProcessor.Result result = processor.process(results, droppedPlayers);

        assertEquals(8, result.getPromotedPlayers().size());
        assertEquals(List.of(), result.getRevivedPlayers());
        assertEquals(8, result.getEliminatedPlayers().size());
        assertEquals(3, result.getNextLoserRoundTables().size());
        assertRound(result.getNextLoserRoundTables(), RoundType.LOSER, 3);

        List<Player> expectedNextRoundPlayers = new ArrayList<>(result.getPromotedPlayers());
        expectedNextRoundPlayers.addAll(droppedPlayers);
        assertAssignedPlayers(expectedNextRoundPlayers, result.getNextLoserRoundTables());
    }

    @Test
    void sendsLoserRoundFiveTopTwoToFinal() {
        LoserRoundResultProcessor processor = new LoserRoundResultProcessor();
        List<TableResult> results = loserResults(5, 1);

        LoserRoundResultProcessor.Result result = processor.process(results);

        assertEquals(List.of("discord-1", "discord-2"), discordIds(result.getPromotedPlayers()));
        assertEquals(List.of("discord-1", "discord-2"), discordIds(result.getFinalPlayers()));
        assertEquals(List.of("discord-3", "discord-4"), discordIds(result.getEliminatedPlayers()));
        assertTrue(result.getNextLoserRoundTables().isEmpty());
    }

    @Test
    void rejectsNonLoserResults() {
        LoserRoundResultProcessor processor = new LoserRoundResultProcessor();
        List<TableResult> results = loserResults(1, 3);
        results.set(0, tableResult(new Round(RoundType.WINNER, 1), "A", 1));

        assertThrows(IllegalArgumentException.class, () -> processor.process(results, players(101, 8)));
    }

    @Test
    void rejectsWrongTableCountForRound() {
        LoserRoundResultProcessor processor = new LoserRoundResultProcessor();
        List<TableResult> results = loserResults(2, 3);

        assertThrows(IllegalArgumentException.class, () -> processor.process(results, players(101, 4)));
    }

    @Test
    void rejectsWrongWinnerDropCountForRound() {
        LoserRoundResultProcessor processor = new LoserRoundResultProcessor();
        List<TableResult> results = loserResults(3, 3);

        assertThrows(IllegalArgumentException.class, () -> processor.process(results, players(101, 4)));
    }

    @Test
    void rejectsDuplicatePlayers() {
        LoserRoundResultProcessor processor = new LoserRoundResultProcessor();
        List<TableResult> results = loserResults(1, 3);
        results.set(1, tableResult(new Round(RoundType.LOSER, 1), "B", 1));

        assertThrows(IllegalArgumentException.class, () -> processor.process(results, players(101, 8)));
    }

    private List<TableResult> loserResults(int roundNumber, int tableCount) {
        List<TableResult> results = new ArrayList<>();
        Round round = new Round(RoundType.LOSER, roundNumber);
        for (int table = 0; table < tableCount; table++) {
            int firstPlayerNumber = table * 4 + 1;
            results.add(tableResult(round, String.valueOf((char) ('A' + table)), firstPlayerNumber));
        }
        return results;
    }

    private TableResult tableResult(Round round, String tableCode, int firstPlayerNumber) {
        List<Player> players = players(firstPlayerNumber, 4);
        MatchTable table = new MatchTable(round, tableCode, players);

        return new TableResult(table, List.of(
                new PlayerResult(players.get(0), 40000),
                new PlayerResult(players.get(1), 30000),
                new PlayerResult(players.get(2), 100000 - firstPlayerNumber),
                new PlayerResult(players.get(3), 10000)
        ));
    }

    private List<Player> players(int firstPlayerNumber, int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            players.add(player(firstPlayerNumber + i));
        }
        return players;
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
