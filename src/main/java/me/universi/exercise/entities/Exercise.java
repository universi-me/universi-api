package me.universi.exercise.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.group.entities.Group;
import me.universi.question.entities.Question;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
public class Exercise implements Serializable {

    @Serial
    private static final long serialVersionUID = -408946581836369991L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exercise_generator")
    @SequenceGenerator(name = "exercise_generator", sequenceName = "exercise_sequence", allocationSize = 1)
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="exercise_question",
            joinColumns={@JoinColumn(name="exercise_id")},
            inverseJoinColumns={@JoinColumn(name="question_id")})
    private List<Question> questions;

    @JsonIgnore
    @NotNull(message = "Group is mandatory")
    @JoinColumn(name = "group_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Group group;

    public Exercise() {
    }

    public Exercise(Long id, Group group, String title) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
