package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateInventoryRequest;
import com.example.ecommerce.dto.request.ReserveStockRequest;
import com.example.ecommerce.dto.response.InventoryResponse;
import com.example.ecommerce.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Inventory Controller - REST endpoints cho Inventory
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * GET /api/v1/inventory/{id} - Lấy inventory theo ID
     * 
     * @param id Inventory ID
     * @return ResponseEntity<InventoryResponse> với status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable String id) {
        log.info("GET /api/v1/inventory/{} - Fetching inventory", id);
        InventoryResponse response = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/inventory/product/{productId} - Lấy inventory theo Product ID
     * 
     * @param productId Product ID
     * @return ResponseEntity<InventoryResponse> với status 200 OK
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable String productId) {
        log.info("GET /api/v1/inventory/product/{} - Fetching inventory by product", productId);
        InventoryResponse response = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/inventory - Tạo inventory mới
     * 
     * @param request CreateInventoryRequest
     * @return ResponseEntity<InventoryResponse> với status 201 Created
     */
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        log.info("POST /api/v1/inventory - Creating inventory");
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/v1/inventory/{id} - Cập nhật inventory
     * 
     * @param id Inventory ID
     * @param request CreateInventoryRequest
     * @return ResponseEntity<InventoryResponse> với status 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable String id,
            @Valid @RequestBody CreateInventoryRequest request) {
        log.info("PUT /api/v1/inventory/{} - Updating inventory", id);
        InventoryResponse response = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/inventory/{id}/reserve - Đặt giữ kho (reserve stock)
     * 
     * @param id Inventory ID
     * @param request ReserveStockRequest
     * @return ResponseEntity<InventoryResponse> với status 200 OK
     */
    @PostMapping("/{id}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable String id,
            @Valid @RequestBody ReserveStockRequest request) {
        log.info("POST /api/v1/inventory/{}/reserve - Reserving stock", id);
        InventoryResponse response = inventoryService.reserveStock(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/inventory/{id}/release - Giải phóng kho (release stock)
     * 
     * @param id Inventory ID
     * @param request ReserveStockRequest (chứa quantity)
     * @return ResponseEntity<InventoryResponse> với status 200 OK
     */
    @PostMapping("/{id}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable String id,
            @Valid @RequestBody ReserveStockRequest request) {
        log.info("POST /api/v1/inventory/{}/release - Releasing stock", id);
        InventoryResponse response = inventoryService.releaseStock(id, request);
        return ResponseEntity.ok(response);
    }
}
