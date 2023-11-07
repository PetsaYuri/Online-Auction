package com.OnlineAuction.Exceptions.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserIsBannedException extends RuntimeException{
    public UserIsBannedException() {
        super("The user is banned");
    }
}
