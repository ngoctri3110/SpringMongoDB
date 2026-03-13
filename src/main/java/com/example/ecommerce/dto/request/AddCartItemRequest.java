package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để thêm item vào giỏ hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCartItemRequest {

    /**
     * Product ID
     */
    @NotBlank(message = "Product ID is required")
    private String productId;

    /**
     * Quantity to add
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
