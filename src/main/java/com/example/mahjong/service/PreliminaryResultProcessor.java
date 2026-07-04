package com.example.mahjong.service;

import com.example.mahjong.model.MatchTable;
import com.example.mahjong.model.Player;
import com.example.mahjong.model.PlayerResult;
import com.example.mahjong.model.Round;
import com.example.mahjong.model.RoundType;
import com.example.mahjong.model.TableResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 處理預賽 7 桌結果，產生勝部R1與敗部R1。
 */
public class PreliminaryResultProcessor {

    private final TableGenerator tableGenerator;

    public PreliminaryResultProcessor(TableGenerator tableGenerator) {
        this.tableGenerator = tableGenerator;
    }

    public PreliminaryResultProcessor() {
        this(new TableGenerator());
    }

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

            winners.addAll(toPlayers(result.topTwo()));
            loserCandidates.addAll(result.bottomTwo());
        }

        loserCandidates.sort(Comparator.comparingInt(PlayerResult::getScore).reversed());

        List<Player> transferred = toPlayers(loserCandidates.subList(0, 2));
        List<Player> losers = toPlayers(loserCandidates.subList(2, loserCandidates.size()));

        winners.addAll(transferred);
        ensureUniquePlayers(winners, losers);

        return new Result(
                winners,
                losers,
                transferred,
                tableGenerator.generate(new Round(RoundType.WINNER, 1), winners),
                tableGenerator.generate(new Round(RoundType.LOSER, 1), losers)
        );
    }

    private List<Player> toPlayers(List<PlayerResult> results) {
        List<Player> players = new ArrayList<>();
        for (PlayerResult result : results) {
            players.add(result.getPlayer());
        }
        return players;
    }

    private void ensureUniquePlayers(List<Player> winners, List<Player> losers) {
        Set<String> ids = new HashSet<>();
        for (Player player : winners) {
            if (!ids.add(player.getDiscordId())) {
                throw new IllegalArgumentException("預賽結果含有重複玩家");
            }
        }
        for (Player player : losers) {
            if (!ids.add(player.getDiscordId())) {
                throw new IllegalArgumentException("預賽結果含有重複玩家");
            }
        }
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
