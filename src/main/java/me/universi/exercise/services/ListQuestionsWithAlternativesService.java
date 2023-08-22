package me.universi.exercise.services;


import me.universi.exercise.dto.QuestionWithAlternativesDTO;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface ListQuestionsWithAlternativesService {
    List<QuestionWithAlternativesDTO> getQuestionsWithAlternatives(UUID groupId, UUID exerciseId, int amount);
}
