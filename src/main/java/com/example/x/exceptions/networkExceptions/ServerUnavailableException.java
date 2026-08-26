package com.example.x.exceptions.networkExceptions;

public class ServerUnavailableException extends NetworkException {
    public ServerUnavailableException() {
        super("Server is unavailable!");
    }

    public ServerUnavailableException(String host, int port) {
        super("Server is unavailable at " + host + ":" + port);
    }
}

