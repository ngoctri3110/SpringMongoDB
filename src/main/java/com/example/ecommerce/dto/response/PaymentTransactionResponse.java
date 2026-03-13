package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho PaymentTransaction response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionResponse {

    private String id;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String method;
    private String status;
    private String transactionId;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
