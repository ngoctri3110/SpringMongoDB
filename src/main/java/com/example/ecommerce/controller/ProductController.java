package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.response.PagedResponse;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * Product Controller - REST endpoints cho Product
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * POST /api/v1/products - Tạo product mới
     * 
     * @param request CreateProductRequest
     * @return ResponseEntity<ProductResponse> với status 201 Created
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("POST /api/v1/products - Creating product");
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/products/{id} - Lấy product theo ID
     * 
     * @param id Product ID
     * @return ResponseEntity<ProductResponse> với status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        log.info("GET /api/v1/products/{} - Fetching product", id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/products/{id} - Cập nhật product
     * 
     * @param id Product ID
     * @param request CreateProductRequest
     * @return ResponseEntity<ProductResponse> với status 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id, 
            @Valid @RequestBody CreateProductRequest request) {
        log.info("PUT /api/v1/products/{} - Updating product", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/products/{id} - Xóa product
     * 
     * @param id Product ID
     * @return ResponseEntity với status 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.info("DELETE /api/v1/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/products - Lấy danh sách products (phân trang)
     * 
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<ProductResponse>> với status 200 OK
     */
    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/products - Fetching products with page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> pageResponse = productService.getProducts(pageable);
        
        PagedResponse<ProductResponse> response = PagedResponse.<ProductResponse>builder()
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
     * GET /api/v1/products/category/{categoryId} - Lấy products theo category (phân trang)
     * 
     * @param categoryId Category ID
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<ProductResponse>> với status 200 OK
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/products/category/{} - Fetching products with page: {}, size: {}", 
                categoryId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> pageResponse = productService.getProductsByCategory(categoryId, pageable);
        
        PagedResponse<ProductResponse> response = PagedResponse.<ProductResponse>builder()
                .content(pageResponse.getContent())
                .page(pageResponse.getNumber())
                .size(pageResponse.getSize())
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages())
                .last(pageResponse.isLast())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
