package me.universi.exercise.services;

import me.universi.question.entities.Question;

@FunctionalInterface
public interface AddQuestionExerciseService {
    void addQuestion(Long exerciseId, Question question);
}
