package com.example.ecommerce.model;

import lombok.*;

/**
 * Embedded document representing payment information for an order.
 * This is embedded within the Order document and should not be used as a standalone collection.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PaymentInfo {

    /**
     * Payment method (CREDIT_CARD, PAYPAL, BANK_TRANSFER, etc.)
     * TODO: Consider using an enum in future versions for type safety
     */
    private String method;

    /**
     * Payment status (PENDING, SUCCESS, FAILED, CANCELLED)
     * TODO: Consider using an enum in future versions for type safety
     */
    private String status;

    /**
     * Transaction ID from the payment gateway.
     */
    private String transactionId;

    /**
     * Error message if payment failed.
     */
    private String errorMessage;

    /**
     * Convenience constructor for successful payments.
     *
     * @param method the payment method
     * @param transactionId the transaction ID from gateway
     */
    public PaymentInfo(String method, String transactionId) {
        this.method = method;
        this.transactionId = transactionId;
        this.status = "SUCCESS";
    }
}
