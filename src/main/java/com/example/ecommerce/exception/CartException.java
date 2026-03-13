package com.example.ecommerce.exception;

/**
 * Exception thrown for cart operation failures.
 * This includes adding/removing items, updating quantities, clearing cart, etc.
 * 
 * Exception được throw khi thao tác giỏ hàng thất bại.
 * Điều này bao gồm thêm/xóa mục, cập nhật số lượng, xóa giỏ, v.v.
 */
public class CartException extends RuntimeException {

    /**
     * Constructs a CartException with a detailed message.
     * 
     * @param message the detail message explaining the cart operation failure
     */
    public CartException(String message) {
        super(message);
    }

    /**
     * Constructs a CartException with a message and cause.
     * 
     * @param message the detail message explaining the cart operation failure
     * @param cause the cause of the cart operation failure
     */
    public CartException(String message, Throwable cause) {
        super(message, cause);
    }
}
