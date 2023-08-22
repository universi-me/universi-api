package me.universi.exercise.services;

import me.universi.question.entities.Question;

import java.util.UUID;

@FunctionalInterface
public interface AddQuestionExerciseService {
    void addQuestion(UUID exerciseId, Question question);
}
