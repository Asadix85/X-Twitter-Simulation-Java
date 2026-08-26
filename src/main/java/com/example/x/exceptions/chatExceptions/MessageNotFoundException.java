package com.example.x.exceptions.chatExceptions;

public class MessageNotFoundException extends ChatException {
    public MessageNotFoundException() {
        super("Message not found!");
    }

    public MessageNotFoundException(int messageId) {
        super("Message '" + messageId + "' not found!");
    }
}
