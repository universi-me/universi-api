package me.universi.exercise.services;

import me.universi.exercise.entities.Exercise;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface ListExerciseService {
    List<Exercise> listExercise(UUID groupId);
}
