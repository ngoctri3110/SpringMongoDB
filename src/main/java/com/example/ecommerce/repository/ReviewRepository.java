package com.example.ecommerce.repository;

import com.example.ecommerce.model.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity.
 * Provides database operations for managing product reviews in MongoDB.
 * 
 * @author E-Commerce Platform
 * @version 1.0
 */
@Repository
@Slf4j
public interface ReviewRepository extends MongoRepository<Review, String> {

    /**
     * Finds all reviews for a specific product with pagination support.
     * MongoDB equivalent: db.reviews.find({ productId: productId })
     *
     * @param productId the product ID
     * @param pageable pagination information
     * @return paginated page of reviews for the product
     */
    Page<Review> findByProductId(String productId, Pageable pageable);

    /**
     * Finds a review by user and product (ensures one review per user per product).
     * MongoDB equivalent: db.reviews.findOne({ userId: userId, productId: productId })
     *
     * @param userId the user ID
     * @param productId the product ID
     * @return optional containing the review if found
     */
    Optional<Review> findByUserIdAndProductId(String userId, String productId);

    /**
     * Finds all reviews for a product with a specific approval status.
     * MongoDB equivalent: db.reviews.find({ productId: productId, status: status })
     *
     * @param productId the product ID
     * @param status the review status (approved, pending, rejected)
     * @return list of reviews matching criteria
     */
    List<Review> findByProductIdAndStatus(String productId, String status);

    /**
     * Finds all reviews from a user sorted by creation date (newest first).
     * MongoDB equivalent: db.reviews.find({ userId: userId }).sort({ createdAt: -1 })
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return paginated page of user's reviews sorted by creation date descending
     */
    Page<Review> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Counts approved reviews for a specific product.
     * MongoDB equivalent: db.reviews.countDocuments({ productId: productId, status: status })
     *
     * @param productId the product ID
     * @param status the review status
     * @return count of reviews matching criteria
     */
    long countByProductIdAndStatus(String productId, String status);

    /**
     * Finds all reviews for a product with a minimum rating threshold.
     * MongoDB equivalent: db.reviews.find({ productId: productId, rating: { $gte: minRating } })
     *
     * @param productId the product ID
     * @param minRating the minimum rating value (1-5)
     * @return list of reviews with rating >= minRating
     */
    List<Review> findByProductIdAndRatingGreaterThanEqual(String productId, int minRating);
}
