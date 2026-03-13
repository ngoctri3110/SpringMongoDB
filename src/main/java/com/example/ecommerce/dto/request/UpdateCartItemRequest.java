package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để cập nhật số lượng item trong giỏ hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartItemRequest {

    /**
     * New quantity
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
