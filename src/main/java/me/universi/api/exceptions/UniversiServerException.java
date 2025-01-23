package me.universi.api.exceptions;

import org.springframework.http.HttpStatus;

public class UniversiServerException extends UniversiException {
    public UniversiServerException( String message ) {
        super( message, HttpStatus.INTERNAL_SERVER_ERROR );
    }

    public UniversiServerException( Throwable throwable ) {
        super( throwable , HttpStatus.INTERNAL_SERVER_ERROR );
    }
}
