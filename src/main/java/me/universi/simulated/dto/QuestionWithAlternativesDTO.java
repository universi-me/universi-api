package me.universi.simulated.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.universi.alternative.entities.Alternative;
import me.universi.question.entities.Question;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class QuestionWithAlternativesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8635877452433009323L;

    private Question question;

    private List<Alternative> alternatives = new ArrayList<>();

    public QuestionWithAlternativesDTO() {
    }

    public QuestionWithAlternativesDTO(Question question, List<Alternative> alternatives) {
        this.question = question;
        this.alternatives = alternatives;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }
}
