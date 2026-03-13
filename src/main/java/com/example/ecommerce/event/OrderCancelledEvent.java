package com.example.ecommerce.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Sự kiện được phát hành khi một đơn hàng bị hủy.
 * Event này thông báo cho các listener về việc hủy đơn hàng,
 * bao gồm lý do hủy và số tiền hoàn lại cho khách hàng.
 *
 * <p>Event được sử dụng để:
 * <ul>
 *   <li>Hoàn lại thanh toán cho khách hàng</li>
 *   <li>Giải phóng hàng tồn kho đã dự trữ</li>
 *   <li>Gửi email xác nhận hủy đơn hàng</li>
 *   <li>Cập nhật thống kê bán hàng</li>
 * </ul>
 *
 * <p><b>Sử dụng không đồng bộ:</b>
 * <pre>
 * @EventListener
 * @Async("taskExecutor")
 * public void onOrderCancelled(OrderCancelledEvent event) {
 *     refundService.processRefund(event.getOrderId(), event.getRefundAmount());
 *     inventoryService.releaseReservedItems(event.getOrderId());
 * }
 * </pre>
 */
@Getter
public class OrderCancelledEvent extends ApplicationEvent {
    private final String orderId;
    private final String reason;
    private final Double refundAmount;
    private final LocalDateTime cancelledAt;

    /**
     * Khởi tạo sự kiện hủy đơn hàng.
     *
     * @param source        Nguồn của sự kiện
     * @param orderId       Mã định danh của đơn hàng
     * @param reason        Lý do hủy đơn hàng
     * @param refundAmount  Số tiền hoàn lại cho khách hàng
     */
    public OrderCancelledEvent(Object source, String orderId, String reason, Double refundAmount) {
        super(source);
        this.orderId = orderId;
        this.reason = reason;
        this.refundAmount = refundAmount;
        this.cancelledAt = LocalDateTime.now();
    }
}
