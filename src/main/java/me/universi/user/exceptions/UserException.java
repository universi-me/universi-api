package me.universi.user.exceptions;

import me.universi.api.exceptions.UniversiException;
import org.springframework.http.HttpStatus;

public class UserException extends UniversiException {
    public UserException(String message, HttpStatus status) {
        super(message, status);
    }
    public UserException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}