package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho xử lý thanh toán
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {

    @NotBlank(message = "ID đơn hàng không được để trống")
    private String orderId;

    @NotBlank(message = "ID người dùng không được để trống")
    private String userId;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải > 0")
    private BigDecimal amount;

    @NotBlank(message = "Mã tiền tệ không được để trống")
    private String currency;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String method;

    private String transactionId;

    private String paymentDetails;
}
