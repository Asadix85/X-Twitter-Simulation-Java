package com.example.x.exceptions.databaseExceptions;

public class ConnectionFailedException extends DatabaseException {
    public ConnectionFailedException() {
        super("Failed to connect to database!");
    }

    public ConnectionFailedException(String reason) {
        super("Failed to connect to database: " + reason);
    }
}