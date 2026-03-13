package com.example.ecommerce.exception;

/**
 * Exception thrown when inventory is not available for a product.
 * Occurs when a customer attempts to order more quantity than available stock.
 * 
 * Exception được throw khi hàng hóa không có sẵn.
 * Xảy ra khi khách hàng cố gắng đặt hàng với số lượng vượt quá tồn kho.
 */
public class InsufficientStockException extends RuntimeException {

    private final String productId;
    private final int requested;
    private final int available;

    /**
     * Constructs an InsufficientStockException with detailed stock information.
     * 
     * @param productId the ID of the product with insufficient stock
     * @param requested the quantity requested by the customer
     * @param available the quantity currently available in stock
     */
    public InsufficientStockException(String productId, int requested, int available) {
        super(String.format("Insufficient stock for product %s: requested %d, but only %d available",
                productId, requested, available));
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }

    public String getProductId() {
        return productId;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}
