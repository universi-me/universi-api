package me.universi.exercise.services;


import me.universi.exercise.dto.QuestionWithAlternativesDTO;

import java.util.List;

@FunctionalInterface
public interface GetExerciseService {
    List<QuestionWithAlternativesDTO> getQuestionsWithAlternatives(int amount);
}
