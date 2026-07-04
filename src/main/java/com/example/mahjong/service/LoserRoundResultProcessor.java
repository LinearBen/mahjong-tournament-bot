package com.example.mahjong.service;

import com.example.mahjong.model.MatchTable;
import com.example.mahjong.model.Player;
import com.example.mahjong.model.PlayerResult;
import com.example.mahjong.model.Round;
import com.example.mahjong.model.RoundType;
import com.example.mahjong.model.TableResult;
import com.example.mahjong.model.TournamentState;

import java.util.ArrayList;
import java.util.List;

/**
 * 處理敗部單輪結果。
 */
public class LoserRoundResultProcessor implements RoundProcessor<LoserRoundResultProcessor.Result> {

    private final TableGenerator tableGenerator;
    private final PromotionService promotionService;

    public LoserRoundResultProcessor(TableGenerator tableGenerator, PromotionService promotionService) {
        this.tableGenerator = tableGenerator;
        this.promotionService = promotionService;
    }

    public LoserRoundResultProcessor(TableGenerator tableGenerator) {
        this(tableGenerator, new PromotionService());
    }

    public LoserRoundResultProcessor() {
        this(new TableGenerator());
    }

    @Override
    public TournamentState state() {
        return TournamentState.LOSER;
    }

    @Override
    public Result process(List<TableResult> results) {
        return process(results, List.of());
    }

    public Result process(List<TableResult> results, List<Player> droppedPlayers) {
        if (results.isEmpty()) {
            throw new IllegalArgumentException("敗部結果不可為空");
        }

        int roundNumber = results.get(0).getTable().getRoundKey().getRoundNumber();
        if (roundNumber < 1 || roundNumber > 5) {
            throw new IllegalArgumentException("只能處理敗部R1到R5");
        }
        if (results.size() != expectedTableCount(roundNumber)) {
            throw new IllegalArgumentException("敗部R" + roundNumber + "結果桌數不正確");
        }
        if (droppedPlayers.size() != expectedDroppedPlayerCount(roundNumber)) {
            throw new IllegalArgumentException("敗部R" + roundNumber + "接收勝部掉落人數不正確");
        }

        List<Player> promotedPlayers = new ArrayList<>();
        List<PlayerResult> bottomResults = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>(droppedPlayers);

        for (TableResult result : results) {
            Round round = result.getTable().getRoundKey();
            if (round.getType() != RoundType.LOSER || round.getRoundNumber() != roundNumber) {
                throw new IllegalArgumentException("只能處理同一輪敗部結果");
            }

            promotedPlayers.addAll(promotionService.topTwo(result));
            bottomResults.addAll(result.bottomTwo());
            allPlayers.addAll(promotionService.toPlayers(result.getRanking()));
        }

        List<Player> revivedPlayers = roundNumber == 1
                ? promotionService.toPlayers(promotionService.highestScore(bottomResults, 2))
                : List.of();
        promotedPlayers.addAll(revivedPlayers);

        List<Player> eliminatedPlayers = promotionService.toPlayers(bottomResults);
        eliminatedPlayers.removeAll(revivedPlayers);

        List<Player> nextLoserPlayers = new ArrayList<>(promotedPlayers);
        nextLoserPlayers.addAll(droppedPlayers);
        promotionService.ensureUniquePlayers(allPlayers, "敗部結果含有重複玩家");

        List<MatchTable> nextLoserRoundTables = roundNumber < 5
                ? tableGenerator.generate(new Round(RoundType.LOSER, roundNumber + 1), nextLoserPlayers)
                : List.of();

        return new Result(
                roundNumber,
                promotedPlayers,
                revivedPlayers,
                eliminatedPlayers,
                roundNumber == 5 ? promotedPlayers : List.of(),
                nextLoserRoundTables
        );
    }

    private int expectedTableCount(int roundNumber) {
        return switch (roundNumber) {
            case 1 -> 3;
            case 2 -> 4;
            case 3 -> 3;
            case 4 -> 2;
            case 5 -> 1;
            default -> throw new IllegalArgumentException("只能處理敗部R1到R5");
        };
    }

    private int expectedDroppedPlayerCount(int roundNumber) {
        return switch (roundNumber) {
            case 1 -> 8;
            case 2 -> 4;
            case 3 -> 2;
            case 4, 5 -> 0;
            default -> throw new IllegalArgumentException("只能處理敗部R1到R5");
        };
    }

    public static class Result {

        private final int roundNumber;
        private final List<Player> promotedPlayers;
        private final List<Player> revivedPlayers;
        private final List<Player> eliminatedPlayers;
        private final List<Player> finalPlayers;
        private final List<MatchTable> nextLoserRoundTables;

        public Result(
                int roundNumber,
                List<Player> promotedPlayers,
                List<Player> revivedPlayers,
                List<Player> eliminatedPlayers,
                List<Player> finalPlayers,
                List<MatchTable> nextLoserRoundTables
        ) {
            this.roundNumber = roundNumber;
            this.promotedPlayers = new ArrayList<>(promotedPlayers);
            this.revivedPlayers = new ArrayList<>(revivedPlayers);
            this.eliminatedPlayers = new ArrayList<>(eliminatedPlayers);
            this.finalPlayers = new ArrayList<>(finalPlayers);
            this.nextLoserRoundTables = new ArrayList<>(nextLoserRoundTables);
        }

        public int getRoundNumber() {
            return roundNumber;
        }

        public List<Player> getPromotedPlayers() {
            return new ArrayList<>(promotedPlayers);
        }

        public List<Player> getRevivedPlayers() {
            return new ArrayList<>(revivedPlayers);
        }

        public List<Player> getEliminatedPlayers() {
            return new ArrayList<>(eliminatedPlayers);
        }

        public List<Player> getFinalPlayers() {
            return new ArrayList<>(finalPlayers);
        }

        public List<MatchTable> getNextLoserRoundTables() {
            return new ArrayList<>(nextLoserRoundTables);
        }
    }
}
