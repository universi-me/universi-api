package me.universi.question.services;


import me.universi.question.entities.Question;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface ListQuestionService {
    List<Question> listQuestion(UUID groupId, UUID exerciseId);
}
