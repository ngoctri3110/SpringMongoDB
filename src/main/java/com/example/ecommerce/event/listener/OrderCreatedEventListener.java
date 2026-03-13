package com.example.ecommerce.event.listener;

import com.example.ecommerce.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener xử lý sự kiện tạo đơn hàng (OrderCreatedEvent).
 * Thực hiện các tác vụ liên quan đến việc tạo đơn hàng như:
 * - Ghi nhật ký sự kiện
 * - Gửi thông báo cho khách hàng
 * - Dự trữ hàng tồn kho
 *
 * <p>Listener này được cấu hình để xử lý sự kiện không đồng bộ (asynchronous)
 * để tránh làm chậm quá trình tạo đơn hàng.
 *
 * <p><b>Cấu hình không đồng bộ:</b>
 * <pre>
 * Để sử dụng tính năng @Async, đảm bảo rằng:
 * 1. @EnableAsync được kích hoạt trong ứng dụng Spring Boot
 * 2. TaskExecutor bean được cấu hình với thread pool phù hợp
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

    /**
     * Xử lý sự kiện OrderCreatedEvent.
     * Phương thức này được kích hoạt tự động khi OrderCreatedEvent được phát hành.
     *
     * <p>Các tác vụ được thực hiện:
     * <ol>
     *   <li>Ghi nhật ký thông tin đơn hàng được tạo</li>
     *   <li>Gửi email/SMS thông báo cho khách hàng</li>
     *   <li>Dự trữ hàng tồn kho cho các sản phẩm trong đơn hàng</li>
     *   <li>Cập nhật thống kê bán hàng theo thời gian thực</li>
     * </ol>
     *
     * @param event Sự kiện chứa thông tin đơn hàng được tạo
     */
    @EventListener
    @Async("taskExecutor")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("================================");
        log.info("Processing OrderCreatedEvent for Order: {}", event.getOrderId());
        log.info("Order Number: {}, User ID: {}, Total Amount: {}",
                event.getOrderNumber(), event.getUserId(), event.getTotalAmount());
        log.info("Event Timestamp: {}", event.getTimestamp());

        try {
            // Task 1: Log the order creation event
            log.debug("Logging order creation event for order ID: {}", event.getOrderId());
            logOrderCreation(event);

            // Task 2: Send notification to customer
            log.debug("Sending notification for order: {}", event.getOrderId());
            sendNotification(event);

            // Task 3: Reserve inventory
            log.debug("Reserving inventory for order: {}", event.getOrderId());
            reserveInventory(event);

            log.info("OrderCreatedEvent processed successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order: {}", event.getOrderId(), e);
        }
        log.info("================================");
    }

    /**
     * Ghi nhật ký sự kiện tạo đơn hàng.
     *
     * @param event Sự kiện tạo đơn hàng
     */
    private void logOrderCreation(OrderCreatedEvent event) {
        log.info("[ORDER_CREATED] Order {} created by user {} with total amount: ${}",
                event.getOrderId(), event.getUserId(), event.getTotalAmount());
        // TODO: Persist audit log to database
    }

    /**
     * Gửi thông báo tạo đơn hàng cho khách hàng.
     * Có thể là email, SMS, hoặc push notification.
     *
     * @param event Sự kiện tạo đơn hàng
     */
    private void sendNotification(OrderCreatedEvent event) {
        log.info("Sending order confirmation notification to user: {}", event.getUserId());
        // TODO: Integrate with notification service (email/SMS/push)
        // emailService.sendOrderConfirmation(event.getUserId(), event.getOrderId());
        // smsService.sendOrderSMS(event.getUserId(), event.getOrderId());
    }

    /**
     * Dự trữ hàng tồn kho cho các sản phẩm trong đơn hàng.
     *
     * @param event Sự kiện tạo đơn hàng
     */
    private void reserveInventory(OrderCreatedEvent event) {
        log.info("Reserving inventory for order: {}", event.getOrderId());
        // TODO: Integrate with inventory service
        // inventoryService.reserveItems(event.getOrderId());
    }
}
