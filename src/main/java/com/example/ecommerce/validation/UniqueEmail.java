package com.example.ecommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation to validate that an email address is unique in the system.
 * 
 * Annotation tùy chỉnh để xác thực rằng địa chỉ email là duy nhất trong hệ thống.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {

    String message() default "Email address already exists in the system";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
