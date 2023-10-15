package com.OnlineAuction.Exceptions.Users;

public class EmailAlreadyUsesException extends RuntimeException{
    public EmailAlreadyUsesException() {
        super("Email already used in this application");
    }
}
