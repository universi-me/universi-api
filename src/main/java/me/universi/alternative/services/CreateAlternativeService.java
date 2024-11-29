package me.universi.alternative.services;

import me.universi.alternative.dto.AlternativeCreateDTO;
import me.universi.alternative.entities.Alternative;

import java.util.UUID;

@FunctionalInterface
public interface CreateAlternativeService {
    Alternative createAlternative(UUID groupId, UUID exerciseId, UUID questionId, AlternativeCreateDTO alternative);
}
