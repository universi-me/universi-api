package me.universi.question.builder;

import me.universi.feedback.entities.Feedback;
import me.universi.question.entities.Question;

public class QuestionBuilder {
    public static Question createQuestion(){
        Question question = new Question();
        question.setId(1L);
        question.setTitle("Question 1");
        Feedback feedback = new Feedback("www.google.com","feedback test", question);
        question.setFeedback(feedback);
        return question;
    }
}
