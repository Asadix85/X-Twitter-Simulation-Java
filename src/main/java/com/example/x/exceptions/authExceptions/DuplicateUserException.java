package com.example.x.exceptions.authExceptions;

public class DuplicateUserException extends AuthException {
    public DuplicateUserException() {
        super("Username already exists!");
    }

    public DuplicateUserException(String username) {
        super("Username '" + username + "' already exists!");
    }
}
