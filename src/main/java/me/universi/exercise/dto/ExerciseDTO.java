package me.universi.exercise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import me.universi.exercise.entities.Exercise;
import me.universi.grupo.entities.Group;
import me.universi.question.entities.Question;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ExerciseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1249376902788845315L;

    private Long id;

    private List<Question> questions;

    @NotNull(message = "Group is mandatory")
    private Group group;

    public ExerciseDTO(Long id, List<Question> questions, Group group) {
        this.id = id;
        this.questions = questions;
        this.group = group;
    }

    public ExerciseDTO(List<Question> questions, Group group) {
        this.questions = questions;
        this.group = group;
    }

    public ExerciseDTO() {
    }

    public ExerciseDTO from (Exercise exercise){
        return new ExerciseDTO(exercise.getId(), exercise.getQuestions(), exercise.getGroup());
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


}
