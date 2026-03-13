package com.example.ecommerce.service;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Inventory;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.InventoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing product inventory and stock levels.
 * Handles inventory tracking, stock updates, reservations, and releases.
 * Prevents negative inventory and tracks reserved quantities for pending orders.
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    /**
     * Retrieves inventory information for a specific product.
     *
     * @param productId the product ID
     * @return the Inventory object
     * @throws ResourceNotFoundException if inventory doesn't exist
     */
    @Transactional(readOnly = true)
    public Inventory getInventory(String productId) {
        log.debug("Fetching inventory for product: {}", productId);

        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory", "productId", productId));
    }

    /**
     * Updates the stock quantity for a product.
     * Validates that product exists and prevents negative inventory.
     *
     * @param productId the product ID
     * @param newQuantity the new stock quantity (must be >= 0)
     * @return the updated Inventory object
     * @throws ResourceNotFoundException if inventory doesn't exist
     * @throws IllegalArgumentException if quantity is negative
     */
    @Transactional
    public Inventory updateStock(String productId, Integer newQuantity) {
        log.info("Updating stock for product: {} to quantity: {}", productId, newQuantity);

        // Validate quantity
        if (newQuantity == null || newQuantity < 0) {
            log.warn("Invalid quantity: {} - must be >= 0", newQuantity);
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        // Get existing inventory
        Inventory inventory = getInventory(productId);

        // Update quantity
        Integer oldQuantity = inventory.getQuantity();
        inventory.setQuantity(newQuantity);
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        // Update product stock
        updateProductStock(productId, newQuantity);

        log.info("Stock updated for product: {} - oldQuantity: {}, newQuantity: {}",
                productId, oldQuantity, newQuantity);

        return updatedInventory;
    }

    /**
     * Increments the stock quantity for a product.
     * Used for restock operations.
     *
     * @param productId the product ID
     * @param quantity the quantity to add (must be > 0)
     * @return the updated Inventory object
     * @throws ResourceNotFoundException if inventory doesn't exist
     */
    @Transactional
    public Inventory incrementStock(String productId, Integer quantity) {
        log.info("Incrementing stock for product: {} by quantity: {}", productId, quantity);

        if (quantity == null || quantity <= 0) {
            log.warn("Invalid increment quantity: {}", quantity);
            throw new IllegalArgumentException("Increment quantity must be greater than 0");
        }

        Inventory inventory = getInventory(productId);
        Integer newQuantity = inventory.getQuantity() + quantity;

        return updateStock(productId, newQuantity);
    }

    /**
     * Decrements the stock quantity for a product.
     * Used for order fulfillment.
     *
     * @param productId the product ID
     * @param quantity the quantity to subtract (must be > 0)
     * @return the updated Inventory object
     * @throws ResourceNotFoundException if inventory doesn't exist
     * @throws IllegalArgumentException if result would be negative
     */
    @Transactional
    public Inventory decrementStock(String productId, Integer quantity) {
        log.info("Decrementing stock for product: {} by quantity: {}", productId, quantity);

        if (quantity == null || quantity <= 0) {
            log.warn("Invalid decrement quantity: {}", quantity);
            throw new IllegalArgumentException("Decrement quantity must be greater than 0");
        }

        Inventory inventory = getInventory(productId);
        Integer newQuantity = inventory.getQuantity() - quantity;

        if (newQuantity < 0) {
            log.error("Stock would go negative for product: {} - current: {}, decrement: {}",
                    productId, inventory.getQuantity(), quantity);
            throw new InsufficientStockException(
                    "Insufficient stock to decrement");
        }

        return updateStock(productId, newQuantity);
    }

    /**
     * Reserves stock for a pending order.
     * Reserved stock is deducted from available quantity but not from total quantity.
     * This allows for cancellation without losing stock permanently.
     *
     * @param productId the product ID
     * @param quantity the quantity to reserve (must be > 0)
     * @return the updated Inventory object
     * @throws ResourceNotFoundException if inventory doesn't exist
     * @throws InsufficientStockException if available stock is insufficient
     */
    @Transactional
    public Inventory reserveStock(String productId, Integer quantity) {
        log.info("Reserving stock for product: {} - quantity: {}", productId, quantity);

        if (quantity == null || quantity <= 0) {
            log.warn("Invalid reserve quantity: {}", quantity);
            throw new IllegalArgumentException("Reserve quantity must be greater than 0");
        }

        Inventory inventory = getInventory(productId);

        // Check if enough available stock
        if (!inventory.canReserve(quantity)) {
            log.warn("Cannot reserve stock for product: {} - requested: {}, available: {}",
                    productId, quantity, inventory.getAvailable());
            throw new InsufficientStockException(
                    "Insufficient available stock for reservation");
        }

        // Reserve the stock
        if (!inventory.reserve(quantity)) {
            log.error("Failed to reserve stock for product: {}", productId);
            throw new InsufficientStockException(
                    "Failed to reserve stock");
        }

        Inventory updatedInventory = inventoryRepository.save(inventory);
        log.debug("Stock reserved for product: {} - reserved: {}, available: {}",
                productId, inventory.getReserved(), inventory.getAvailable());

        return updatedInventory;
    }

    /**
     * Releases reserved stock (e.g., when an order is cancelled).
     * Moves stock from reserved back to available.
     *
     * @param productId the product ID
     * @param quantity the quantity to release (must be > 0)
     * @return the updated Inventory object
     * @throws ResourceNotFoundException if inventory doesn't exist
     * @throws IllegalArgumentException if more quantity is released than was reserved
     */
    @Transactional
    public Inventory releaseStock(String productId, Integer quantity) {
        log.info("Releasing reserved stock for product: {} - quantity: {}", productId, quantity);

        if (quantity == null || quantity <= 0) {
            log.warn("Invalid release quantity: {}", quantity);
            throw new IllegalArgumentException("Release quantity must be greater than 0");
        }

        Inventory inventory = getInventory(productId);

        // Release the reserved stock
        if (!inventory.release(quantity)) {
            log.error("Failed to release stock for product: {} - reserved: {}, requested: {}",
                    productId, inventory.getReserved(), quantity);
            throw new IllegalArgumentException(
                    "Cannot release more stock than reserved");
        }

        Inventory updatedInventory = inventoryRepository.save(inventory);
        log.debug("Stock released for product: {} - reserved: {}, available: {}",
                productId, inventory.getReserved(), inventory.getAvailable());

        return updatedInventory;
    }

    /**
     * Retrieves all inventory records with stock below a specified threshold.
     * Useful for low stock alerts.
     *
     * @param threshold the minimum stock level
     * @return List of Inventory objects below threshold
     */
    @Transactional(readOnly = true)
    public List<Inventory> getLowStockProducts(int threshold) {
        log.debug("Fetching low stock products with threshold: {}", threshold);
        return inventoryRepository.findByQuantityLessThan(threshold);
    }

    /**
     * Retrieves low stock inventory with pagination.
     *
     * @param threshold the minimum stock level
     * @param pageable pagination information
     * @return Page of Inventory objects below threshold
     */
    @Transactional(readOnly = true)
    public Page<Inventory> getLowStockProductsPaginated(int threshold, Pageable pageable) {
        log.debug("Fetching low stock products paginated - threshold: {}", threshold);
        return inventoryRepository.findByQuantityLessThan(threshold, pageable);
    }

    /**
     * Gets the available quantity (total - reserved) for a product.
     *
     * @param productId the product ID
     * @return available quantity
     * @throws ResourceNotFoundException if inventory doesn't exist
     */
    @Transactional(readOnly = true)
    public Integer getAvailableQuantity(String productId) {
        Inventory inventory = getInventory(productId);
        return inventory.getAvailable();
    }

    /**
     * Gets the reserved quantity for a product.
     *
     * @param productId the product ID
     * @return reserved quantity
     * @throws ResourceNotFoundException if inventory doesn't exist
     */
    @Transactional(readOnly = true)
    public Integer getReservedQuantity(String productId) {
        Inventory inventory = getInventory(productId);
        return inventory.getReserved() != null ? inventory.getReserved() : 0;
    }

    /**
     * Checks if a product has sufficient stock for a given quantity.
     *
     * @param productId the product ID
     * @param requestedQuantity the quantity to check
     * @return true if stock is sufficient, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean hassufficientStock(String productId, Integer requestedQuantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
        if (inventoryOpt.isEmpty()) {
            return false;
        }

        Inventory inventory = inventoryOpt.get();
        return inventory.getAvailable() >= requestedQuantity;
    }

    /**
     * Creates a new inventory record for a product.
     *
     * @param productId the product ID
     * @param quantity the initial quantity
     * @param warehouse the warehouse location
     * @return the created Inventory object
     */
    @Transactional
    public Inventory createInventory(String productId, Integer quantity, String warehouse) {
        log.info("Creating inventory for product: {} - quantity: {}, warehouse: {}",
                productId, quantity, warehouse);

        // Verify product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", productId));

        // Check if inventory already exists
        Optional<Inventory> existingInventory = inventoryRepository.findByProductId(productId);
        if (existingInventory.isPresent()) {
            log.warn("Inventory already exists for product: {}", productId);
            throw new IllegalArgumentException(
                    "Inventory already exists for this product");
        }

        // Create new inventory
        Inventory inventory = new Inventory(productId, quantity, warehouse);
        Inventory savedInventory = inventoryRepository.save(inventory);

        log.debug("Inventory created for product: {}", productId);

        return savedInventory;
    }

    /**
     * Updates the warehouse location for a product's inventory.
     *
     * @param productId the product ID
     * @param newWarehouse the new warehouse location
     * @return the updated Inventory object
     */
    @Transactional
    public Inventory updateWarehouse(String productId, String newWarehouse) {
        log.info("Updating warehouse for product: {} to: {}", productId, newWarehouse);

        Inventory inventory = getInventory(productId);
        inventory.setWarehouse(newWarehouse);
        inventory.setLastUpdated(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    /**
     * Updates product stock in the Product document to match Inventory.
     * Ensures consistency between documents.
     *
     * @param productId the product ID
     * @param quantity the new stock quantity
     */
    private void updateProductStock(String productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStock(quantity);
            productRepository.save(product);
            log.debug("Product stock updated: {}", productId);
        }
    }
}

/**
 * Custom exception for insufficient inventory.
 */
class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
