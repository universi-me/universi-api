package me.universi.alternative.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import me.universi.alternative.dto.AlternativeCreateDTO;
import me.universi.question.entities.Question;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "alternative")
@SQLDelete(sql = "UPDATE alternative SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Alternative implements Serializable {

    @Serial
    private static final long serialVersionUID = 1482176771595190928L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @NotBlank(message = "title not blank")
    @NotNull(message = "title not null")
    private String title;

    @NotNull
    @Value("false")
    private Boolean correct;

    @NotNull(message = "Question is mandatory")
    @JoinColumn(name = "question_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private Question question;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Alternative(String title, Boolean correct, Question question) {
        this.title = title;
        this.correct = correct;
        this.question = question;
    }

    public Alternative() {
    }

    public static Alternative from (AlternativeCreateDTO alternativeCreateDTO){
        return new Alternative(
                alternativeCreateDTO.getTitle(),
                alternativeCreateDTO.getCorrect(),
                alternativeCreateDTO.getQuestion());
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Alternative that = (Alternative) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
