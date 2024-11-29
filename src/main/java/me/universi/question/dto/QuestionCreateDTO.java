package me.universi.question.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.universi.profile.entities.Profile;
import me.universi.question.entities.Question;
import me.universi.user.entities.User;

import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class QuestionCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -368443678230510253L;

    @NotBlank(message = "title not blank")
    @NotNull(message = "title not null")
    @Size(min = 15, max = 512)
    private String title;

    private Profile profileCreate;

    public QuestionCreateDTO(String title) {
        this.title = title;
    }

    public static QuestionCreateDTO from (Question question){
        return new QuestionCreateDTO(
                question.getTitle());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Profile getProfileCreate() {
        return profileCreate;
    }

    public void setProfileCreate(Profile profileCreate) {
        this.profileCreate = profileCreate;
    }
}
