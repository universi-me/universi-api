package me.universi.api.exceptions;

import org.springframework.http.HttpStatus;

public class UniversiNoEntityException extends UniversiException {
    public UniversiNoEntityException( String message ) {
        super( message, HttpStatus.NOT_FOUND );
    }
}
