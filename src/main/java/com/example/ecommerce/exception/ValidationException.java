package com.example.ecommerce.exception;

/**
 * Exception thrown for custom validation errors.
 * This is used for business logic validation that goes beyond standard constraint violations.
 * 
 * Exception được throw cho các lỗi xác thực tùy chỉnh.
 * Điều này được sử dụng cho xác thực logic kinh doanh vượt quá các vi phạm ràng buộc tiêu chuẩn.
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs a ValidationException with a detailed message.
     * 
     * @param message the detail message explaining the validation failure
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a ValidationException with a message and cause.
     * 
     * @param message the detail message explaining the validation failure
     * @param cause the cause of the validation failure
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
