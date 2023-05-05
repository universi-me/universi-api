package me.universi.exercise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.exercise.entities.Exercise;
import me.universi.grupo.entities.Group;
import me.universi.question.entities.Question;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ExerciseCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5469813469325773698L;

    private List<Question> questions;

    private Group group;

    @NotNull
    @NotBlank
    private String title;

    public ExerciseCreateDTO(List<Question> questions, Group group) {
        this.questions = questions;
        this.group = group;
    }

    public ExerciseCreateDTO() {
    }

    public ExerciseCreateDTO(Group group) {
        this.group = group;
    }

    public ExerciseCreateDTO(String title) {
        this.title = title;
    }

    public ExerciseCreateDTO from (Exercise exercise){
        return new ExerciseCreateDTO(exercise.getQuestions(), exercise.getGroup());
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
