package com.example.ecommerce.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for OrderCreatedEvent
 * Xử lý sự kiện khi đơn hàng được tạo
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventListener {

    /**
     * Handle order created event
     * Kích hoạt khi OrderCreatedEvent được publish
     *
     * @param event OrderCreatedEvent
     */
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Order created event received - Order ID: {}, Order Number: {}, Total: {}", 
                 event.getOrderId(), event.getOrderNumber(), event.getTotalAmount());

        // Log order creation
        log.info("Processing order creation for user: {}", event.getUserId());

        // Trigger inventory reservation (TODO: implement)
        log.info("Reserving inventory for order: {}", event.getOrderNumber());

        // Send notification (TODO: implement notification service)
        log.info("Sending order confirmation notification to user: {}", event.getUserId());

        // Update analytics (TODO: implement)
        log.info("Recording order creation in analytics");
    }
}
