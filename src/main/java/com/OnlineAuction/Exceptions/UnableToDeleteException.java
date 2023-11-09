package com.OnlineAuction.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnableToDeleteException extends RuntimeException {
    public UnableToDeleteException(String message) {
        super(message);
    }
}