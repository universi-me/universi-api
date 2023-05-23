package me.universi.question.services;

@FunctionalInterface
public interface DeleteQuestionService {
    void deleteQuestion(Long groupId, Long exerciseId, Long questionId);
}
