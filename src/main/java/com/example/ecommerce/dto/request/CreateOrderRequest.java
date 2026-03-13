package com.example.ecommerce.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO cho tạo Order mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "ID người dùng không được để trống")
    private String userId;

    @NotEmpty(message = "Danh sách item không được để trống")
    private List<CreateOrderItemRequest> items;

    @Valid
    private CreateAddressRequest shippingAddress;

    private String notes;
}
