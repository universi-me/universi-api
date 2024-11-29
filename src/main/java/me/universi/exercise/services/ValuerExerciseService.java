package me.universi.exercise.services;


import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface ValuerExerciseService {
    ExerciseAnswersDTO exercisesAnswers(UUID groupId, UUID exerciseId, List<AnswerDTO> answers);
}
