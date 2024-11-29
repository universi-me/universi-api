package me.universi.question.services;

import java.util.UUID;

@FunctionalInterface
public interface DeleteQuestionService {
    void deleteQuestion(UUID groupId, UUID exerciseId, UUID questionId);
}
