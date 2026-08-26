package com.example.x.exceptions.networkExceptions;

public class PacketSendException extends NetworkException {
    public PacketSendException() {
        super("Failed to send packet!");
    }
}
