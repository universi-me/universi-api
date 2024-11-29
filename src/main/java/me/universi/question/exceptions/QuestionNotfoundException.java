package me.universi.question.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class QuestionNotfoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -2225358959285373128L;

    private static final String QUESTION_NOT_FOUND = "Question not found!";

    public QuestionNotfoundException(){
        super(QUESTION_NOT_FOUND);
    }
}
