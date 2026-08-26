package com.example.x.exceptions.postExceptions;

public class PostException extends Exception {

    public PostException(String message) {
        super(message);
    }

    public PostException(String message, Throwable cause) {
        super(message, cause);
    }
}

