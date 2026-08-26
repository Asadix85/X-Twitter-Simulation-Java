package com.example.x.exceptions.authExceptions;

public class InvalidUserNameException extends AuthException {
    public InvalidUserNameException() {
        super("Invalid UserName format!");
    }

    public InvalidUserNameException(String userName) {
        super("Invalid userName: " + userName);
    }
}
