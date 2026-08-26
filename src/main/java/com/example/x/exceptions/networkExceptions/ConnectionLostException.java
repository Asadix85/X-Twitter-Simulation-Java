package com.example.x.exceptions.networkExceptions;

public class ConnectionLostException extends NetworkException {
    public ConnectionLostException() {
        super("Connection to server lost!");
    }
}