package com.example.x.exceptions.postExceptions;

public class NotEnoughBalanceException extends PostException {
    public NotEnoughBalanceException() {
        super("Insufficient token balance!");
    }

    public NotEnoughBalanceException(int needed, int available) {
        super("Insufficient tokens! Need: " + needed + ", Available: " + available);
    }
}
