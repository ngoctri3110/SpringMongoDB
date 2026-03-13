package com.example.ecommerce.enums;

/**
 * Product status enumeration.
 * 상품 상태 열거형.
 */
public enum ProductStatus {
    ACTIVE("Active", "활성"),
    INACTIVE("Inactive", "비활성"),
    OUT_OF_STOCK("Out of Stock", "재고 없음"),
    DISCONTINUED("Discontinued", "단종됨");

    private final String value;
    private final String description;

    ProductStatus(String value, String description) {
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
     * Get ProductStatus from string value (case-insensitive).
     *
     * @param value the string value
     * @return the matching ProductStatus or ACTIVE if not found
     */
    public static ProductStatus fromValue(String value) {
        if (value == null) return ACTIVE;
        for (ProductStatus status : ProductStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
