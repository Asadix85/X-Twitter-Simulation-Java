package com.example.x.exceptions.validationExceptions;

public class InvalidInputException extends ValidationException {
    public InvalidInputException(String field) {
        super("Invalid input for field: " + field);
    }

    public InvalidInputException(String field, String reason) {
        super("Invalid input for field '" + field + "': " + reason);
    }
}
