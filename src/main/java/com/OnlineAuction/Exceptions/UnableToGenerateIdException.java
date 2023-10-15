package com.OnlineAuction.Exceptions;

public class UnableToGenerateIdException extends RuntimeException{
    public UnableToGenerateIdException() {
        super("Unable to generate id");
    }
}