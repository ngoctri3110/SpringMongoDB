package com.example.ecommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation to validate that a product slug is unique in the system.
 * 
 * Annotation tùy chỉnh để xác thực rằng slug sản phẩm là duy nhất trong hệ thống.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueSlugValidator.class)
@Documented
public @interface UniqueSlug {

    String message() default "Product slug already exists in the system";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
