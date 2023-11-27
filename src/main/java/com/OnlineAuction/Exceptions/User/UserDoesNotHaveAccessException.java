package com.OnlineAuction.Exceptions.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserDoesNotHaveAccessException extends RuntimeException{
    public UserDoesNotHaveAccessException() {
        super("User doesn't have access to this endpoint");
    }
}
