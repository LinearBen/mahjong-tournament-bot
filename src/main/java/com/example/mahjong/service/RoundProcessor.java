package com.example.mahjong.service;

import com.example.mahjong.model.TableResult;
import com.example.mahjong.model.TournamentState;

import java.util.List;

/**
 * 單一階段的賽果處理器。
 */
public interface RoundProcessor<R> {

    TournamentState state();

    R process(List<TableResult> results);
}
