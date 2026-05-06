package com.codesoftlabs.umbral.exception;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", 401);
    }

    public UnauthorizedException() {
        super("Authentication required", "UNAUTHORIZED", 401);
    }
}
