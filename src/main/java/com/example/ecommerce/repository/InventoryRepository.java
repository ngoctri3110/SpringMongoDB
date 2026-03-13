package com.example.ecommerce.repository;

import com.example.ecommerce.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Inventory entity.
 * Provides database operations for managing product inventory and stock levels in MongoDB.
 * 
 * @author E-Commerce Platform
 * @version 1.0
 */
@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {

    /**
     * Finds inventory record for a specific product.
     * MongoDB equivalent: db.inventory.findOne({ productId: productId })
     *
     * @param productId the product ID
     * @return optional containing the inventory if found
     */
    Optional<Inventory> findByProductId(String productId);

    /**
     * Finds all inventory records with stock below a specified threshold.
     * MongoDB equivalent: db.inventory.find({ quantity: { $lt: threshold } })
     *
     * @param threshold the minimum quantity threshold
     * @return list of inventory records below threshold
     */
    List<Inventory> findByQuantityLessThan(int threshold);

    /**
     * Updates stock quantity for a product using MongoDB update operation.
     * MongoDB equivalent: db.inventory.updateOne({ productId: productId }, { $set: { quantity: quantity } })
     *
     * @param productId the product ID
     * @param quantity the new quantity value
     */
    @Query("{ 'productId': ?0 }")
    void updateStock(String productId, int quantity);

    /**
     * Finds products with low stock using pagination.
     * MongoDB equivalent: db.inventory.find({ quantity: { $lt: threshold } })
     *
     * @param threshold the low stock threshold
     * @param pageable pagination information
     * @return paginated page of low stock inventory records
     */
    Page<Inventory> findByQuantityLessThan(int threshold, Pageable pageable);
}
