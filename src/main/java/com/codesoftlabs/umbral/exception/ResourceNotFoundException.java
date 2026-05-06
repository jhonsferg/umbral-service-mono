package com.codesoftlabs.umbral.exception;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", 404);
    }

    public ResourceNotFoundException(String message, String resourceName, String identifier) {
        super(String.format("%s not found with %s", resourceName, identifier),
                "RESOURCE_NOT_FOUND", 404);
    }
}
