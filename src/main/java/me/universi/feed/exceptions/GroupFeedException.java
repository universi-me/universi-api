package me.universi.feed.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GroupFeedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GroupFeedException(String m) {
        super(m);
    }
}
