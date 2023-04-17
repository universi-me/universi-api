package me.universi.question.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import me.universi.feedback.entities.Feedback;
import me.universi.question.dto.QuestionDTO;
import me.universi.question.dto.QuestionCreateDTO;
import me.universi.user.entities.User;
import org.hibernate.Hibernate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 4044714151661426179L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_generator")
    @SequenceGenerator(name = "question_generator", sequenceName = "question_sequence", allocationSize = 1)
    private Long id;

    @NotBlank(message = "title not blank")
    @NotNull(message = "title not null")
    @Size(min = 15, max = 512)
    private String title;

    @NotNull(message = "user is mandatory")
    @JoinColumn(name = "user_create_id")
    @ManyToOne
    private User userCreate;

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    public Question() {
    }

    public Question(Long id, String title, User userCreate, Feedback feedback) {
        this.id = id;
        this.title = title;
        this.userCreate = userCreate;
        this.feedback = feedback;
    }

    public Question(String title, User userCreate, Feedback feedback) {
        this.title = title;
        this.userCreate = userCreate;
        this.feedback = feedback;
    }

    public static Question from(QuestionDTO questionDTO) {
        return new Question(
                questionDTO.getId(),
                questionDTO.getTitle(),
                questionDTO.getUserCreate(),
                questionDTO.getFeedback());
    }

    public static Question from(QuestionCreateDTO questionDTO) {
        return new Question(
                questionDTO.getTitle(),
                questionDTO.getUserCreate(),
                questionDTO.getFeedback());
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
