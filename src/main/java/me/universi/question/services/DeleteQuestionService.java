package me.universi.question.services;

@FunctionalInterface
public interface DeleteQuestionService {
    void deleteQuestion(Long userId, Long questionId);
}
