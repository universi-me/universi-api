package me.universi.exercise.services;

import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.dto.ExerciseDTO;
import me.universi.exercise.entities.Exercise;

import java.util.UUID;

@FunctionalInterface
public interface CreateExerciseService {
    Exercise createExercise(UUID groupId, ExerciseCreateDTO exercise);
}
