package com.example.ecommerce.model;

import com.example.ecommerce.model.base.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Inventory document representing product stock and reservation information.
 * Extends BaseDocument to get createdAt and updatedAt audit fields.
 * Tracks available quantity and reserved quantity for pending orders.
 */
@Document(collection = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Inventory extends BaseDocument {

    /**
     * The unique identifier for this inventory record (MongoDB ObjectId).
     */
    @Id
    private String id;

    /**
     * The ID of the product this inventory record tracks.
     */
    @Indexed
    private String productId;

    /**
     * Total quantity of the product in stock.
     */
    private Integer quantity;

    /**
     * Quantity reserved for pending orders.
     * The available quantity = quantity - reserved
     */
    private Integer reserved;

    /**
     * Warehouse location or code.
     */
    private String warehouse;

    /**
     * Timestamp of the last inventory update.
     */
    private LocalDateTime lastUpdated;

    /**
     * Convenience constructor for creating inventory with essential fields.
     *
     * @param productId the product ID
     * @param quantity the total quantity
     * @param warehouse the warehouse location
     */
    public Inventory(String productId, Integer quantity, String warehouse) {
        this.productId = productId;
        this.quantity = quantity;
        this.warehouse = warehouse;
        this.reserved = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Get the available quantity (not reserved).
     *
     * @return quantity - reserved
     */
    public Integer getAvailable() {
        return quantity - (reserved != null ? reserved : 0);
    }

    /**
     * Check if the requested quantity can be reserved.
     *
     * @param qty the quantity to check
     * @return true if available quantity >= qty, false otherwise
     */
    public boolean canReserve(Integer qty) {
        return getAvailable() >= qty;
    }

    /**
     * Reserve the specified quantity for a pending order.
     * Updates the reserved counter and lastUpdated timestamp.
     *
     * @param qty the quantity to reserve
     * @return true if reservation successful, false if insufficient quantity
     */
    public boolean reserve(Integer qty) {
        if (!canReserve(qty)) {
            return false;
        }
        this.reserved = (reserved != null ? reserved : 0) + qty;
        this.lastUpdated = LocalDateTime.now();
        return true;
    }

    /**
     * Release a reserved quantity (e.g., when an order is cancelled).
     * Updates the reserved counter and lastUpdated timestamp.
     *
     * @param qty the quantity to release
     * @return true if release successful, false if not enough reserved
     */
    public boolean release(Integer qty) {
        if (reserved == null || reserved < qty) {
            return false;
        }
        this.reserved -= qty;
        this.lastUpdated = LocalDateTime.now();
        return true;
    }
}
