package me.universi.alternative.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlternativeNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1984068428878577946L;
    private static final String ALTERNATIVE_NOT_FOUND = "Alternative not found!";


    public AlternativeNotFoundException() {
        super(ALTERNATIVE_NOT_FOUND);
    }
}
