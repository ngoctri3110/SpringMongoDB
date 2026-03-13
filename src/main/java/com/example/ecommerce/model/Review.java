package com.example.ecommerce.model;

import com.example.ecommerce.model.base.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * Review document representing a customer review for a product.
 * Extends BaseDocument to get createdAt and updatedAt audit fields.
 */
@Document(collection = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Review extends BaseDocument {

    /**
     * The unique identifier for this review (MongoDB ObjectId).
     */
    @Id
    private String id;

    /**
     * The ID of the user who wrote the review.
     */
    private String userId;

    /**
     * The ID of the product being reviewed.
     */
    @Indexed
    private String productId;

    /**
     * Rating given by the reviewer (1-5 stars).
     * Validation should ensure this is between 1 and 5.
     */
    private Integer rating;

    /**
     * Title/summary of the review.
     */
    private String title;

    /**
     * Detailed comment/content of the review.
     */
    private String comment;

    /**
     * List of user IDs who found this review helpful.
     */
    private List<String> helpful;

    /**
     * Flag indicating if the reviewer is a verified buyer.
     */
    private Boolean verified;

    /**
     * Review status (PENDING, APPROVED, REJECTED)
     * TODO: Consider using an enum in future versions for type safety
     */
    private String status;

    /**
     * Convenience constructor for creating a review with essential fields.
     *
     * @param userId the user ID
     * @param productId the product ID
     * @param rating the rating (1-5)
     * @param title the review title
     * @param comment the review comment
     */
    public Review(String userId, String productId, Integer rating, String title, String comment) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.verified = false;
        this.status = "PENDING";
    }

    /**
     * Convenience constructor with verified flag.
     *
     * @param userId the user ID
     * @param productId the product ID
     * @param rating the rating (1-5)
     * @param title the review title
     * @param comment the review comment
     * @param verified whether the reviewer is a verified buyer
     */
    public Review(String userId, String productId, Integer rating, String title, 
                  String comment, Boolean verified) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.verified = verified;
        this.status = "PENDING";
    }
}
