package com.example.x.exceptions.postExceptions;

public class PostLockedException extends PostException {
    public PostLockedException() {
        super("This post is locked!");
    }
}
