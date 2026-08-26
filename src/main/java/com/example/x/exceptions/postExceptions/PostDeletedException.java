package com.example.x.exceptions.postExceptions;

public class PostDeletedException extends PostException {
    public PostDeletedException() {
        super("This post has been deleted!");
    }

    public PostDeletedException(String postId) {
        super("Post '" + postId + "' has been deleted!");
    }
}
