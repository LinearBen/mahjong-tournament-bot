package com.example.mahjong.service;

import com.example.mahjong.model.MatchTable;
import com.example.mahjong.model.Player;
import com.example.mahjong.model.Round;
import com.example.mahjong.model.RoundType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableGeneratorTest {

    @Test
    void generatesTablesWithFourPlayersEach() {
        TableGenerator generator = new TableGenerator();
        Round round = new Round(RoundType.WINNER, 1);
        List<Player> players = players(8);

        List<MatchTable> tables = generator.generate(round, players);

        assertEquals(2, tables.size());
        assertEquals("A", tables.get(0).getTableCode());
        assertEquals("B", tables.get(1).getTableCode());

        List<Player> assignedPlayers = new ArrayList<>();
        for (MatchTable table : tables) {
            assertEquals(round, table.getRoundKey());
            assertEquals(4, table.getPlayers().size());
            assignedPlayers.addAll(table.getPlayers());
        }

        Set<Player> uniqueAssignedPlayers = new HashSet<>(assignedPlayers);
        assertEquals(players.size(), assignedPlayers.size());
        assertEquals(players.size(), uniqueAssignedPlayers.size());
        assertEquals(new HashSet<>(players), uniqueAssignedPlayers);
    }

    @Test
    void rejectsPlayerCountThatIsNotMultipleOfFour() {
        TableGenerator generator = new TableGenerator();
        Round round = new Round(RoundType.PRELIMINARY, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(round, players(5))
        );
    }

    private List<Player> players(int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            players.add(new Player("discord-" + i, "玩家" + i));
        }
        return players;
    }
}
