package com.example.ecommerce.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer - Nhận events từ Kafka topics
 * Xử lý các sự kiện bất đồng bộ từ các hệ thống khác
 */
@Service
@Slf4j
public class KafkaConsumer {

    /**
     * Nhận order events từ topic 'orders'
     */
    @KafkaListener(
            topics = "orders",
            groupId = "order-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(KafkaOrderEvent event) {
        try {
            log.info("Received Order Event: OrderID={}, Type={}, Status={}", 
                    event.getOrderId(), event.getEventType(), event.getStatus());

            // Xử lý order event
            switch (event.getEventType()) {
                case "CREATED":
                    log.info("Processing ORDER_CREATED: {}", event.getOrderId());
                    // Có thể gửi notification, update inventory, etc.
                    break;
                case "CONFIRMED":
                    log.info("Processing ORDER_CONFIRMED: {}", event.getOrderId());
                    break;
                case "SHIPPED":
                    log.info("Processing ORDER_SHIPPED: {}", event.getOrderId());
                    // Có thể gửi email cho customer
                    break;
                case "DELIVERED":
                    log.info("Processing ORDER_DELIVERED: {}", event.getOrderId());
                    break;
                case "CANCELLED":
                    log.info("Processing ORDER_CANCELLED: {}", event.getOrderId());
                    break;
                default:
                    log.warn("Unknown order event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing order event", e);
        }
    }

    /**
     * Nhận payment events từ topic 'payments'
     */
    @KafkaListener(
            topics = "payments",
            groupId = "payment-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentEvent(KafkaPaymentEvent event) {
        try {
            log.info("Received Payment Event: TransactionID={}, Type={}, Status={}", 
                    event.getTransactionId(), event.getEventType(), event.getStatus());

            // Xử lý payment event
            switch (event.getEventType()) {
                case "INITIATED":
                    log.info("Payment initiated: {}", event.getTransactionId());
                    break;
                case "PROCESSING":
                    log.info("Payment processing: {}", event.getTransactionId());
                    break;
                case "COMPLETED":
                    log.info("Payment completed: {} for order: {}", event.getTransactionId(), event.getOrderId());
                    // Có thể update order status
                    break;
                case "FAILED":
                    log.info("Payment failed: {}", event.getTransactionId());
                    // Có thể send notification to user
                    break;
                case "REFUNDED":
                    log.info("Payment refunded: {}", event.getTransactionId());
                    break;
                default:
                    log.warn("Unknown payment event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing payment event", e);
        }
    }

    /**
     * Nhận review events từ topic 'reviews'
     */
    @KafkaListener(
            topics = "reviews",
            groupId = "review-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeReviewEvent(KafkaReviewEvent event) {
        try {
            log.info("Received Review Event: ReviewID={}, Type={}, ProductID={}", 
                    event.getReviewId(), event.getEventType(), event.getProductId());

            // Xử lý review event
            switch (event.getEventType()) {
                case "CREATED":
                    log.info("Review created for product: {}", event.getProductId());
                    // Có thể update product rating
                    break;
                case "UPDATED":
                    log.info("Review updated: {}", event.getReviewId());
                    break;
                case "APPROVED":
                    log.info("Review approved: {}", event.getReviewId());
                    // Có thể notify user
                    break;
                case "REJECTED":
                    log.info("Review rejected: {}", event.getReviewId());
                    break;
                default:
                    log.warn("Unknown review event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing review event", e);
        }
    }
}
