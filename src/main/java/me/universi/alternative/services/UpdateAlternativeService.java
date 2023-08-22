package me.universi.alternative.services;

import me.universi.alternative.dto.AlternativeUpdateDTO;
import me.universi.alternative.entities.Alternative;

import java.util.UUID;

@FunctionalInterface
public interface UpdateAlternativeService {
    Alternative updateAlternative(UUID groupId, UUID exerciseId, UUID questionId, UUID alternativeId, AlternativeUpdateDTO alternativeUpdateDTO);
}
