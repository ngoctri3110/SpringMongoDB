package com.example.ecommerce.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event triggered when a payment is processed.
 * 결제가 처리될 때 발생하는 이벤트.
 */
@Getter
public class PaymentProcessedEvent extends ApplicationEvent {

    private final String orderId;
    private final String paymentId;
    private final BigDecimal amount;
    private final String status; // SUCCESS, FAILURE
    private final LocalDateTime eventTimestamp;

    /**
     * Create a PaymentProcessedEvent.
     *
     * @param source the source object
     * @param orderId the order ID
     * @param paymentId the payment ID
     * @param amount the amount processed
     * @param status SUCCESS or FAILURE
     */
    public PaymentProcessedEvent(Object source, String orderId, String paymentId, 
                                  BigDecimal amount, String status) {
        super(source);
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.eventTimestamp = LocalDateTime.now();
    }
}
