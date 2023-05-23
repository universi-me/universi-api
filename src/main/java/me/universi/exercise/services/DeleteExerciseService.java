package me.universi.exercise.services;

@FunctionalInterface
public interface DeleteExerciseService {
    void deleteExercise(Long groupId, Long exerciseId);
}
