package me.universi.api.exceptions;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.http.HttpStatus;

import me.universi.api.ApiError;

public class UniversiException extends RuntimeException {
    public final String message;
    public final HttpStatus status;

    public UniversiException( String message, HttpStatus status ) {
        super( message );
        this.message = message;
        this.status = status;
    }

    public UniversiException( Throwable throwable, HttpStatus status ) {
        super( throwable );
        this.message = throwable.getMessage();
        this.status = status;
    }

    public ApiError toApiError() {
        return ApiError.builder()
            .status( status )
            .errors( Arrays.asList( this.message ) )
            .timestamp( LocalDateTime.now() )
            .build();
    }
}
