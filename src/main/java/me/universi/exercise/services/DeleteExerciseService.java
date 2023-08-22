package me.universi.exercise.services;

import java.util.UUID;

@FunctionalInterface
public interface DeleteExerciseService {
    void deleteExercise(UUID groupId, UUID exerciseId);
}
