package com.example.ecommerce.repository;

import com.example.ecommerce.model.PaymentTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for PaymentTransaction entity.
 * Provides database operations for managing payment transactions in MongoDB.
 * 
 * @author E-Commerce Platform
 * @version 1.0
 */
@Repository
@Slf4j
public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String> {

    /**
     * Finds a payment transaction associated with a specific order.
     * MongoDB equivalent: db.paymentTransactions.findOne({ orderId: orderId })
     *
     * @param orderId the order ID
     * @return optional containing the payment transaction if found
     */
    Optional<PaymentTransaction> findByOrderId(String orderId);

    /**
     * Finds all payment transactions for a specific user with pagination support.
     * MongoDB equivalent: db.paymentTransactions.find({ userId: userId })
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return paginated page of payment transactions for the user
     */
    Page<PaymentTransaction> findByUserId(String userId, Pageable pageable);

    /**
     * Finds all payment transactions with a specific status with pagination.
     * MongoDB equivalent: db.paymentTransactions.find({ status: status })
     *
     * @param status the transaction status (success, failed, pending, etc.)
     * @param pageable pagination information
     * @return paginated page of transactions with given status
     */
    Page<PaymentTransaction> findByStatus(String status, Pageable pageable);

    /**
     * Finds payment transactions for a user with specific status, sorted by creation date (newest first).
     * MongoDB equivalent: db.paymentTransactions.find({ userId: userId, status: status }).sort({ createdAt: -1 })
     *
     * @param userId the user ID
     * @param status the transaction status
     * @param pageable pagination information
     * @return paginated page of transactions sorted by creation date descending
     */
    Page<PaymentTransaction> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status, Pageable pageable);

    /**
     * Counts payment transactions with a specific status within a date range.
     * MongoDB equivalent: db.paymentTransactions.countDocuments({ status: status, createdAt: { $gte: from, $lte: to } })
     *
     * @param status the transaction status
     * @param from the start date (inclusive)
     * @param to the end date (inclusive)
     * @return count of transactions matching criteria
     */
    long countByStatusAndCreatedAtBetween(String status, LocalDateTime from, LocalDateTime to);
}
