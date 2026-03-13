package com.example.ecommerce.enums;

/**
 * Payment status enumeration.
 * 결제 상태 열거형.
 */
public enum PaymentStatus {
    PENDING("Pending", "결제 대기 중"),
    COMPLETED("Completed", "결제 완료"),
    FAILED("Failed", "결제 실패"),
    REFUNDED("Refunded", "환불됨"),
    CANCELLED("Cancelled", "취소됨");

    private final String value;
    private final String description;

    PaymentStatus(String value, String description) {
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
     * Get PaymentStatus from string value (case-insensitive).
     *
     * @param value the string value
     * @return the matching PaymentStatus or PENDING if not found
     */
    public static PaymentStatus fromValue(String value) {
        if (value == null) return PENDING;
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }
}
