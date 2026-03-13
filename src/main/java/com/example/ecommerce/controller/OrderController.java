package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.request.UpdateOrderStatusRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.dto.response.PagedResponse;
import com.example.ecommerce.service.OrderService;
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
 * Order Controller - REST endpoints cho Order
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/v1/orders - Tạo order mới
     * 
     * @param request CreateOrderRequest
     * @return ResponseEntity<OrderResponse> với status 201 Created
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/v1/orders - Creating order");
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/orders/{id} - Lấy order theo ID
     * 
     * @param id Order ID
     * @return ResponseEntity<OrderResponse> với status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        log.info("GET /api/v1/orders/{} - Fetching order", id);
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/orders - Lấy danh sách orders (phân trang)
     * 
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<OrderResponse>> với status 200 OK
     */
    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponse>> listOrders(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/orders - Fetching orders with page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> pageResponse = orderService.listOrders(pageable);
        
        PagedResponse<OrderResponse> response = PagedResponse.<OrderResponse>builder()
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
     * GET /api/v1/orders/user/{userId} - Lấy orders của user (phân trang)
     * 
     * @param userId User ID
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<OrderResponse>> với status 200 OK
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<OrderResponse>> getOrdersByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/orders/user/{} - Fetching user orders with page: {}, size: {}", 
                userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> pageResponse = orderService.getOrdersByUser(userId, pageable);
        
        PagedResponse<OrderResponse> response = PagedResponse.<OrderResponse>builder()
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
     * PUT /api/v1/orders/{id}/status - Cập nhật trạng thái order
     * 
     * @param id Order ID
     * @param request UpdateOrderStatusRequest
     * @return ResponseEntity<OrderResponse> với status 200 OK
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("PUT /api/v1/orders/{}/status - Updating order status", id);
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/orders/{id}/cancel - Hủy order
     * 
     * @param id Order ID
     * @return ResponseEntity<OrderResponse> với status 200 OK
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String id) {
        log.info("PUT /api/v1/orders/{}/cancel - Cancelling order", id);
        OrderResponse response = orderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }
}
