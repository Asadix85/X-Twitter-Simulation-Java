package com.example.x.exceptions.authExceptions;

public class InvalidFullNameException extends AuthException {
    public InvalidFullNameException() {
            super("Invalid FullName format!");
        }

    public InvalidFullNameException(String fullName) {
            super("Invalid fullName: " + fullName);
    }
}
