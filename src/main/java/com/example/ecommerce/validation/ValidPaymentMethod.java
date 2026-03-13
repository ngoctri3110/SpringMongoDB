package com.example.ecommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation to validate that a payment method is one of the valid methods.
 * Valid methods: CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
 * 
 * Annotation tùy chỉnh để xác thực rằng phương thức thanh toán là một trong các phương thức hợp lệ.
 * Phương thức hợp lệ: CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPaymentMethodValidator.class)
@Documented
public @interface ValidPaymentMethod {

    String message() default "Payment method must be one of: CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
