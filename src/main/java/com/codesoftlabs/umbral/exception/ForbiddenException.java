package com.codesoftlabs.umbral.exception;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", 403);
    }

    public ForbiddenException() {
        super("Access denied", "FORBIDDEN", 403);
    }
}
