package me.universi.exercise.services;


import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;

import java.util.List;

@FunctionalInterface
public interface ValuerExerciseService {
    ExerciseAnswersDTO exercisesAnswers(Long groupId, Long exerciseId, List<AnswerDTO> answers);
}
