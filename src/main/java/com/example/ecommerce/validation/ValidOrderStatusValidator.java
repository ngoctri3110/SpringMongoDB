package com.example.ecommerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * Validator implementation for ValidOrderStatus constraint.
 * Validates that an order status is one of the allowed order statuses.
 * 
 * Triển khai xác thực cho ràng buộc ValidOrderStatus.
 * Xác thực rằng trạng thái đơn hàng là một trong những trạng thái đơn hàng được phép.
 */
public class ValidOrderStatusValidator implements ConstraintValidator<ValidOrderStatus, String> {

    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING",
            "PROCESSING",
            "SHIPPED",
            "DELIVERED",
            "CANCELLED"
    );

    @Override
    public void initialize(ValidOrderStatus annotation) {
        // Initialization logic if needed
    }

    /**
     * Validates that the order status is one of the allowed values.
     * 
     * @param value the order status to validate
     * @param context the constraint validator context
     * @return true if the status is valid, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are considered valid by default
        if (value == null) {
            return true;
        }

        // Check if the status is in the set of valid statuses
        return VALID_STATUSES.contains(value.toUpperCase());
    }
}
