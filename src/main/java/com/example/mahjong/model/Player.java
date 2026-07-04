package com.example.mahjong.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Player {

    private final String discordId;

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}