package me.universi.indicators.services;

import me.universi.indicators.entities.Indicators;

import java.util.List;

@FunctionalInterface
public interface GetRakingScoreService {
    List<Indicators> getRankingIndicators(Long userId);
}
