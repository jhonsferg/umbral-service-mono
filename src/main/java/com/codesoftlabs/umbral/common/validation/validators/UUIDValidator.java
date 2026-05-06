package com.codesoftlabs.umbral.common.validation.validators;

import com.codesoftlabs.umbral.common.validation.annotations.IsUUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UUIDValidator implements ConstraintValidator<IsUUID, String> {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-" +
                    "[0-9a-fA-F]{4}-" +
                    "[1-5][0-9a-fA-F]{3}-" +
                    "[89abAB][0-9a-fA-F]{3}-" +
                    "[0-9a-fA-F]{12}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return UUID_PATTERN.matcher(value).matches();
    }
}
