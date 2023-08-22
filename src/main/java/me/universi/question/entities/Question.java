package me.universi.question.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.CascadeType;
import me.universi.exercise.entities.Exercise;
import me.universi.feedback.entities.Feedback;
import me.universi.profile.entities.Profile;
import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.dto.QuestionDTO;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "question")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 4044714151661426179L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @NotBlank(message = "title not blank")
    @NotNull(message = "title not null")
    @Size(min = 15, max = 512)
    private String title;

    @NotNull(message = "Profile is mandatory")
    @JoinColumn(name = "profile_id")
    @ManyToOne
    private Profile profileCreate;

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    @ManyToMany(mappedBy = "questions", cascade = CascadeType.REFRESH)
    @JsonBackReference
    private List<Exercise> exercises;

    public Question() {
    }

    public Question(UUID id, String title, Profile profileCreate, Feedback feedback, List<Exercise> exercises) {
        this.id = id;
        this.title = title;
        this.profileCreate = profileCreate;
        this.feedback = feedback;
        this.exercises = exercises;
    }

    public Question(String title, Profile profileCreate, Feedback feedback) {
        this.title = title;
        this.profileCreate = profileCreate;
        this.feedback = feedback;
    }

    public static Question from(QuestionDTO questionDTO) {
        return new Question(
                questionDTO.getId(),
                questionDTO.getTitle(),
                questionDTO.getProfileCreate(),
                questionDTO.getFeedback(),
                questionDTO.getExercises());
    }

    public static Question from(QuestionCreateDTO questionDTO) {
        return new Question(
                questionDTO.getTitle(),
                questionDTO.getProfileCreate(),
                questionDTO.getFeedback());
    }

    public UUID getId() {
        return id;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Question question = (Question) o;
        return id != null && Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
