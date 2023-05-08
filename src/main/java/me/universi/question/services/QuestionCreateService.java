package me.universi.question.services;


import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.entities.Question;

@FunctionalInterface
public interface QuestionCreateService {
    Question createQuestion(Long groupId, Long exerciseId, QuestionCreateDTO questionCreateDTO);
}
