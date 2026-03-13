package com.example.ecommerce.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Kafka Review Event
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaReviewEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @JsonProperty("event_type")
    private String eventType; // CREATED, UPDATED, APPROVED, REJECTED

    @JsonProperty("review_id")
    private String reviewId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("title")
    private String title;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("status")
    private String status;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("message")
    private String message;
}
