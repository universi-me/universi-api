package me.universi.exercise.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="exercise_question",
            joinColumns={@JoinColumn(name="exercise_id")},
            inverseJoinColumns={@JoinColumn(name="question_id")})
    private List<Question> questions;

    public Exercise() {
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
