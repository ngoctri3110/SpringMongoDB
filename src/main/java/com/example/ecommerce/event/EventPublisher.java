package com.example.ecommerce.event;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.PaymentTransaction;
import com.example.ecommerce.model.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Service for publishing domain events.
 * 도메인 이벤트를 발행하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Publish an order created event.
     *
     * @param order the Order entity
     */
    public void publishOrderCreatedEvent(Order order) {
        log.info("Publishing OrderCreatedEvent for order: {}", order.getOrderNumber());
        eventPublisher.publishEvent(new OrderCreatedEvent(this, order));
    }

    /**
     * Publish a payment processed event.
     *
     * @param transaction the PaymentTransaction entity
     * @param status SUCCESS or FAILURE
     */
    public void publishPaymentProcessedEvent(PaymentTransaction transaction, String status) {
        log.info("Publishing PaymentProcessedEvent for payment: {}, status: {}", transaction.getId(), status);
        eventPublisher.publishEvent(new PaymentProcessedEvent(this, transaction.getOrderId(), 
                transaction.getId(), transaction.getAmount(), status));
    }

    /**
     * Publish a review created event.
     *
     * @param review the Review entity
     */
    public void publishReviewCreatedEvent(Review review) {
        log.info("Publishing ReviewCreatedEvent for product: {}", review.getProductId());
        eventPublisher.publishEvent(new ReviewCreatedEvent(this, review));
    }
}
