package me.universi.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -169360247287281236L;

    private static final String USER_HAS_NO_AUTHORIZATION = "User has no authorization! ";


    public UnauthorizedException(){
        super(USER_HAS_NO_AUTHORIZATION);
    }
}
