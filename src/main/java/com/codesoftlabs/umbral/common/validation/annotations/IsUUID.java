package com.codesoftlabs.umbral.common.validation.annotations;

import com.codesoftlabs.umbral.common.validation.validators.IsUUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsUUIDValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsUUID {
    String message() default "The value must be a valid UUID (xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowCompact() default false;
}
