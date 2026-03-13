package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.ProcessPaymentRequest;
import com.example.ecommerce.dto.request.RefundPaymentRequest;
import com.example.ecommerce.dto.response.PaymentTransactionResponse;
import com.example.ecommerce.event.EventPublisher;
import com.example.ecommerce.exception.PaymentException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.PaymentTransaction;
import com.example.ecommerce.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Payment Transaction Service - Xử lý logic nghiệp vụ cho thanh toán
 * Quản lý xử lý thanh toán, hoàn tiền, và lịch sử giao dịch
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final EventPublisher eventPublisher;

    /**
     * Xử lý thanh toán cho đơn hàng
     *
     * @param request ProcessPaymentRequest chứa thông tin thanh toán
     * @return PaymentTransactionResponse
     * @throws PaymentException nếu thanh toán thất bại
     */
    public PaymentTransactionResponse processPayment(ProcessPaymentRequest request) {
        log.info("Processing payment for order: {}, amount: {}, method: {}", 
                 request.getOrderId(), request.getAmount(), request.getPaymentMethod());

        try {
            // Validate payment request
            if (request.getAmount() == null || request.getAmount().signum() <= 0) {
                throw new PaymentException("Invalid payment amount");
            }

            // Create payment transaction
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .orderId(request.getOrderId())
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .transactionId(request.getTransactionId())
                    .status("COMPLETED")
                    .build();

            PaymentTransaction savedTransaction = paymentTransactionRepository.save(transaction);
            log.info("Payment processed successfully for order: {}, transactionId: {}", 
                     request.getOrderId(), savedTransaction.getId());

            // Publish event
            eventPublisher.publishPaymentProcessedEvent(savedTransaction, "SUCCESS");

            return mapToResponse(savedTransaction);
        } catch (Exception e) {
            log.error("Error processing payment for order: {}", request.getOrderId(), e);
            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Lấy thanh toán theo ID
     *
     * @param id Payment ID
     * @return PaymentTransactionResponse
     * @throws ResourceNotFoundException nếu thanh toán không tồn tại
     */
    @Transactional(readOnly = true)
    public PaymentTransactionResponse getPaymentById(String id) {
        log.info("Fetching payment with ID: {}", id);

        try {
            PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentTransaction", "id", id));

            log.info("Payment found with ID: {}", id);
            return mapToResponse(transaction);
        } catch (Exception e) {
            log.error("Error fetching payment: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Hoàn tiền cho giao dịch thanh toán
     *
     * @param id Payment ID
     * @param request RefundPaymentRequest chứa lý do hoàn tiền
     * @return PaymentTransactionResponse
     * @throws ResourceNotFoundException nếu thanh toán không tồn tại
     * @throws PaymentException nếu không thể hoàn tiền
     */
    public PaymentTransactionResponse refundPayment(String id, RefundPaymentRequest request) {
        log.info("Processing refund for payment: {}, reason: {}", id, request.getReason());

        try {
            PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentTransaction", "id", id));

            // Check if transaction can be refunded
            if ("REFUNDED".equalsIgnoreCase(transaction.getStatus())) {
                throw new PaymentException("Payment already refunded");
            }

            if (!"COMPLETED".equalsIgnoreCase(transaction.getStatus())) {
                throw new PaymentException("Only completed payments can be refunded");
            }

            // Update status
            transaction.setStatus("REFUNDED");
            transaction.setUpdatedAt(LocalDateTime.now());

            PaymentTransaction updatedTransaction = paymentTransactionRepository.save(transaction);
            log.info("Payment refunded successfully: {}", id);

            // Publish event
            eventPublisher.publishPaymentProcessedEvent(updatedTransaction, "REFUNDED");

            return mapToResponse(updatedTransaction);
        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy danh sách thanh toán theo Order ID
     *
     * @param orderId Order ID
     * @return List<PaymentTransaction>
     */
    @Transactional(readOnly = true)
    public List<PaymentTransaction> getPaymentsByOrderId(String orderId) {
        log.info("Fetching payments for order: {}", orderId);

        try {
            List<PaymentTransaction> transactions = paymentTransactionRepository
                    .findByOrderIdOrderByCreatedAtDesc(orderId);

            log.info("Found {} payments for order: {}", transactions.size(), orderId);
            return transactions;
        } catch (Exception e) {
            log.error("Error fetching payments by order: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy danh sách thanh toán theo User ID (phân trang)
     *
     * @param userId User ID
     * @param pageable Pageable chứa thông tin phân trang
     * @return Page<PaymentTransactionResponse>
     */
    @Transactional(readOnly = true)
    public Page<PaymentTransactionResponse> getPaymentsByUserId(String userId, Pageable pageable) {
        log.info("Fetching payments for user: {} with pagination - page: {}, size: {}", 
                 userId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<PaymentTransaction> transactions = paymentTransactionRepository
                    .findByUserIdOrderByCreatedAtDesc(userId, pageable);

            Page<PaymentTransactionResponse> responses = transactions.map(this::mapToResponse);

            log.info("Found {} payments for user: {}", responses.getTotalElements(), userId);
            return responses;
        } catch (Exception e) {
            log.error("Error fetching payments by user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Map PaymentTransaction entity sang PaymentTransactionResponse DTO
     *
     * @param transaction PaymentTransaction entity
     * @return PaymentTransactionResponse
     */
    private PaymentTransactionResponse mapToResponse(PaymentTransaction transaction) {
        return PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .orderId(transaction.getOrderId())
                .userId(transaction.getUserId())
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .transactionId(transaction.getTransactionId())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
