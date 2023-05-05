package me.universi.exercise.services;

import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.dto.ExerciseDTO;
import me.universi.exercise.entities.Exercise;

@FunctionalInterface
public interface CreateExerciseService {
    Exercise createExercise(Long groupId, ExerciseCreateDTO exercise);
}
