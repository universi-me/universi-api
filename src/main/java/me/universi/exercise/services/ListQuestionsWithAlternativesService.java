package me.universi.exercise.services;


import me.universi.exercise.dto.QuestionWithAlternativesDTO;

import java.util.List;

@FunctionalInterface
public interface ListQuestionsWithAlternativesService {
    List<QuestionWithAlternativesDTO> getQuestionsWithAlternatives(Long groupId, Long exerciseId, int amount);
}
