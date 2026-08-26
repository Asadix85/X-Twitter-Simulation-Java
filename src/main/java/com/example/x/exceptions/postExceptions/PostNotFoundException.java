package com.example.x.exceptions.postExceptions;

public class PostNotFoundException extends PostException {
    public PostNotFoundException() {
        super("Post not found!");
    }

    public PostNotFoundException(String postId) {
        super("Post '" + postId + "' not found!");
    }
}
