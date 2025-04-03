package me.universi.api.exceptions;

import org.springframework.http.HttpStatus;

public class UniversiConflictingOperationException extends UniversiException {
    public UniversiConflictingOperationException( String message ) {
        super( message, HttpStatus.CONFLICT );
    }
}
