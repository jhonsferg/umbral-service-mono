package com.codesoftlabs.umbral.common.validation.validators;

import com.codesoftlabs.umbral.common.validation.annotations.IsUUID;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

public class IsUUIDValidator implements ConstraintValidator<IsUUID, String> {

    private boolean allowCompact;

    @Override
    public void initialize(IsUUID annotation) {
        this.allowCompact = annotation.allowCompact();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (allowCompact && value.matches("^[0-9a-fA-F]{32}$")) {
            return true;
        }

        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
