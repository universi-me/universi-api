package me.universi.exercise.services;


import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;

import java.util.List;

@FunctionalInterface
public interface ValuerExerciseService {
    ExerciseAnswersDTO simulatedAnswers(Long userId, List<AnswerDTO> answers);
}
