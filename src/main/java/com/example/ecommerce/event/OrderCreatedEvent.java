package com.example.ecommerce.event;

import com.example.ecommerce.model.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event triggered when an order is created.
 * 주문이 생성될 때 발생하는 이벤트.
 */
@Getter
public class OrderCreatedEvent extends ApplicationEvent {

    private final String orderId;
    private final String userId;
    private final String orderNumber;
    private final BigDecimal totalAmount;
    private final LocalDateTime timestamp;

    /**
     * Create an OrderCreatedEvent from an Order entity.
     *
     * @param source the Order entity
     * @param order the Order object containing event data
     */
    public OrderCreatedEvent(Object source, Order order) {
        super(source);
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.orderNumber = order.getOrderNumber();
        this.totalAmount = order.getTotalAmount();
        this.timestamp = LocalDateTime.now();
    }
}
