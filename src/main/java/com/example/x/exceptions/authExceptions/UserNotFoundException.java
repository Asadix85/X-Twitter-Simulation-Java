package com.example.x.exceptions.authExceptions;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException() {
        super("User not found!");
    }

    public UserNotFoundException(String username) {
        super("User '" + username + "' not found!");
    }
}
