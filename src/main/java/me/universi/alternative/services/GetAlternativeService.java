package me.universi.alternative.services;


import me.universi.alternative.entities.Alternative;

@FunctionalInterface
public interface GetAlternativeService {
    Alternative getAlternative(Long userId, Long questionId, Long id);
}