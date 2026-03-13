package com.example.ecommerce.event;

import com.example.ecommerce.model.Review;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Event triggered when a review is created.
 * 리뷰가 생성될 때 발생하는 이벤트.
 */
@Getter
public class ReviewCreatedEvent extends ApplicationEvent {

    private final String reviewId;
    private final String productId;
    private final String userId;
    private final int rating;
    private final LocalDateTime timestamp;

    /**
     * Create a ReviewCreatedEvent from a Review entity.
     *
     * @param source the Review entity
     * @param review the Review object containing event data
     */
    public ReviewCreatedEvent(Object source, Review review) {
        super(source);
        this.reviewId = review.getId();
        this.productId = review.getProductId();
        this.userId = review.getUserId();
        this.rating = review.getRating();
        this.timestamp = LocalDateTime.now();
    }
}
