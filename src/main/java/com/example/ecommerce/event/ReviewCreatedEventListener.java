package com.example.ecommerce.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for ReviewCreatedEvent
 * Xử lý sự kiện khi bài review được tạo
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewCreatedEventListener {

    /**
     * Handle review created event
     * Kích hoạt khi ReviewCreatedEvent được publish
     *
     * @param event ReviewCreatedEvent
     */
    @EventListener
    public void onReviewCreated(ReviewCreatedEvent event) {
        log.info("Review created event received - Product ID: {}, Rating: {}, User ID: {}", 
                 event.getProductId(), event.getRating(), event.getUserId());

        // Update product average rating (TODO: implement in ProductService)
        log.info("Updating product rating for product: {}", event.getProductId());

        // Increment review count
        log.info("Incrementing review count for product: {}", event.getProductId());

        // Update search index (TODO: implement)
        log.info("Updating search index with new review");

        // Notify product owner (TODO: implement notification)
        log.info("Notifying product seller of new review");

        // Check for spam (TODO: implement spam detection)
        log.info("Checking review for spam patterns");

        // Update analytics
        log.info("Recording review creation in analytics");
    }
}
