package me.universi.question.builder;

import me.universi.feedback.entities.Feedback;
import me.universi.question.entities.Question;

import java.util.UUID;

public class QuestionBuilder {
    public static Question createQuestion(){
        Question question = new Question();
        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        question.setId(uuid_1);
        question.setTitle("Question 1");
        Feedback feedback = new Feedback("www.google.com","feedback test", question);
        question.setFeedback(feedback);
        return question;
    }
    public static Question createQuestion(UUID id){
        Question question = new Question();
        question.setId(id);
        question.setTitle("Question 1");
        Feedback feedback = new Feedback("www.google.com","feedback test", question);
        question.setFeedback(feedback);
        return question;
    }
}
