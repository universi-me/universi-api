package me.universi.api.exceptions;

import org.springframework.http.HttpStatus;

public class UniversiUnprocessableOperationException extends UniversiException {
    public UniversiUnprocessableOperationException( String message ) {
        super( message, HttpStatus.UNPROCESSABLE_ENTITY );
    }
}
