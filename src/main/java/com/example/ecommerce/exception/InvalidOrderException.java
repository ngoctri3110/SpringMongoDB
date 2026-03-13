package com.example.ecommerce.exception;

/**
 * Exception thrown when an invalid order operation is attempted.
 * This can include invalid state transitions, empty orders, invalid items, etc.
 * 
 * Exception được throw khi thao tác đặt hàng không hợp lệ được thực hiện.
 * Điều này có thể bao gồm chuyển đổi trạng thái không hợp lệ, đơn hàng trống, mục không hợp lệ, v.v.
 */
public class InvalidOrderException extends RuntimeException {

    /**
     * Constructs an InvalidOrderException with a detailed message.
     * 
     * @param message the detail message explaining the order operation failure
     */
    public InvalidOrderException(String message) {
        super(message);
    }
}
