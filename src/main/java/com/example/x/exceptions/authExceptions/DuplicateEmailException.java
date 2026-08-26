package com.example.x.exceptions.authExceptions;

public class DuplicateEmailException extends AuthException {
    public DuplicateEmailException() {
        super("Email already exists!");
    }
    public DuplicateEmailException(String email) {
        super("Email '" + email + "' already exists!");
    }
}
