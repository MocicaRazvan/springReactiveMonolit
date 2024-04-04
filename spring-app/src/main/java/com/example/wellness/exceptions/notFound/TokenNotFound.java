package com.example.wellness.exceptions.notFound;

public class TokenNotFound extends NotFoundBase {
    public TokenNotFound() {
        super("A valid token wasn't provided!");
    }
}
