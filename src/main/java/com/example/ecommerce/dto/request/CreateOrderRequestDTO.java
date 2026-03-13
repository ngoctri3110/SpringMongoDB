package com.example.ecommerce.dto.request;

import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.PaymentInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for order creation request.
 * Contains order items, shipping address, and payment information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {

    @NotEmpty(message = "Order items cannot be empty")
    private List<CartItem> items;

    @NotNull(message = "Shipping address is required")
    @Valid
    private Address shippingAddress;

    @NotNull(message = "Payment info is required")
    @Valid
    private PaymentInfo paymentInfo;
}
