package com.example.ecommerce.model;

import lombok.*;
import java.math.BigDecimal;

/**
 * Embedded document representing an item in an order.
 * This is embedded within the Order document and should not be used as a standalone collection.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class OrderItem {

    /**
     * The ID of the product.
     */
    private String productId;

    /**
     * The name of the product at the time of order.
     */
    private String productName;

    /**
     * The slug/URL-friendly identifier for the product.
     */
    private String productSlug;

    /**
     * The price of the product at the time of order.
     */
    private BigDecimal price;

    /**
     * The quantity ordered.
     */
    private Integer quantity;

    /**
     * Total price for this line item (price * quantity).
     */
    private BigDecimal totalPrice;

    /**
     * Convenience constructor for common use cases.
     *
     * @param productId the product ID
     * @param productName the product name
     * @param price the price per unit
     * @param quantity the quantity ordered
     */
    public OrderItem(String productId, String productName, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price.multiply(new BigDecimal(quantity));
    }
}
