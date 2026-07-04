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
 * 處理預賽 7 桌結果，產生勝部R1與敗部R1。
 */
public class PreliminaryResultProcessor implements RoundProcessor<PreliminaryResultProcessor.Result> {

    private final TableGenerator tableGenerator;
    private final PromotionService promotionService;

    public PreliminaryResultProcessor(TableGenerator tableGenerator, PromotionService promotionService) {
        this.tableGenerator = tableGenerator;
        this.promotionService = promotionService;
    }

    public PreliminaryResultProcessor(TableGenerator tableGenerator) {
        this(tableGenerator, new PromotionService());
    }

    public PreliminaryResultProcessor() {
        this(new TableGenerator());
    }

    @Override
    public TournamentState state() {
        return TournamentState.PRELIMINARY;
    }

    @Override
    public Result process(List<TableResult> results) {
        if (results.size() != 7) {
            throw new IllegalArgumentException("預賽結果必須剛好 7 桌");
        }

        List<Player> winners = new ArrayList<>();
        List<PlayerResult> loserCandidates = new ArrayList<>();

        for (TableResult result : results) {
            if (result.getTable().getRoundKey().getType() != RoundType.PRELIMINARY) {
                throw new IllegalArgumentException("只能處理預賽結果");
            }

            winners.addAll(promotionService.topTwo(result));
            loserCandidates.addAll(result.bottomTwo());
        }

        List<PlayerResult> transferredResults = promotionService.highestScore(loserCandidates, 2);
        loserCandidates.removeAll(transferredResults);
        List<Player> transferred = promotionService.toPlayers(transferredResults);
        List<Player> losers = promotionService.toPlayers(loserCandidates);

        winners.addAll(transferred);
        List<Player> promotedPlayers = new ArrayList<>(winners);
        promotedPlayers.addAll(losers);
        promotionService.ensureUniquePlayers(promotedPlayers, "預賽結果含有重複玩家");

        return new Result(
                winners,
                losers,
                transferred,
                tableGenerator.generate(new Round(RoundType.WINNER, 1), winners),
                tableGenerator.generate(new Round(RoundType.LOSER, 1), losers)
        );
    }

    public static class Result {

        private final List<Player> winnerPlayers;
        private final List<Player> loserPlayers;
        private final List<Player> transferredPlayers;
        private final List<MatchTable> winnerRoundOneTables;
        private final List<MatchTable> loserRoundOneTables;

        public Result(
                List<Player> winnerPlayers,
                List<Player> loserPlayers,
                List<Player> transferredPlayers,
                List<MatchTable> winnerRoundOneTables,
                List<MatchTable> loserRoundOneTables
        ) {
            this.winnerPlayers = new ArrayList<>(winnerPlayers);
            this.loserPlayers = new ArrayList<>(loserPlayers);
            this.transferredPlayers = new ArrayList<>(transferredPlayers);
            this.winnerRoundOneTables = new ArrayList<>(winnerRoundOneTables);
            this.loserRoundOneTables = new ArrayList<>(loserRoundOneTables);
        }

        public List<Player> getWinnerPlayers() {
            return new ArrayList<>(winnerPlayers);
        }

        public List<Player> getLoserPlayers() {
            return new ArrayList<>(loserPlayers);
        }

        public List<Player> getTransferredPlayers() {
            return new ArrayList<>(transferredPlayers);
        }

        public List<MatchTable> getWinnerRoundOneTables() {
            return new ArrayList<>(winnerRoundOneTables);
        }

        public List<MatchTable> getLoserRoundOneTables() {
            return new ArrayList<>(loserRoundOneTables);
        }
    }
}
