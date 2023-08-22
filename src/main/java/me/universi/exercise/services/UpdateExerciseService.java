package me.universi.exercise.services;

import me.universi.exercise.dto.ExerciseUpdateDTO;
import me.universi.exercise.entities.Exercise;

import java.util.UUID;

@FunctionalInterface
public interface UpdateExerciseService {
    Exercise updateExercise(UUID groupId, UUID exerciseId, ExerciseUpdateDTO exerciseUpdateDTO);
}
