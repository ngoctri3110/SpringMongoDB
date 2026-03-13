package com.example.ecommerce.exception;

/**
 * Exception thrown when payment processing fails.
 * This includes payment gateway errors, declined cards, insufficient funds, etc.
 * 
 * Exception được throw khi xử lý thanh toán thất bại.
 * Điều này bao gồm lỗi cổng thanh toán, thẻ bị từ chối, không đủ tiền, v.v.
 */
public class PaymentException extends RuntimeException {

    /**
     * Constructs a PaymentException with a detailed error message.
     * 
     * @param message the detail message explaining the payment failure
     */
    public PaymentException(String message) {
        super(message);
    }

    /**
     * Constructs a PaymentException with a message and cause.
     * 
     * @param message the detail message explaining the payment failure
     * @param cause the cause of the payment failure
     */
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
