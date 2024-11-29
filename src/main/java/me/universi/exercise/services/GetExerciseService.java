package me.universi.exercise.services;

import me.universi.exercise.entities.Exercise;

import java.util.UUID;

@FunctionalInterface
public interface GetExerciseService {
    Exercise getExercise(UUID groupId, UUID exerciseId);
}
