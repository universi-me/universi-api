package me.universi.exercise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.universi.alternative.entities.Alternative;
import me.universi.question.entities.Question;

import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class AnswerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7850448750856935447L;

    private Question question;

    private Alternative alternativeSelected;

    public AnswerDTO(Question question, Alternative alternativeSelected) {
        this.question = question;
        this.alternativeSelected = alternativeSelected;
    }

    public AnswerDTO() {
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Alternative getAlternativeSelected() {
        return alternativeSelected;
    }

    public void setAlternativeSelected(Alternative alternativeSelected) {
        this.alternativeSelected = alternativeSelected;
    }
}
