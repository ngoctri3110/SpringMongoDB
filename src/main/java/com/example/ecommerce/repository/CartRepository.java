package com.example.ecommerce.repository;

import com.example.ecommerce.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Cart entity.
 * Provides database operations for managing shopping carts in MongoDB.
 * 
 * @author E-Commerce Platform
 * @version 1.0
 */
@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    /**
     * Finds the shopping cart for a specific user.
     * MongoDB equivalent: db.carts.findOne({ userId: userId })
     *
     * @param userId the user ID
     * @return optional containing the user's cart if found
     */
    Optional<Cart> findByUserId(String userId);

    /**
     * Checks if a user has an existing shopping cart.
     * MongoDB equivalent: db.carts.countDocuments({ userId: userId }) > 0
     *
     * @param userId the user ID
     * @return true if cart exists for the user, false otherwise
     */
    boolean existsByUserId(String userId);

    /**
     * Deletes the shopping cart for a specific user.
     * MongoDB equivalent: db.carts.deleteOne({ userId: userId })
     *
     * @param userId the user ID to delete cart for
     */
    void deleteByUserId(String userId);
}
