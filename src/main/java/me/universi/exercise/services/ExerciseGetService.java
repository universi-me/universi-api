package me.universi.exercise.services;

import me.universi.exercise.entities.Exercise;

@FunctionalInterface
public interface ExerciseGetService {
    Exercise getExercise(Long exerciseId, Long groupId);
}
