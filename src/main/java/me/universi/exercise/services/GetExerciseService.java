package me.universi.exercise.services;

import me.universi.exercise.entities.Exercise;

@FunctionalInterface
public interface GetExerciseService {
    Exercise getExercise(Long groupId, Long exerciseId);
}
