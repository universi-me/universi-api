package me.universi.api.exceptions;

import org.springframework.http.HttpStatus;

public class UniversiPayloadTooLargeException extends UniversiException {
    public UniversiPayloadTooLargeException( String message ) {
        super( message, HttpStatus.PAYLOAD_TOO_LARGE );
    }
}
