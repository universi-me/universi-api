package me.universi.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -4678022517650019451L;

    private static final String USER_NOT_FOUND = "User not found";

    public UserNotFoundException (){
        super(USER_NOT_FOUND);
    }
}
