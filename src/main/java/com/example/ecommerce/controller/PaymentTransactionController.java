package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.ProcessPaymentRequest;
import com.example.ecommerce.dto.request.RefundPaymentRequest;
import com.example.ecommerce.dto.response.PagedResponse;
import com.example.ecommerce.dto.response.PaymentTransactionResponse;
import com.example.ecommerce.service.PaymentTransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Payment Transaction Controller - REST endpoints cho Payment Transaction
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    /**
     * POST /api/v1/payments - Xử lý thanh toán
     * 
     * @param request ProcessPaymentRequest
     * @return ResponseEntity<PaymentTransactionResponse> với status 201 Created
     */
    @PostMapping
    public ResponseEntity<PaymentTransactionResponse> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {
        log.info("POST /api/v1/payments - Processing payment");
        PaymentTransactionResponse response = paymentTransactionService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/payments/{id} - Lấy payment transaction theo ID
     * 
     * @param id Payment Transaction ID
     * @return ResponseEntity<PaymentTransactionResponse> với status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransactionResponse> getPaymentById(@PathVariable String id) {
        log.info("GET /api/v1/payments/{} - Fetching payment transaction", id);
        PaymentTransactionResponse response = paymentTransactionService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/payments/order/{orderId} - Lấy payment transactions theo Order ID
     * 
     * @param orderId Order ID
     * @return ResponseEntity<PagedResponse<PaymentTransactionResponse>> với status 200 OK
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PagedResponse<PaymentTransactionResponse>> getPaymentsByOrderId(
            @PathVariable String orderId) {
        log.info("GET /api/v1/payments/order/{} - Fetching payments by order", orderId);
        Page<PaymentTransactionResponse> pageResponse = paymentTransactionService.getPaymentsByOrderId(orderId);
        
        PagedResponse<PaymentTransactionResponse> response = PagedResponse.<PaymentTransactionResponse>builder()
                .content(pageResponse.getContent())
                .page(pageResponse.getNumber())
                .size(pageResponse.getSize())
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages())
                .last(pageResponse.isLast())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/payments/user/{userId} - Lấy payment transactions của user (phân trang)
     * 
     * @param userId User ID
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<PaymentTransactionResponse>> với status 200 OK
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<PaymentTransactionResponse>> getPaymentsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/payments/user/{} - Fetching payments by user with page: {}, size: {}", 
                userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentTransactionResponse> pageResponse = paymentTransactionService.getPaymentsByUserId(userId, pageable);
        
        PagedResponse<PaymentTransactionResponse> response = PagedResponse.<PaymentTransactionResponse>builder()
                .content(pageResponse.getContent())
                .page(pageResponse.getNumber())
                .size(pageResponse.getSize())
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages())
                .last(pageResponse.isLast())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/payments/{id}/refund - Hoàn lại tiền (refund payment)
     * 
     * @param id Payment Transaction ID
     * @param request RefundPaymentRequest
     * @return ResponseEntity<PaymentTransactionResponse> với status 200 OK
     */
    @PutMapping("/{id}/refund")
    public ResponseEntity<PaymentTransactionResponse> refundPayment(
            @PathVariable String id,
            @Valid @RequestBody RefundPaymentRequest request) {
        log.info("PUT /api/v1/payments/{}/refund - Refunding payment", id);
        PaymentTransactionResponse response = paymentTransactionService.refundPayment(id, request);
        return ResponseEntity.ok(response);
    }
}
