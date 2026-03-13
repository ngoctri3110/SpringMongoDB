package com.example.ecommerce.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Kafka Payment Event
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaPaymentEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @JsonProperty("event_type")
    private String eventType; // INITIATED, PROCESSING, COMPLETED, FAILED, REFUNDED

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("status")
    private String status;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("message")
    private String message;
}
