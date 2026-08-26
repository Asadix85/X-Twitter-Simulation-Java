package com.example.x.exceptions.userExceptions;

public class UserBlockedException extends UserException {
    public UserBlockedException() {
        super("This user has been blocked!");
    }

    public UserBlockedException(String username) {
        super("User '" + username + "' has been blocked!");
    }}
