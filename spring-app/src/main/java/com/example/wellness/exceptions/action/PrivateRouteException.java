package com.example.wellness.exceptions.action;

public class PrivateRouteException extends RuntimeException {
    public PrivateRouteException() {
        super("Not allowed!");
    }
}
