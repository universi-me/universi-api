package me.universi.alternative.services;

import me.universi.alternative.dto.AlternativeCreateDTO;
import me.universi.alternative.entities.Alternative;

@FunctionalInterface
public interface CreateAlternativeService {
    Alternative createAlternative(Long groupId, Long exerciseId, Long questionId, AlternativeCreateDTO alternative);
}
