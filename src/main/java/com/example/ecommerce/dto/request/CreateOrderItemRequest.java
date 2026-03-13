package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho item trong Order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequest {

    @NotBlank(message = "ID sản phẩm không được để trống")
    private String productId;

    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải > 0")
    private Integer quantity;

    @NotNull(message = "Giá không được để trống")
    private BigDecimal price;
}
