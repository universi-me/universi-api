package me.universi.question.services;


import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.entities.Question;

@FunctionalInterface
public interface CreateQuestionService {
    Question createQuestion(Long userId, QuestionCreateDTO questionCreateDTO);
}
