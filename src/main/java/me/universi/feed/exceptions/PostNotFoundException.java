package me.universi.feed.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PostNotFoundException(String postId) {
        super("Post not found with ID: " + postId);
    }

    public PostNotFoundException() {
        super("Post not found");
    }
}
