package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho tạo Inventory mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryRequest {

    @NotBlank(message = "ID sản phẩm không được để trống")
    private String productId;

    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải > 0")
    private Integer quantity;

    @NotBlank(message = "Kho không được để trống")
    private String warehouse;

    private Integer reserved = 0;
}
