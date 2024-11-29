package me.universi.exercise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ExerciseAnswersDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 3519798839224309378L;

    private List<AnswerDTO> answers;

    private float score;

    public ExerciseAnswersDTO(List<AnswerDTO> answers, float score) {
        this.answers = answers;
        this.score = score;
    }

    public ExerciseAnswersDTO() {
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }


}
