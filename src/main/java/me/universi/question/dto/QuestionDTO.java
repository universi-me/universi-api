package me.universi.question.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.universi.feedback.entities.Feedback;
import me.universi.question.entities.Question;
import me.universi.usuario.entities.User;

import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class QuestionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4614619593410000375L;
    private Long id;

    @NotBlank(message = "title not blank")
    @NotNull(message = "title not null")
    @Size(min = 15, max = 512)
    private String title;

    @NotNull(message = "user is mandatory")
    private User userCreate;

    private Feedback feedback;

    public QuestionDTO(Long id, String title, User userCreate, Feedback feedback) {
        this.id = id;
        this.title = title;
        this.userCreate = userCreate;
        this.feedback = feedback;
    }

    public static QuestionDTO from (Question question){
        return new QuestionDTO(
                question.getId(),
                question.getTitle(),
                question.getUserCreate(),
                question.getFeedback());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(User userCreate) {
        this.userCreate = userCreate;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}

