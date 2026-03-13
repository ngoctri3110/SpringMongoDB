package com.example.ecommerce.dto.response;

import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.PaymentInfo;
import com.example.ecommerce.model.ShippingInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho Order response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private String id;
    private String userId;
    private String orderNumber;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    private Address shippingAddress;
    private PaymentInfo paymentInfo;
    private ShippingInfo shippingInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
