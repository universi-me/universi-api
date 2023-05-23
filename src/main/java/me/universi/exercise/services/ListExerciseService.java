package me.universi.exercise.services;

import me.universi.exercise.entities.Exercise;

import java.util.List;

@FunctionalInterface
public interface ListExerciseService {
    List<Exercise> listExercise(Long groupId);
}
