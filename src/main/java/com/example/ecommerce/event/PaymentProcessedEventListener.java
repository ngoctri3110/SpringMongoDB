package com.example.ecommerce.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for PaymentProcessedEvent
 * Xử lý sự kiện khi thanh toán được xử lý
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessedEventListener {

    /**
     * Handle payment processed event
     * Kích hoạt khi PaymentProcessedEvent được publish
     *
     * @param event PaymentProcessedEvent
     */
    @EventListener
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        log.info("Payment processed event received - Order ID: {}, Payment ID: {}, Status: {}", 
                 event.getOrderId(), event.getPaymentId(), event.getStatus());

        if ("SUCCESS".equalsIgnoreCase(event.getStatus())) {
            log.info("Payment successful for order: {}", event.getOrderId());

            // Update order status to CONFIRMED/PROCESSING
            log.info("Updating order status to CONFIRMED");

            // Send payment receipt email (TODO: implement email service)
            log.info("Sending payment receipt to customer");

            // Trigger shipment preparation (TODO: implement)
            log.info("Preparing shipment for order: {}", event.getOrderId());
        } else {
            log.warn("Payment failed for order: {}", event.getOrderId());

            // Send payment failure notification
            log.info("Notifying user of payment failure");

            // Trigger order cancellation process (TODO: implement)
            log.info("Initiating order cancellation for failed payment");
        }

        // Update analytics
        log.info("Recording payment event in analytics");
    }
}
