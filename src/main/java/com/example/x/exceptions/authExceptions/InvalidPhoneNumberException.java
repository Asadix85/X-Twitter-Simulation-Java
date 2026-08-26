package com.example.x.exceptions.authExceptions;

public class InvalidPhoneNumberException extends AuthException {
    public InvalidPhoneNumberException() {
        super("Invalid phone number format!");
    }

    public InvalidPhoneNumberException(String phoneNumber) {
        super("Invalid phone number: " + phoneNumber);
    }
}
