package com.example.ecommerce.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Kafka Producer - Gửi events tới Kafka topics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Gửi order event tới Kafka
     */
    public void sendOrderEvent(String orderId, KafkaOrderEvent event) {
        try {
            Message<KafkaOrderEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, "orders")
                    .setHeader("kafka_messageKey", orderId)
                    .build();

            kafkaTemplate.send(message);
            log.info("Order event sent to Kafka - Order ID: {}, Type: {}", orderId, event.getEventType());
        } catch (Exception e) {
            log.error("Failed to send order event to Kafka - Order ID: {}", orderId, e);
        }
    }

    /**
     * Gửi payment event tới Kafka
     */
    public void sendPaymentEvent(String transactionId, KafkaPaymentEvent event) {
        try {
            Message<KafkaPaymentEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, "payments")
                    .setHeader("kafka_messageKey", transactionId)
                    .build();

            kafkaTemplate.send(message);
            log.info("Payment event sent to Kafka - Transaction ID: {}, Type: {}", transactionId, event.getEventType());
        } catch (Exception e) {
            log.error("Failed to send payment event to Kafka - Transaction ID: {}", transactionId, e);
        }
    }

    /**
     * Gửi review event tới Kafka
     */
    public void sendReviewEvent(String reviewId, KafkaReviewEvent event) {
        try {
            Message<KafkaReviewEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, "reviews")
                    .setHeader("kafka_messageKey", reviewId)
                    .build();

            kafkaTemplate.send(message);
            log.info("Review event sent to Kafka - Review ID: {}, Type: {}", reviewId, event.getEventType());
        } catch (Exception e) {
            log.error("Failed to send review event to Kafka - Review ID: {}", reviewId, e);
        }
    }
}
