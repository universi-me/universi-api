package me.universi.exercise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExerciseNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -8088964753764574221L;

    private static final String EXERCISE_NOT_FOUND = "Exercise not found!";



    public ExerciseNotFoundException() {
        super(EXERCISE_NOT_FOUND);
    }
}
