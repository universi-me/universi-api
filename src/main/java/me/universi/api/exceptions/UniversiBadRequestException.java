package me.universi.api.exceptions;

import org.springframework.http.HttpStatus;

public class UniversiBadRequestException extends UniversiException {
    public UniversiBadRequestException( String message ) {
        super( message, HttpStatus.BAD_REQUEST );
    }

    public UniversiBadRequestException( Throwable throwable ) {
        super( throwable , HttpStatus.BAD_REQUEST );
    }
}
