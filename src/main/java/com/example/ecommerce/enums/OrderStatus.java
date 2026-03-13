package com.example.ecommerce.enums;

/**
 * Order status enumeration.
 * 주문 상태 열거형.
 */
public enum OrderStatus {
    PENDING("Pending", "주문 대기 중"),
    PROCESSING("Processing", "주문 처리 중"),
    CONFIRMED("Confirmed", "주문 확정"),
    SHIPPED("Shipped", "배송됨"),
    DELIVERED("Delivered", "배송 완료"),
    CANCELLED("Cancelled", "취소됨"),
    RETURNED("Returned", "반품됨");

    private final String value;
    private final String description;

    OrderStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get OrderStatus from string value (case-insensitive).
     *
     * @param value the string value
     * @return the matching OrderStatus or PENDING if not found
     */
    public static OrderStatus fromValue(String value) {
        if (value == null) return PENDING;
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }
}
