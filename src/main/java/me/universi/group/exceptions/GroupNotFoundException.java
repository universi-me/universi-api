package me.universi.group.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GroupNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -8297195427149770678L;

    private static final String GROUP_NOT_FOUND = "Group not found!";

    public GroupNotFoundException() {
        super(GROUP_NOT_FOUND);
    }
}
