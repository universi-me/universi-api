package me.universi.indicators.services;

import me.universi.indicators.entities.Indicators;

@FunctionalInterface
public interface GetIndicatorsService {
    Indicators getIndicators(Long userId);
}
