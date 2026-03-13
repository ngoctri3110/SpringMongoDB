package com.example.ecommerce.model;

import com.example.ecommerce.model.base.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PaymentTransaction document representing a payment transaction.
 * Extends BaseDocument to get createdAt and updatedAt audit fields.
 * Records all payment attempts and their outcomes.
 */
@Document(collection = "paymentTransactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PaymentTransaction extends BaseDocument {

    /**
     * The unique identifier for this transaction (MongoDB ObjectId).
     */
    @Id
    private String id;

    /**
     * The ID of the order associated with this transaction.
     */
    @Indexed
    private String orderId;

    /**
     * The ID of the user making the payment.
     */
    private String userId;

    /**
     * The transaction amount.
     */
    private BigDecimal amount;

    /**
     * The currency code (USD, EUR, VND, etc.)
     */
    private String currency;

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
     * Error message if the transaction failed.
     */
    private String errorMessage;

    /**
     * Convenience constructor for creating a transaction with essential fields.
     *
     * @param orderId the order ID
     * @param userId the user ID
     * @param amount the transaction amount
     * @param currency the currency code
     * @param method the payment method
     */
    public PaymentTransaction(String orderId, String userId, BigDecimal amount, 
                              String currency, String method) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.status = "PENDING";
    }

    /**
     * Convenience constructor for a successful transaction.
     *
     * @param orderId the order ID
     * @param userId the user ID
     * @param amount the transaction amount
     * @param currency the currency code
     * @param method the payment method
     * @param transactionId the gateway transaction ID
     */
    public PaymentTransaction(String orderId, String userId, BigDecimal amount,
                              String currency, String method, String transactionId) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.transactionId = transactionId;
        this.status = "SUCCESS";
    }

    /**
     * Mark the transaction as successful with a gateway transaction ID.
     *
     * @param transactionId the gateway transaction ID
     */
    public void markSuccessful(String transactionId) {
        this.status = "SUCCESS";
        this.transactionId = transactionId;
        this.errorMessage = null;
    }

    /**
     * Mark the transaction as failed with an error message.
     *
     * @param errorMessage the error message
     */
    public void markFailed(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
    }
}
