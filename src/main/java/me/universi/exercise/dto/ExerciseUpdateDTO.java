package me.universi.exercise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ExerciseUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3607514412275394492L;

    @NotNull
    @NotBlank
    private String title;

    public ExerciseUpdateDTO(String title) {
        this.title = title;
    }

    public ExerciseUpdateDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
