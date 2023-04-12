package me.universi.feedback.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class FeedbackCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8179723447677973852L;

    @NotNull
    @NotBlank
    @URL
    private String link;

    @NotNull
    @NotBlank
    @Size(min = 20,max = 512, message = "description must be less than 512 characters")
    private String feedbackText;

    public FeedbackCreateDTO(String link, String feedbackText) {
        this.link = link;
        this.feedbackText = feedbackText;
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
}
