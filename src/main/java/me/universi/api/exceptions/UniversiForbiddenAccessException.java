package me.universi.api.exceptions;

import org.springframework.http.HttpStatus;

public class UniversiForbiddenAccessException extends UniversiException {
    public UniversiForbiddenAccessException( String message ) {
        super( message, HttpStatus.FORBIDDEN );
    }
}
