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
 * Kafka Order Event - Gửi qua Kafka khi có order event
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaOrderEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @JsonProperty("event_type")
    private String eventType; // CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("item_count")
    private Integer itemCount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("message")
    private String message;
}
