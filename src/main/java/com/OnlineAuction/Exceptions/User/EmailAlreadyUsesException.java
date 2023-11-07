package com.OnlineAuction.Exceptions.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailAlreadyUsesException extends RuntimeException{
    public EmailAlreadyUsesException() {
        super("Email already used in this application");
    }
}
