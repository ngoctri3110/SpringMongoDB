package com.example.ecommerce.model;

import com.example.ecommerce.model.base.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.util.List;

/**
 * Order document representing a customer order.
 * Extends BaseDocument to get createdAt and updatedAt audit fields.
 */
@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseDocument {

    /**
     * The unique identifier for this order (MongoDB ObjectId).
     */
    @Id
    private String id;

    /**
     * The ID of the user who placed the order.
     */
    private String userId;

    /**
     * The unique order number (e.g., ORD-2025-001234)
     * This is indexed and unique for easy customer reference.
     */
    @Indexed(unique = true)
    private String orderNumber;

    /**
     * List of items in this order (embedded documents).
     */
    private List<OrderItem> items;

    /**
     * Total amount for the order.
     */
    private BigDecimal totalAmount;

    /**
     * Order status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
     * TODO: Consider using an enum in future versions for type safety
     */
    private String status;

    /**
     * Shipping address for the order.
     */
    private Address shippingAddress;

    /**
     * Payment information for the order.
     */
    private PaymentInfo paymentInfo;

    /**
     * Shipping information including tracking and estimated delivery.
     */
    private ShippingInfo shippingInfo;

    /**
     * Convenience constructor for creating an order with essential fields.
     *
     * @param userId the user ID
     * @param orderNumber the order number
     * @param items the list of items
     * @param totalAmount the total amount
     * @param status the order status
     */
    public Order(String userId, String orderNumber, List<OrderItem> items, 
                 BigDecimal totalAmount, String status) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    /**
     * Convenience constructor with shipping address.
     *
     * @param userId the user ID
     * @param orderNumber the order number
     * @param items the list of items
     * @param totalAmount the total amount
     * @param status the order status
     * @param shippingAddress the shipping address
     */
    public Order(String userId, String orderNumber, List<OrderItem> items,
                 BigDecimal totalAmount, String status, Address shippingAddress) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }
}
