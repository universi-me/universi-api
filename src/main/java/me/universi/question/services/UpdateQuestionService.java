package me.universi.question.services;


import me.universi.question.dto.QuestionUpdateDTO;
import me.universi.question.entities.Question;

@FunctionalInterface
public interface UpdateQuestionService {
    Question updateQuestion(Long userId, Long questionId, QuestionUpdateDTO questionUpdateDto);
}
