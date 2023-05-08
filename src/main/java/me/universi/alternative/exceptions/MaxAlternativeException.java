package me.universi.alternative.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MaxAlternativeException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -3652030014178389442L;

    private static final String MAX_ALTERNATIVE = "Maximum of alternatives is 5!";

    public MaxAlternativeException() {
        super(MAX_ALTERNATIVE);
    }
}
