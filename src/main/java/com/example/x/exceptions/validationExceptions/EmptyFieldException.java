package com.example.x.exceptions.validationExceptions;

public class EmptyFieldException extends ValidationException {
    public EmptyFieldException(String field) {
        super("Field '" + field + "' cannot be empty!");
    }
}