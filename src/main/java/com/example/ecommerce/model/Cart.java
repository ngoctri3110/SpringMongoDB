package com.example.ecommerce.model;

import com.example.ecommerce.model.base.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cart document representing a customer's shopping cart.
 * Extends BaseDocument to get createdAt and updatedAt audit fields.
 * Supports TTL (Time To Live) for automatic cleanup of expired carts.
 */
@Document(collection = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Cart extends BaseDocument {

    /**
     * The unique identifier for this cart (MongoDB ObjectId).
     */
    @Id
    private String id;

    /**
     * The ID of the user who owns this cart.
     * This is indexed and unique to ensure one cart per user.
     */
    @Indexed(unique = true)
    private String userId;

    /**
     * List of items in the cart (embedded documents).
     */
    private List<CartItem> items;

    /**
     * Total quantity of items in the cart.
     */
    private Integer totalQuantity;

    /**
     * Total price for all items in the cart.
     */
    private BigDecimal totalPrice;

    /**
     * Expiration timestamp for the cart (for TTL index).
     * Carts can be automatically deleted after this time.
     */
    private LocalDateTime expiresAt;

    /**
     * Convenience constructor for creating a cart with essential fields.
     *
     * @param userId the user ID
     * @param items the list of items
     */
    public Cart(String userId, List<CartItem> items) {
        this.userId = userId;
        this.items = items;
        this.totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        this.totalPrice = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Convenience constructor with expiration time.
     *
     * @param userId the user ID
     * @param items the list of items
     * @param expiresAt the expiration timestamp
     */
    public Cart(String userId, List<CartItem> items, LocalDateTime expiresAt) {
        this.userId = userId;
        this.items = items;
        this.expiresAt = expiresAt;
        this.totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        this.totalPrice = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Recalculate totals based on current items.
     * Call this after modifying the items list.
     */
    public void recalculateTotals() {
        if (items == null || items.isEmpty()) {
            this.totalQuantity = 0;
            this.totalPrice = BigDecimal.ZERO;
        } else {
            this.totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
            this.totalPrice = items.stream()
                    .map(CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}
