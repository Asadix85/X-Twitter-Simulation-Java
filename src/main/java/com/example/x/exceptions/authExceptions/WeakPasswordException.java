package com.example.x.exceptions.authExceptions;

public class WeakPasswordException extends AuthException {
    public WeakPasswordException() {
        super("Password is too weak! Use at least 8 characters with letters and numbers.");
    }}
