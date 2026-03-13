package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateOrderRequestDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service class for managing order operations.
 * Handles order creation, retrieval, status updates, and cancellations.
 * Manages inventory decrement, cart clearing, and order validation.
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderRepositoryCustom orderRepositoryCustom;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;

    /**
     * Creates a new order from a user's cart items.
     * Validates that all products exist, have sufficient stock, and decrements inventory.
     * Automatically clears the user's cart after order creation.
     *
     * @param userId the user ID placing the order
     * @param request the order creation request containing items, shipping address, and payment info
     * @return the created Order object
     * @throws ResourceNotFoundException if a product doesn't exist
     * @throws InsufficientStockException if inventory is insufficient for any item
     */
    @Transactional
    public Order createOrder(String userId, CreateOrderRequestDTO request) {
        log.info("Creating order for user: {}", userId);

        // Validate and collect order items with product details
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : request.getItems()) {
            // Verify product exists
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product", "id", cartItem.getProductId()));

            // Check inventory availability
            Inventory inventory = inventoryRepository.findByProductId(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory", "productId", cartItem.getProductId()));

            if (!inventory.canReserve(cartItem.getQuantity())) {
                log.warn("Insufficient stock for product: {} - requested: {}, available: {}",
                        cartItem.getProductId(), cartItem.getQuantity(), inventory.getAvailable());
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName());
            }

            // Create order item from cart item
            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .productSlug(product.getSlug())
                    .price(cartItem.getPrice())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(cartItem.getTotalPrice());

            // Reserve inventory for the order
            inventory.reserve(cartItem.getQuantity());
            inventoryRepository.save(inventory);
            log.debug("Reserved {} units of product: {}", cartItem.getQuantity(), cartItem.getProductId());
        }

        // Generate order number
        String orderNumber = generateOrderNumber();

        // Create and save order
        Order order = Order.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .items(orderItems)
                .totalAmount(totalAmount)
                .status("PENDING")
                .shippingAddress(request.getShippingAddress())
                .paymentInfo(request.getPaymentInfo())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with order number: {}", orderNumber);

        // Clear user's cart after successful order creation
        cartRepository.deleteByUserId(userId);
        log.debug("User cart cleared for user: {}", userId);

        return savedOrder;
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the order ID
     * @return the Order object
     * @throws ResourceNotFoundException if order doesn't exist
     */
    @Transactional(readOnly = true)
    public Order getOrderById(String orderId) {
        log.debug("Fetching order: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order", "id", orderId));
    }

    /**
     * Retrieves all orders for a user with pagination.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of Order objects for the user
     */
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByUserId(String userId, Pageable pageable) {
        log.debug("Fetching orders for user: {} with pageable: {}", userId, pageable);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId the order ID
     * @param newStatus the new status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
     * @return the updated Order object
     * @throws ResourceNotFoundException if order doesn't exist
     */
    @Transactional
    public Order updateOrderStatus(String orderId, String newStatus) {
        log.info("Updating order: {} status to: {}", orderId, newStatus);

        Order order = getOrderById(orderId);
        String oldStatus = order.getStatus();
        order.setStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated from {} to {}", oldStatus, newStatus);

        return updatedOrder;
    }

    /**
     * Cancels an order and releases reserved inventory.
     * Only works for orders in PENDING or PROCESSING status.
     *
     * @param orderId the order ID to cancel
     * @return the cancelled Order object
     * @throws ResourceNotFoundException if order doesn't exist
     */
    @Transactional
    public Order cancelOrder(String orderId) {
        log.info("Cancelling order: {}", orderId);

        Order order = getOrderById(orderId);

        // Validate order can be cancelled
        if (!order.getStatus().equals("PENDING") && !order.getStatus().equals("PROCESSING")) {
            log.warn("Cannot cancel order {} with status: {}", orderId, order.getStatus());
            throw new IllegalStateException(
                    "Order cannot be cancelled with status: " + order.getStatus());
        }

        // Release reserved inventory for all items
        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory", "productId", item.getProductId()));

            inventory.release(item.getQuantity());
            inventoryRepository.save(inventory);
            log.debug("Released {} units of product: {}", item.getQuantity(), item.getProductId());
        }

        // Update order status
        order.setStatus("CANCELLED");
        Order cancelledOrder = orderRepository.save(order);
        log.info("Order cancelled successfully: {}", orderId);

        return cancelledOrder;
    }

    /**
     * Generates a unique order number in the format ORD-YYYY-XXXXXX.
     * Uses a simple sequential counter approach.
     *
     * @return a unique order number
     */
    private String generateOrderNumber() {
        AtomicInteger counter = new AtomicInteger(1000);
        return String.format("ORD-%d-%06d", LocalDateTime.now().getYear(), counter.getAndIncrement());
    }

    /**
     * Retrieves orders with dynamic filtering criteria.
     *
     * @param userId filter by user ID (optional)
     * @param status filter by status (optional)
     * @param startDate filter by start date (optional)
     * @param endDate filter by end date (optional)
     * @param minAmount filter by minimum amount (optional)
     * @param maxAmount filter by maximum amount (optional)
     * @param pageable pagination information
     * @return Page of Order objects matching criteria
     */
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByDynamicFilters(String userId, String status,
                                                  LocalDateTime startDate, LocalDateTime endDate,
                                                  Double minAmount, Double maxAmount, Pageable pageable) {
        log.debug("Fetching orders with dynamic filters");
        return orderRepositoryCustom.findOrdersByDynamicFilters(
                userId, status, startDate, endDate, minAmount, maxAmount, pageable);
    }

    /**
     * Counts total orders with a specific status.
     *
     * @param status the order status to count
     * @return count of orders with the given status
     */
    @Transactional(readOnly = true)
    public long countOrdersByStatus(String status) {
        return orderRepository.countByStatus(status);
    }
    }
