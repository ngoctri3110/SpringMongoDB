package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity.
 * Provides database operations for managing orders in MongoDB.
 * 
 * @author E-Commerce Platform
 * @version 1.0
 */
@Repository
@Slf4j
public interface OrderRepository extends MongoRepository<Order, String>, OrderRepositoryCustom {

    /**
     * Finds all orders for a specific user with pagination support.
     * MongoDB equivalent: db.orders.find({ userId: userId })
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return paginated page of orders for the user
     */
    Page<Order> findByUserId(String userId, Pageable pageable);

    /**
     * Finds a specific order by its order number.
     * MongoDB equivalent: db.orders.findOne({ orderNumber: orderNumber })
     *
     * @param orderNumber the order number
     * @return optional containing the order if found
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Finds orders for a user with a specific status with pagination.
     * MongoDB equivalent: db.orders.find({ userId: userId, status: status })
     *
     * @param userId the user ID
     * @param status the order status
     * @param pageable pagination information
     * @return paginated page of orders matching criteria
     */
    Page<Order> findByUserIdAndStatus(String userId, String status, Pageable pageable);

    /**
     * Finds orders with a specific status, sorted by creation date (newest first).
     * MongoDB equivalent: db.orders.find({ status: status }).sort({ createdAt: -1 })
     *
     * @param status the order status
     * @param pageable pagination information
     * @return paginated page of orders sorted by creation date descending
     */
    Page<Order> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    /**
     * Finds all orders for a user sorted by creation date (newest first).
     * MongoDB equivalent: db.orders.find({ userId: userId }).sort({ createdAt: -1 })
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return paginated page of user's orders sorted by creation date descending
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Counts the number of orders with a specific status.
     * MongoDB equivalent: db.orders.countDocuments({ status: status })
     *
     * @param status the order status
     * @return count of orders with the given status
     */
    long countByStatus(String status);

    /**
     * Finds the most recent orders with a limit using custom MongoDB query.
     * MongoDB equivalent: db.orders.find({}).sort({ createdAt: -1 }).limit(limit)
     *
     * @param limit the maximum number of orders to return
     * @return list of most recent orders
     */
    @Query(value = "{}", sort = "{ 'createdAt': -1 }")
    List<Order> findRecentOrders(int limit);
}
