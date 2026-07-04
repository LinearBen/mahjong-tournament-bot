package com.example.mahjong.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 一場賽事。
 *
 * 目前只先記錄參賽玩家；賽制流程等真的要排輪次時再加。
 */
@Getter
public class Tournament {

    /** 參加這場賽事的玩家。 */
    private final List<Player> players;

    public Tournament(List<Player> players) {
        this.players = new ArrayList<>(players);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
}
