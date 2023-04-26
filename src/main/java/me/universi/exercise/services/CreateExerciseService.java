package me.universi.exercise.services;

import me.universi.exercise.entities.Exercise;

@FunctionalInterface
public interface CreateExerciseService {
    Exercise createExercise(Long groupId, Exercise exercise);
}
