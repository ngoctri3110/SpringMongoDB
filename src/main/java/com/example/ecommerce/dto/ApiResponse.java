package com.example.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper/envelope.
 * 일반적인 API 응답 래퍼/봉투.
 *
 * @param <T> the type of data being wrapped
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates whether the request was successful.
     */
    private boolean success;

    /**
     * Response message describing the result.
     */
    private String message;

    /**
     * The actual response data.
     */
    private T data;

    /**
     * Server timestamp when the response was generated.
     */
    private LocalDateTime timestamp;

    /**
     * Error code for failed requests (e.g., ERR_001, ERR_002).
     */
    private String code;

    /**
     * Create a successful response with data.
     *
     * @param data the response data
     * @param <T> the type of data
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create a successful response with data and custom message.
     *
     * @param data the response data
     * @param message custom success message
     * @param <T> the type of data
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create an error response with message and code.
     *
     * @param message error message
     * @param code error code (e.g., ERR_001)
     * @param <T> the type of data
     * @return ApiResponse with success=false
     */
    public static <T> ApiResponse<T> error(String message, String code) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create an error response with message only.
     *
     * @param message error message
     * @param <T> the type of data
     * @return ApiResponse with success=false
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
