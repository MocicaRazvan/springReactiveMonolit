package com.example.wellness.exceptions.notFound;

public abstract class NotFoundBase extends RuntimeException {
    public NotFoundBase(String message) {
        super(message);
    }
}
