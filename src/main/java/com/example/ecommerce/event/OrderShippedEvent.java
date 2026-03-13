package com.example.ecommerce.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Sự kiện được phát hành khi một đơn hàng được vận chuyển.
 * Event này thông báo cho các listener về trạng thái vận chuyển của đơn hàng,
 * bao gồm số theo dõi và thời gian dự kiến giao hàng.
 *
 * <p>Event được sử dụng để:
 * <ul>
 *   <li>Gửi email thông báo vận chuyển đến khách hàng</li>
 *   <li>Cập nhật trạng thái đơn hàng trong hệ thống</li>
 *   <li>Ghi nhật ký theo dõi vận chuyển</li>
 * </ul>
 *
 * <p><b>Sử dụng không đồng bộ:</b>
 * <pre>
 * @EventListener
 * @Async
 * public void onOrderShipped(OrderShippedEvent event) {
 *     notificationService.sendShippingNotification(event.getOrderId(), event.getTrackingNumber());
 * }
 * </pre>
 */
@Getter
public class OrderShippedEvent extends ApplicationEvent {
    private final String orderId;
    private final String trackingNumber;
    private final LocalDateTime estimatedDelivery;

    /**
     * Khởi tạo sự kiện vận chuyển đơn hàng.
     *
     * @param source              Nguồn của sự kiện
     * @param orderId             Mã định danh của đơn hàng
     * @param trackingNumber      Số theo dõi vận chuyển
     * @param estimatedDelivery   Thời gian giao hàng dự kiến
     */
    public OrderShippedEvent(Object source, String orderId, String trackingNumber, LocalDateTime estimatedDelivery) {
        super(source);
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.estimatedDelivery = estimatedDelivery;
    }
}
