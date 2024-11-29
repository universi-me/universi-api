package me.universi.alternative.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import me.universi.question.entities.Question;
import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class AlternativeCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3996486921183317646L;

    @NotBlank(message = "title not blank")
    @NotNull(message = "title not null")
    private String title;

    @NotNull
    @Value("false")
    private Boolean correct;

    private Question question;

    public AlternativeCreateDTO() {
    }

    public AlternativeCreateDTO(String title, Boolean correct, Question question) {
        this.title = title;
        this.correct = correct;
        this.question = question;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
