package me.universi.feedback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.universi.question.entities.Question;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.URL;
import me.universi.feedback.dto.FeedbackCreateDTO;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Feedback implements Serializable {

    @Serial
    private static final long serialVersionUID = -6370355453756629023L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_generator")
    @SequenceGenerator(name = "feedback_generator", sequenceName = "feedback_sequence", allocationSize = 1)
    private Long id;

    @NotNull
    @NotBlank
    @URL
    private String link;

    @NotNull
    @NotBlank
    @Size(min = 20,max = 512, message = "description must be less than 512 characters")
    private String feedbackText;

    @OneToOne(mappedBy = "feedback")
    @JsonBackReference
    private Question question;

    public Feedback(String link, String feedbackText, Question question) {
        this.link = link;
        this.feedbackText = feedbackText;
        this.question = question;
    }

    public Feedback(String link, String feedbackText) {
        this.link = link;
        this.feedbackText = feedbackText;
    }

    public Feedback() {
    }


    public static Feedback from (FeedbackCreateDTO feedbackCreateDTO){
        return new Feedback(
                feedbackCreateDTO.getLink(),
                feedbackCreateDTO.getFeedbackText());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Feedback feedback = (Feedback) o;
        return id != null && Objects.equals(id, feedback.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
