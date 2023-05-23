package me.universi.question.services;


import me.universi.question.entities.Question;

import java.util.List;

@FunctionalInterface
public interface ListQuestionService {
    List<Question> listQuestion(Long groupId, Long exerciseId);
}
