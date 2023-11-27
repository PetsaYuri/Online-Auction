package com.OnlineAuction.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnableToSaveFileException extends RuntimeException{
    public UnableToSaveFileException(String message) {
        super(message);
    }
}
