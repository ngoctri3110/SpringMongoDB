package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for refunding a payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentRequest {

    @NotBlank(message = "Refund reason cannot be blank")
    private String reason;

    private String notes;
}
