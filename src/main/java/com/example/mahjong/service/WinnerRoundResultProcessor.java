package com.example.mahjong.service;

import com.example.mahjong.model.MatchTable;
import com.example.mahjong.model.Player;
import com.example.mahjong.model.Round;
import com.example.mahjong.model.RoundType;
import com.example.mahjong.model.TableResult;
import com.example.mahjong.model.TournamentState;

import java.util.ArrayList;
import java.util.List;

/**
 * 處理勝部單輪結果。
 */
public class WinnerRoundResultProcessor implements RoundProcessor<WinnerRoundResultProcessor.Result> {

    private final TableGenerator tableGenerator;
    private final PromotionService promotionService;

    public WinnerRoundResultProcessor(TableGenerator tableGenerator, PromotionService promotionService) {
        this.tableGenerator = tableGenerator;
        this.promotionService = promotionService;
    }

    public WinnerRoundResultProcessor(TableGenerator tableGenerator) {
        this(tableGenerator, new PromotionService());
    }

    public WinnerRoundResultProcessor() {
        this(new TableGenerator());
    }

    @Override
    public TournamentState state() {
        return TournamentState.WINNER;
    }

    @Override
    public Result process(List<TableResult> results) {
        if (results.isEmpty()) {
            throw new IllegalArgumentException("勝部結果不可為空");
        }

        int roundNumber = results.get(0).getTable().getRoundKey().getRoundNumber();
        if (roundNumber < 1 || roundNumber > 3) {
            throw new IllegalArgumentException("只能處理勝部R1到R3");
        }
        if (results.size() != expectedTableCount(roundNumber)) {
            throw new IllegalArgumentException("勝部R" + roundNumber + "結果桌數不正確");
        }

        List<Player> winners = new ArrayList<>();
        List<Player> droppedPlayers = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>();

        for (TableResult result : results) {
            Round round = result.getTable().getRoundKey();
            if (round.getType() != RoundType.WINNER || round.getRoundNumber() != roundNumber) {
                throw new IllegalArgumentException("只能處理同一輪勝部結果");
            }

            winners.addAll(promotionService.topTwo(result));
            droppedPlayers.addAll(promotionService.toPlayers(result.bottomTwo()));
            allPlayers.addAll(promotionService.toPlayers(result.getRanking()));
        }

        promotionService.ensureUniquePlayers(allPlayers, "勝部結果含有重複玩家");

        List<MatchTable> nextWinnerRoundTables = roundNumber < 3
                ? tableGenerator.generate(new Round(RoundType.WINNER, roundNumber + 1), winners)
                : List.of();

        return new Result(
                roundNumber,
                winners,
                droppedPlayers,
                roundNumber == 3 ? winners : List.of(),
                new Round(RoundType.LOSER, roundNumber + 1),
                nextWinnerRoundTables
        );
    }

    private int expectedTableCount(int roundNumber) {
        return switch (roundNumber) {
            case 1 -> 4;
            case 2 -> 2;
            case 3 -> 1;
            default -> throw new IllegalArgumentException("只能處理勝部R1到R3");
        };
    }

    public static class Result {

        private final int roundNumber;
        private final List<Player> winnerPlayers;
        private final List<Player> droppedPlayers;
        private final List<Player> finalPlayers;
        private final Round droppedToRound;
        private final List<MatchTable> nextWinnerRoundTables;

        public Result(
                int roundNumber,
                List<Player> winnerPlayers,
                List<Player> droppedPlayers,
                List<Player> finalPlayers,
                Round droppedToRound,
                List<MatchTable> nextWinnerRoundTables
        ) {
            this.roundNumber = roundNumber;
            this.winnerPlayers = new ArrayList<>(winnerPlayers);
            this.droppedPlayers = new ArrayList<>(droppedPlayers);
            this.finalPlayers = new ArrayList<>(finalPlayers);
            this.droppedToRound = droppedToRound;
            this.nextWinnerRoundTables = new ArrayList<>(nextWinnerRoundTables);
        }

        public int getRoundNumber() {
            return roundNumber;
        }

        public List<Player> getWinnerPlayers() {
            return new ArrayList<>(winnerPlayers);
        }

        public List<Player> getDroppedPlayers() {
            return new ArrayList<>(droppedPlayers);
        }

        public List<Player> getFinalPlayers() {
            return new ArrayList<>(finalPlayers);
        }

        public Round getDroppedToRound() {
            return droppedToRound;
        }

        public List<MatchTable> getNextWinnerRoundTables() {
            return new ArrayList<>(nextWinnerRoundTables);
        }
    }
}
