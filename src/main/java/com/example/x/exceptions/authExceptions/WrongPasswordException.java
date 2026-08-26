package com.example.x.exceptions.authExceptions;

public class WrongPasswordException extends AuthException {
    public WrongPasswordException() {
        super("Incorrect password!");
    }

    public WrongPasswordException(String message) {
        super(message);
    }
}
