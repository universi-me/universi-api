package me.universi.question.services;


import me.universi.question.dto.QuestionUpdateDTO;
import me.universi.question.entities.Question;

import java.util.UUID;

@FunctionalInterface
public interface UpdateQuestionService {
    Question updateQuestion(UUID exerciseId, UUID groupId, UUID questionId, QuestionUpdateDTO questionUpdateDto);
}
