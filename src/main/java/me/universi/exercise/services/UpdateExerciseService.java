package me.universi.exercise.services;

import me.universi.exercise.dto.ExerciseUpdateDTO;
import me.universi.exercise.entities.Exercise;

@FunctionalInterface
public interface UpdateExerciseService {
    Exercise updateExercise(Long groupId, Long exerciseId, ExerciseUpdateDTO exerciseUpdateDTO);
}
