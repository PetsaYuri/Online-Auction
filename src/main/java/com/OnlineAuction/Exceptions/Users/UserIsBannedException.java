package com.OnlineAuction.Exceptions.Users;

public class UserIsBannedException extends RuntimeException{
    public UserIsBannedException() {
        super("The user is banned");
    }
}
