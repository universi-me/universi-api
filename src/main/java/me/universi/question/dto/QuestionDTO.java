package me.universi.question.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.universi.exercise.entities.Exercise;
import me.universi.feedback.entities.Feedback;
import me.universi.profile.entities.Profile;
import me.universi.question.entities.Question;
import me.universi.user.entities.User;
import org.springframework.security.core.parameters.P;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class QuestionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4614619593410000375L;
    private UUID id;

    @NotBlank(message = "title not blank")
    @NotNull(message = "title not null")
    @Size(min = 15, max = 512)
    private String title;

    @NotNull(message = "user is mandatory")
    private Profile profileCreate;

    private Feedback feedback;

    private List<Exercise> exercises;


    public QuestionDTO(UUID id, String title, Profile profileCreate, Feedback feedback, List<Exercise> exercises) {
        this.id = id;
        this.title = title;
        this.profileCreate = profileCreate;
        this.feedback = feedback;
        this.exercises = exercises;
    }

    public static QuestionDTO from (Question question){
        return new QuestionDTO(
                question.getId(),
                question.getTitle(),
                question.getProfileCreate(),
                question.getFeedback(),
                question.getExercises());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Profile getProfileCreate() {
        return profileCreate;
    }

    public void setProfileCreate(Profile profileCreate) {
        this.profileCreate = profileCreate;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}

