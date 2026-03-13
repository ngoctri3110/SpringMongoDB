package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.AdvancedSearchRequest;
import com.example.ecommerce.dto.response.PagedResponse;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.service.SearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Search Controller - REST endpoints cho tìm kiếm sản phẩm
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SearchController {

    private final SearchService searchService;

    /**
     * GET /api/v1/search/products - Tìm kiếm sản phẩm với filters
     * 
     * @param name Tên sản phẩm (optional)
     * @param category Danh mục sản phẩm (optional)
     * @param priceMin Giá tối thiểu (optional)
     * @param priceMax Giá tối đa (optional)
     * @param tags Tags (optional, có thể filter theo tag)
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<ProductResponse>> với status 200 OK
     */
    @GetMapping("/products")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/search/products - Searching products with filters: name={}, category={}, priceMin={}, priceMax={}, tags={}", 
                name, category, priceMin, priceMax, tags);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> pageResponse = searchService.searchProducts(name, category, priceMin, priceMax, tags, pageable);
        
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
     * GET /api/v1/search/products/advanced - Tìm kiếm sản phẩm nâng cao
     * 
     * @param request AdvancedSearchRequest
     * @return ResponseEntity<PagedResponse<ProductResponse>> với status 200 OK
     */
    @GetMapping("/products/advanced")
    public ResponseEntity<PagedResponse<ProductResponse>> advancedSearch(
            @Valid @RequestBody AdvancedSearchRequest request) {
        log.info("GET /api/v1/search/products/advanced - Advanced search with request: {}", request);
        
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 10
        );
        Page<ProductResponse> pageResponse = searchService.advancedSearch(request, pageable);
        
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
