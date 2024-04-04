package com.example.wellness.exceptions;

public class UserWithEmailExists extends RuntimeException {
    public UserWithEmailExists(String email) {
        super("User with email " + email + " already exists");
    }
}
