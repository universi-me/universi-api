package me.universi.alternative.services;

import me.universi.alternative.dto.AlternativeUpdateDTO;
import me.universi.alternative.entities.Alternative;

@FunctionalInterface
public interface UpdateAlternativeService {
    Alternative updateAlternative(Long groupId, Long exerciseId, Long questionId, Long alternativeId, AlternativeUpdateDTO alternativeUpdateDTO);
}
