package com.example.ecommerce.model;

import lombok.*;
import java.math.BigDecimal;

/**
 * Embedded document representing an item in a shopping cart.
 * This is embedded within the Cart document and should not be used as a standalone collection.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CartItem {

    /**
     * The ID of the product.
     */
    private String productId;

    /**
     * The name of the product.
     */
    private String productName;

    /**
     * The current price of the product.
     */
    private BigDecimal price;

    /**
     * The quantity in the cart.
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
     * @param quantity the quantity in cart
     */
    public CartItem(String productId, String productName, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price.multiply(new BigDecimal(quantity));
    }
}
