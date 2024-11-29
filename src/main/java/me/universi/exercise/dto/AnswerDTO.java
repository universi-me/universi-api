package me.universi.exercise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.universi.question.entities.Question;

import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class AnswerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7850448750856935447L;

    private Question question;

    public AnswerDTO(Question question) {
        this.question = question;
    }

    public AnswerDTO() {
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
