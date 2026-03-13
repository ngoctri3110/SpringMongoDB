package com.example.ecommerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidRating constraint.
 * Validates that a rating value is within the specified range (default 1-5).
 * 
 * Triển khai xác thực cho ràng buộc ValidRating.
 * Xác thực rằng giá trị xếp hạng nằm trong phạm vi được chỉ định (mặc định 1-5).
 */
public class ValidRatingValidator implements ConstraintValidator<ValidRating, Integer> {

    private int min;
    private int max;

    @Override
    public void initialize(ValidRating annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    /**
     * Validates that the rating is within the specified range.
     * 
     * @param value the rating value to validate
     * @param context the constraint validator context
     * @return true if the rating is valid, false otherwise
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // Null values are considered valid by default
        if (value == null) {
            return true;
        }

        // Validate that the rating is within the specified range
        return value >= min && value <= max;
    }
}
