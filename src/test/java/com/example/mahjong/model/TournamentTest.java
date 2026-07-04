package com.example.mahjong.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TournamentTest {

    @Test
    void startsFromPreliminaryState() {
        Tournament tournament = new Tournament(List.of(
                new Player("discord-1", "玩家1"),
                new Player("discord-2", "玩家2")
        ));

        assertEquals(TournamentState.PRELIMINARY, tournament.getState());
    }

    @Test
    void canAdvanceToNextState() {
        Tournament tournament = new Tournament(List.of());

        tournament.advanceTo(TournamentState.WINNER);

        assertEquals(TournamentState.WINNER, tournament.getState());
    }
}
