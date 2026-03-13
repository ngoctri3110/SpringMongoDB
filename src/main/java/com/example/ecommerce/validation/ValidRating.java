package com.example.ecommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation to validate that a rating value is between 1 and 5.
 * Used for product reviews and ratings.
 * 
 * Annotation tùy chỉnh để xác thực rằng giá trị xếp hạng nằm trong khoảng 1 đến 5.
 * Được sử dụng cho đánh giá và xếp hạng sản phẩm.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRatingValidator.class)
@Documented
public @interface ValidRating {

    String message() default "Rating must be between 1 and 5";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 1;

    int max() default 5;
}
