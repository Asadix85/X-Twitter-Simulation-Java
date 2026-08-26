package com.example.x.exceptions.userExceptions;

public class UserNotLoggedInException extends UserException {
    public UserNotLoggedInException() {
        super("User is not logged in!");
    }
}
