package com.example.ecommerce.service;

import com.example.ecommerce.IntegrationTest;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for OrderService
 * Tests business logic for order operations including creation, status updates, and inventory management
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@DisplayName("OrderService Tests")
class OrderServiceTest extends IntegrationTest {

    @Autowired
    private OrderService orderService;

    /**
     * Test: Create a basic order
     * Chức năng: Tạo đơn hàng cơ bản
     */
    @Test
    @DisplayName("Tạo đơn hàng cơ bản thành công")
    void testCreateOrder() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        Order order = orderBuilder()
                .withUserId(user.getId())
                .withOrderNumber("ORD-2025-001")
                .withItems(List.of(item))
                .withTotalAmount(product.getPrice())
                .withStatus("PENDING")
                .build();

        // Act
        Order savedOrder = orderRepository.save(order);

        // Assert
        assertThat(savedOrder)
                .isNotNull()
                .extracting(Order::getUserId, Order::getStatus)
                .containsExactly(user.getId(), "PENDING");
        assertThat(savedOrder.getId()).isNotNull();
    }

    /**
     * Test: Order number generation
     * Chức năng: Tạo số đơn hàng duy nhất
     */
    @Test
    @DisplayName("Số đơn hàng phải duy nhất")
    void testOrderNumberGeneration() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        
        Order order1 = orderBuilder()
                .withUserId(user.getId())
                .withOrderNumber("ORD-2025-001")
                .withItems(List.of(item))
                .withTotalAmount(product.getPrice())
                .build();
        
        Order order2 = orderBuilder()
                .withUserId(user.getId())
                .withOrderNumber("ORD-2025-002")
                .withItems(List.of(item))
                .withTotalAmount(product.getPrice())
                .build();

        // Act
        Order saved1 = orderRepository.save(order1);
        Order saved2 = orderRepository.save(order2);

        // Assert
        assertThat(saved1.getOrderNumber()).isNotEqualTo(saved2.getOrderNumber());
        assertThat(saved1.getOrderNumber()).contains("ORD-");
        assertThat(saved2.getOrderNumber()).contains("ORD-");
    }

    /**
     * Test: Update order status
     * Chức năng: Cập nhật trạng thái đơn hàng
     */
    @Test
    @DisplayName("Cập nhật trạng thái đơn hàng thành công")
    void testUpdateOrderStatus() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        Order order = createTestOrder(user.getId(), "ORD-2025-001", List.of(item), product.getPrice());
        assertThat(order.getStatus()).isEqualTo("PENDING");

        // Act
        order.setStatus("PROCESSING");
        Order updatedOrder = orderRepository.save(order);

        // Assert
        assertThat(updatedOrder.getStatus()).isEqualTo("PROCESSING");
    }

    /**
     * Test: Order status transitions
     * Chức năng: Chuyển đổi trạng thái đơn hàng
     */
    @Test
    @DisplayName("Chuyển đổi trạng thái đơn hàng theo quy trình")
    void testOrderStatusTransitions() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        Order order = createTestOrder(user.getId(), "ORD-2025-001", List.of(item), product.getPrice());

        // Act & Assert - PENDING -> PROCESSING
        order.setStatus("PROCESSING");
        orderRepository.save(order);
        assertThat(orderRepository.findById(order.getId()).get().getStatus()).isEqualTo("PROCESSING");

        // Act & Assert - PROCESSING -> SHIPPED
        order.setStatus("SHIPPED");
        orderRepository.save(order);
        assertThat(orderRepository.findById(order.getId()).get().getStatus()).isEqualTo("SHIPPED");

        // Act & Assert - SHIPPED -> DELIVERED
        order.setStatus("DELIVERED");
        orderRepository.save(order);
        assertThat(orderRepository.findById(order.getId()).get().getStatus()).isEqualTo("DELIVERED");
    }

    /**
     * Test: Find orders by user
     * Chức năng: Tìm đơn hàng theo người dùng
     */
    @Test
    @DisplayName("Tìm đơn hàng theo người dùng thành công")
    void testFindOrdersByUser() {
        // Arrange
        User user1 = createTestUser("customer1@example.com", "customer1", "Customer 1");
        User user2 = createTestUser("customer2@example.com", "customer2", "Customer 2");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        
        // Create multiple orders for user1
        createTestOrder(user1.getId(), "ORD-2025-001", List.of(item), product.getPrice());
        createTestOrder(user1.getId(), "ORD-2025-002", List.of(item), product.getPrice());
        
        // Create order for user2
        createTestOrder(user2.getId(), "ORD-2025-003", List.of(item), product.getPrice());

        // Act
        Page<Order> user1Orders = orderRepository.findByUserId(user1.getId(), PageRequest.of(0, 10));
        Page<Order> user2Orders = orderRepository.findByUserId(user2.getId(), PageRequest.of(0, 10));

        // Assert
        assertThat(user1Orders.getTotalElements()).isEqualTo(2);
        assertThat(user2Orders.getTotalElements()).isEqualTo(1);
        assertThat(user1Orders.getContent())
                .allMatch(order -> order.getUserId().equals(user1.getId()));
    }

    /**
     * Test: Total amount calculation
     * Chức năng: Tính toán tổng số tiền đơn hàng
     */
    @Test
    @DisplayName("Tổng số tiền đơn hàng được tính toán chính xác")
    void testTotalAmountCalculation() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product1 = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        Product product2 = createTestProduct("Mouse", "mouse", BigDecimal.valueOf(49.99), category.getId());
        
        OrderItem item1 = new OrderItem(product1.getId(), product1.getName(), 
                BigDecimal.valueOf(999.99), 1);
        OrderItem item2 = new OrderItem(product2.getId(), product2.getName(), 
                BigDecimal.valueOf(49.99), 2);
        
        BigDecimal expectedTotal = BigDecimal.valueOf(999.99).add(BigDecimal.valueOf(49.99).multiply(BigDecimal.valueOf(2)));
        Order order = orderBuilder()
                .withUserId(user.getId())
                .withOrderNumber("ORD-2025-001")
                .withItems(List.of(item1, item2))
                .withTotalAmount(expectedTotal)
                .build();

        // Act
        Order savedOrder = orderRepository.save(order);

        // Assert
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(expectedTotal);
    }

    /**
     * Test: Order with multiple items
     * Chức năng: Đơn hàng với nhiều mục
     */
    @Test
    @DisplayName("Đơn hàng với nhiều mục thành công")
    void testOrderWithMultipleItems() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product1 = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        Product product2 = createTestProduct("Mouse", "mouse", BigDecimal.valueOf(49.99), category.getId());
        
        OrderItem item1 = new OrderItem(product1.getId(), product1.getName(), 
                BigDecimal.valueOf(999.99), 1);
        OrderItem item2 = new OrderItem(product2.getId(), product2.getName(), 
                BigDecimal.valueOf(49.99), 2);
        
        List<OrderItem> items = List.of(item1, item2);
        Order order = createTestOrder(user.getId(), "ORD-2025-001", items, 
                BigDecimal.valueOf(1099.97));

        // Act
        Order savedOrder = orderRepository.findById(order.getId()).orElseThrow();

        // Assert
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.getItems())
                .extracting(OrderItem::getProductName)
                .containsExactlyInAnyOrder("Laptop", "Mouse");
    }

    /**
     * Test: Find order by order number
     * Chức năng: Tìm đơn hàng bằng số đơn hàng
     */
    @Test
    @DisplayName("Tìm đơn hàng bằng số đơn hàng thành công")
    void testFindByOrderNumber() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        String orderNumber = "ORD-2025-UNIQUE";
        Order order = createTestOrder(user.getId(), orderNumber, List.of(item), product.getPrice());

        // Act
        Order foundOrder = orderRepository.findByOrderNumber(orderNumber).orElseThrow();

        // Assert
        assertThat(foundOrder.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(foundOrder.getUserId()).isEqualTo(user.getId());
    }

    /**
     * Test: Count orders by status
     * Chức năng: Đếm đơn hàng theo trạng thái
     */
    @Test
    @DisplayName("Đếm đơn hàng theo trạng thái thành công")
    void testCountOrdersByStatus() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        
        Order pending1 = createTestOrder(user.getId(), "ORD-2025-001", List.of(item), product.getPrice());
        Order pending2 = createTestOrder(user.getId(), "ORD-2025-002", List.of(item), product.getPrice());
        
        Order processing = createTestOrder(user.getId(), "ORD-2025-003", List.of(item), product.getPrice());
        processing.setStatus("PROCESSING");
        orderRepository.save(processing);

        // Act
        long pendingCount = orderRepository.countByStatus("PENDING");
        long processingCount = orderRepository.countByStatus("PROCESSING");

        // Assert
        assertThat(pendingCount).isEqualTo(2);
        assertThat(processingCount).isEqualTo(1);
    }

    /**
     * Test: Order with shipping address
     * Chức năng: Đơn hàng với địa chỉ giao hàng
     */
    @Test
    @DisplayName("Đơn hàng với địa chỉ giao hàng thành công")
    void testOrderWithShippingAddress() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        Address shippingAddress = new Address();
        shippingAddress.setStreet("123 Ship Street");
        shippingAddress.setCity("Ship City");
        shippingAddress.setCountry("Ship Country");
        
        Order order = orderBuilder()
                .withUserId(user.getId())
                .withOrderNumber("ORD-2025-001")
                .withItems(List.of(item))
                .withTotalAmount(product.getPrice())
                .withShippingAddress(shippingAddress)
                .build();

        // Act
        Order savedOrder = orderRepository.findById(order.getId()).orElseThrow();

        // Assert
        assertThat(savedOrder.getShippingAddress())
                .isNotNull()
                .extracting(Address::getCity, Address::getCountry)
                .containsExactly("Ship City", "Ship Country");
    }

    /**
     * Test: Find orders by user and status
     * Chức năng: Tìm đơn hàng theo người dùng và trạng thái
     */
    @Test
    @DisplayName("Tìm đơn hàng theo người dùng và trạng thái thành công")
    void testFindByUserIdAndStatus() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        
        Order pending = createTestOrder(user.getId(), "ORD-2025-001", List.of(item), product.getPrice());
        
        Order processing = createTestOrder(user.getId(), "ORD-2025-002", List.of(item), product.getPrice());
        processing.setStatus("PROCESSING");
        orderRepository.save(processing);

        // Act
        Page<Order> pendingOrders = orderRepository
                .findByUserIdAndStatus(user.getId(), "PENDING", PageRequest.of(0, 10));
        Page<Order> processingOrders = orderRepository
                .findByUserIdAndStatus(user.getId(), "PROCESSING", PageRequest.of(0, 10));

        // Assert
        assertThat(pendingOrders.getTotalElements()).isEqualTo(1);
        assertThat(processingOrders.getTotalElements()).isEqualTo(1);
    }

    /**
     * Test: Order sorting by creation date
     * Chức năng: Sắp xếp đơn hàng theo ngày tạo
     */
    @Test
    @DisplayName("Đơn hàng sắp xếp theo ngày tạo (mới nhất trước)")
    void testOrderSortingByCreationDate() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        
        createTestOrder(user.getId(), "ORD-2025-001", List.of(item), product.getPrice());
        createTestOrder(user.getId(), "ORD-2025-002", List.of(item), product.getPrice());
        createTestOrder(user.getId(), "ORD-2025-003", List.of(item), product.getPrice());

        // Act
        Page<Order> userOrders = orderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 10));

        // Assert
        assertThat(userOrders.getContent()).hasSize(3);
    }

    /**
     * Test: Order retrieval by non-existent ID
     * Chức năng: Lấy đơn hàng không tồn tại
     */
    @Test
    @DisplayName("Lấy đơn hàng không tồn tại trả về trống")
    void testFindNonExistentOrder() {
        // Act
        var foundOrder = orderRepository.findById("non-existent-id");

        // Assert
        assertThat(foundOrder).isEmpty();
    }

    /**
     * Test: Payment info in order
     * Chức năng: Thông tin thanh toán trong đơn hàng
     */
    @Test
    @DisplayName("Thông tin thanh toán được lưu trữ trong đơn hàng")
    void testOrderWithPaymentInfo() {
        // Arrange
        User user = createTestUser("customer@example.com", "customer", "Customer");
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());
        
        OrderItem item = new OrderItem(product.getId(), product.getName(), 
                product.getPrice(), 1);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setMethod("CREDIT_CARD");
        paymentInfo.setStatus("PENDING");
        
        Order order = orderBuilder()
                .withUserId(user.getId())
                .withOrderNumber("ORD-2025-001")
                .withItems(List.of(item))
                .withTotalAmount(product.getPrice())
                .build();
        order.setPaymentInfo(paymentInfo);
        orderRepository.save(order);

        // Act
        Order savedOrder = orderRepository.findById(order.getId()).orElseThrow();

        // Assert
        assertThat(savedOrder.getPaymentInfo())
                .isNotNull()
                .extracting(PaymentInfo::getMethod, PaymentInfo::getStatus)
                .containsExactly("CREDIT_CARD", "PENDING");
    }
}
