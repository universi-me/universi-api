package me.universi.exercise.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.group.entities.Group;
import me.universi.question.entities.Question;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "exercise")
@SQLDelete(sql = "UPDATE exercise SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Exercise implements Serializable {

    @Serial
    private static final long serialVersionUID = -408946581836369991L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @NotNull
    @NotBlank
    private String title;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="exercise_question",
            joinColumns={@JoinColumn(name="exercise_id")},
            inverseJoinColumns={@JoinColumn(name="question_id")})
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Question> questions;

    @JsonIgnore
    @NotNull(message = "Group is mandatory")
    @JoinColumn(name = "group_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Group group;

    @Column(columnDefinition = "boolean default false")
    @NotFound(action = NotFoundAction.IGNORE)
    private boolean inactivate;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Exercise() {
    }

    public Exercise(UUID id, Group group, String title) {
        this.id = id;
        this.group = group;
        this.title = title;
    }

    public Exercise(String title, Group group) {
        this.title = title;
        this.group = group;
    }

    public static Exercise from (ExerciseCreateDTO exerciseCreateDTO){
        Exercise exercise = new Exercise();
        exercise.setTitle(exerciseCreateDTO.getTitle());
        exercise.setGroup(exerciseCreateDTO.getGroup());
        return exercise;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isInactivate() {
        return inactivate;
    }

    public void setInactivate(boolean inactivate) {
        this.inactivate = inactivate;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise exercise)) return false;
        return Objects.equals(getId(), exercise.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
