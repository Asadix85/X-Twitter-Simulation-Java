package com.example.x.exceptions.postExceptions;

public class CannotEditPostException extends PostException {
    public CannotEditPostException() {
        super("You cannot edit this post!");
    }
}
