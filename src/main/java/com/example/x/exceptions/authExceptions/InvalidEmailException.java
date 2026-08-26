package com.example.x.exceptions.authExceptions;

public class InvalidEmailException extends AuthException {
    public InvalidEmailException() {
        super("Invalid email format!");
    }

    public InvalidEmailException(String email) {
        super("Invalid email: " + email);
    }
}
