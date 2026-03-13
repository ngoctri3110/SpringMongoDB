package com.example.ecommerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * Validator implementation for ValidPaymentMethod constraint.
 * Validates that a payment method is one of the allowed payment methods.
 * 
 * Triển khai xác thực cho ràng buộc ValidPaymentMethod.
 * Xác thực rằng phương thức thanh toán là một trong những phương thức thanh toán được phép.
 */
public class ValidPaymentMethodValidator implements ConstraintValidator<ValidPaymentMethod, String> {

    private static final Set<String> VALID_PAYMENT_METHODS = Set.of(
            "CREDIT_CARD",
            "DEBIT_CARD",
            "PAYPAL",
            "BANK_TRANSFER"
    );

    @Override
    public void initialize(ValidPaymentMethod annotation) {
        // Initialization logic if needed
    }

    /**
     * Validates that the payment method is one of the allowed values.
     * 
     * @param value the payment method to validate
     * @param context the constraint validator context
     * @return true if the payment method is valid, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are considered valid by default
        if (value == null) {
            return true;
        }

        // Check if the payment method is in the set of valid methods
        return VALID_PAYMENT_METHODS.contains(value.toUpperCase());
    }
}
