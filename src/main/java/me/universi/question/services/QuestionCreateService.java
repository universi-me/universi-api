package me.universi.question.services;


import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.entities.Question;

import java.util.UUID;

@FunctionalInterface
public interface QuestionCreateService {
    Question createQuestion(UUID groupId, UUID exerciseId, QuestionCreateDTO questionCreateDTO);
}
