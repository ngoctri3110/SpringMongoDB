package com.example.ecommerce.service;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Review;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for managing product reviews.
 * Handles review creation, updates, deletion, and retrieval.
 * Automatically updates product rating and review count.
 * Enforces one-review-per-user-per-product constraint.
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    /**
     * Creates a new product review.
     * Validates that the user hasn't already reviewed this product.
     * Updates the product's average rating and review count.
     *
     * @param userId the user ID creating the review
     * @param productId the product ID being reviewed
     * @param rating the rating (1-5 stars)
     * @param title the review title
     * @param comment the review comment
     * @param verified whether the reviewer is a verified buyer
     * @return the created Review object
     * @throws ResourceNotFoundException if product doesn't exist
     * @throws DuplicateResourceException if user already reviewed this product
     */
    @Transactional
    public Review createReview(String userId, String productId, Integer rating, String title,
                               String comment, Boolean verified) {
        log.info("Creating review for product: {} by user: {}", productId, userId);

        // Verify product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", productId));

        // Check if user already reviewed this product
        Optional<Review> existingReview = reviewRepository.findByUserIdAndProductId(userId, productId);
        if (existingReview.isPresent()) {
            log.warn("User {} has already reviewed product {}", userId, productId);
            throw new DuplicateResourceException(
                    "User has already reviewed this product");
        }

        // Validate rating is between 1-5
        if (rating == null || rating < 1 || rating > 5) {
            log.warn("Invalid rating {} for review", rating);
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Create review
        Review review = Review.builder()
                .userId(userId)
                .productId(productId)
                .rating(rating)
                .title(title)
                .comment(comment)
                .verified(verified != null ? verified : false)
                .status("PENDING")
                .build();

        Review savedReview = reviewRepository.save(review);
        log.debug("Review created with ID: {}", savedReview.getId());

        // Update product rating and review count
        updateProductRating(productId);

        return savedReview;
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param reviewId the review ID
     * @return the Review object
     * @throws ResourceNotFoundException if review doesn't exist
     */
    @Transactional(readOnly = true)
    public Review getReviewById(String reviewId) {
        log.debug("Fetching review: {}", reviewId);
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review", "id", reviewId));
    }

    /**
     * Updates an existing review.
     * Only allows updating rating, title, and comment.
     * Recalculates product rating after update.
     *
     * @param reviewId the review ID to update
     * @param rating the new rating (1-5)
     * @param title the new title
     * @param comment the new comment
     * @return the updated Review object
     * @throws ResourceNotFoundException if review doesn't exist
     */
    @Transactional
    public Review updateReview(String reviewId, Integer rating, String title, String comment) {
        log.info("Updating review: {}", reviewId);

        Review review = getReviewById(reviewId);
        String productId = review.getProductId();

        // Validate rating if provided
        if (rating != null && (rating < 1 || rating > 5)) {
            log.warn("Invalid rating {} for review update", rating);
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Update fields
        if (rating != null) {
            review.setRating(rating);
        }
        if (title != null) {
            review.setTitle(title);
        }
        if (comment != null) {
            review.setComment(comment);
        }

        Review updatedReview = reviewRepository.save(review);
        log.debug("Review updated: {}", reviewId);

        // Recalculate product rating
        updateProductRating(productId);

        return updatedReview;
    }

    /**
     * Deletes a review by its ID.
     * Recalculates product rating after deletion.
     *
     * @param reviewId the review ID to delete
     * @throws ResourceNotFoundException if review doesn't exist
     */
    @Transactional
    public void deleteReview(String reviewId) {
        log.info("Deleting review: {}", reviewId);

        Review review = getReviewById(reviewId);
        String productId = review.getProductId();

        reviewRepository.deleteById(reviewId);
        log.debug("Review deleted: {}", reviewId);

        // Recalculate product rating
        updateProductRating(productId);
    }

    /**
     * Retrieves all reviews for a product with pagination.
     *
     * @param productId the product ID
     * @param pageable pagination information
     * @return Page of Review objects for the product
     */
    @Transactional(readOnly = true)
    public Page<Review> getProductReviews(String productId, Pageable pageable) {
        log.debug("Fetching reviews for product: {} with pageable: {}", productId, pageable);
        return reviewRepository.findByProductId(productId, pageable);
    }

    /**
     * Retrieves approved reviews for a product with pagination.
     *
     * @param productId the product ID
     * @param pageable pagination information
     * @return Page of approved Review objects
     */
    @Transactional(readOnly = true)
    public Page<Review> getApprovedProductReviews(String productId, Pageable pageable) {
        log.debug("Fetching approved reviews for product: {}", productId);
        return reviewRepository.findByProductId(productId, pageable)
                .filter(review -> "APPROVED".equals(review.getStatus()));
    }

    /**
     * Approves a review (typically done by admin).
     *
     * @param reviewId the review ID to approve
     * @return the approved Review object
     * @throws ResourceNotFoundException if review doesn't exist
     */
    @Transactional
    public Review approveReview(String reviewId) {
        log.info("Approving review: {}", reviewId);

        Review review = getReviewById(reviewId);
        review.setStatus("APPROVED");

        Review approvedReview = reviewRepository.save(review);
        updateProductRating(review.getProductId());

        return approvedReview;
    }

    /**
     * Rejects a review (typically done by admin).
     *
     * @param reviewId the review ID to reject
     * @return the rejected Review object
     * @throws ResourceNotFoundException if review doesn't exist
     */
    @Transactional
    public Review rejectReview(String reviewId) {
        log.info("Rejecting review: {}", reviewId);

        Review review = getReviewById(reviewId);
        review.setStatus("REJECTED");

        return reviewRepository.save(review);
    }

    /**
     * Updates product rating and review count based on all approved reviews.
     * Calculates average rating from all approved reviews.
     *
     * @param productId the product ID to update rating for
     */
    private void updateProductRating(String productId) {
        log.debug("Updating product rating for: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", productId));

        // Get all approved reviews
        var approvedReviews = reviewRepository.findByProductIdAndStatus(productId, "APPROVED");

        if (approvedReviews.isEmpty()) {
            product.setAvgRating(0.0);
            product.setReviewCount(0);
        } else {
            // Calculate average rating
            double avgRating = approvedReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            product.setAvgRating(Math.round(avgRating * 10.0) / 10.0); // Round to 1 decimal
            product.setReviewCount(approvedReviews.size());
        }

        productRepository.save(product);
        log.debug("Product rating updated: avgRating={}, reviewCount={}",
                product.getAvgRating(), product.getReviewCount());
    }

    /**
     * Checks if a user has already reviewed a product.
     *
     * @param userId the user ID
     * @param productId the product ID
     * @return true if user has reviewed the product, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean hasUserReviewedProduct(String userId, String productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    /**
     * Retrieves user's review count.
     *
     * @param userId the user ID
     * @return total number of reviews created by the user
     */
    @Transactional(readOnly = true)
    public long getUserReviewCount(String userId) {
        log.debug("Getting review count for user: {}", userId);
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged()).getTotalElements();
    }
}

/**
 * Custom exception for duplicate resources.
 */
class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
