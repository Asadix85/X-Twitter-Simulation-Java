package com.example.x.exceptions.chatExceptions;

public class CannotSendMessageException extends ChatException {
    public CannotSendMessageException() {
        super("Cannot send message!");
    }

    public CannotSendMessageException(String reason) {
        super("Cannot send message: " + reason);
    }
}
