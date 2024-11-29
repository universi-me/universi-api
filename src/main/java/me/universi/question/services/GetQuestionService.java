package me.universi.question.services;


import me.universi.question.entities.Question;

import java.util.UUID;

@FunctionalInterface
public interface GetQuestionService {
    Question getQuestion(UUID groupId, UUID exerciseId, UUID questionId);
}
