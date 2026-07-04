package com.example.mahjong.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerResult {

    private final Player player;

    private final int score;
}