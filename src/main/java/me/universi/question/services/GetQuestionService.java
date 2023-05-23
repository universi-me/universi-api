package me.universi.question.services;


import me.universi.question.entities.Question;

@FunctionalInterface
public interface GetQuestionService {
    Question getQuestion(Long groupId, Long exerciseId, Long questionId);
}
