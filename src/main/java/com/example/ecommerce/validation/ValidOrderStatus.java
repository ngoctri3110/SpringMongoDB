package com.example.ecommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation to validate that an order status is one of the valid statuses.
 * Valid statuses: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
 * 
 * Annotation tùy chỉnh để xác thực rằng trạng thái đơn hàng là một trong các trạng thái hợp lệ.
 * Trạng thái hợp lệ: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidOrderStatusValidator.class)
@Documented
public @interface ValidOrderStatus {

    String message() default "Order status must be one of: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
