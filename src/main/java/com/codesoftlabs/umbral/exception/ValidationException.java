package com.codesoftlabs.umbral.exception;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
    }

    public ValidationException(String message, String fieldName) {
        super(String.format("Validation failed for field '%s': %s", fieldName, message),
                "VALIDATION_ERROR", 400);
    }
}
