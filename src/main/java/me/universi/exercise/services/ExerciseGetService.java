package me.universi.exercise.services;

import me.universi.exercise.entities.Exercise;

import java.util.UUID;

@FunctionalInterface
public interface ExerciseGetService {
    Exercise getExercise(UUID exerciseId, UUID groupId);
}
