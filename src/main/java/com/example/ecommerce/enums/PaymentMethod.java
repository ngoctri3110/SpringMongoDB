package com.example.ecommerce.enums;

/**
 * Payment method enumeration.
 * 결제 방법 열거형.
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card", "신용카드"),
    DEBIT_CARD("Debit Card", "체크카드"),
    PAYPAL("PayPal", "페이팔"),
    BANK_TRANSFER("Bank Transfer", "계좌이체"),
    WALLET("Digital Wallet", "디지털 지갑"),
    COD("Cash on Delivery", "착불");

    private final String value;
    private final String description;

    PaymentMethod(String value, String description) {
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
     * Get PaymentMethod from string value (case-insensitive).
     *
     * @param value the string value
     * @return the matching PaymentMethod or CREDIT_CARD if not found
     */
    public static PaymentMethod fromValue(String value) {
        if (value == null) return CREDIT_CARD;
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(value) || method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        return CREDIT_CARD;
    }
}
