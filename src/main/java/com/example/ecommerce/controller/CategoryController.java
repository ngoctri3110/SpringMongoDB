package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateCategoryRequest;
import com.example.ecommerce.dto.response.CategoryResponse;
import com.example.ecommerce.dto.response.PagedResponse;
import com.example.ecommerce.service.CategoryService;
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
import java.util.List;

/**
 * Category Controller - REST endpoints cho Category
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * POST /api/v1/categories - Tạo category mới
     * 
     * @param request CreateCategoryRequest
     * @return ResponseEntity<CategoryResponse> với status 201 Created
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("POST /api/v1/categories - Creating category");
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/categories/{id} - Lấy category theo ID
     * 
     * @param id Category ID
     * @return ResponseEntity<CategoryResponse> với status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
        log.info("GET /api/v1/categories/{} - Fetching category", id);
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/categories/{id} - Cập nhật category
     * 
     * @param id Category ID
     * @param request CreateCategoryRequest
     * @return ResponseEntity<CategoryResponse> với status 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable String id, 
            @Valid @RequestBody CreateCategoryRequest request) {
        log.info("PUT /api/v1/categories/{} - Updating category", id);
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/categories/{id} - Xóa category
     * 
     * @param id Category ID
     * @return ResponseEntity với status 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        log.info("DELETE /api/v1/categories/{} - Deleting category", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/categories - Lấy danh sách categories (phân trang)
     * 
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<CategoryResponse>> với status 200 OK
     */
    @GetMapping
    public ResponseEntity<PagedResponse<CategoryResponse>> getCategories(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/categories - Fetching categories with page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponse> pageResponse = categoryService.getCategories(pageable);
        
        PagedResponse<CategoryResponse> response = PagedResponse.<CategoryResponse>builder()
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
     * GET /api/v1/categories/{id}/children - Lấy child categories
     * 
     * @param id Parent Category ID
     * @return ResponseEntity<List<CategoryResponse>> với status 200 OK
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<List<CategoryResponse>> getChildCategories(@PathVariable String id) {
        log.info("GET /api/v1/categories/{}/children - Fetching child categories", id);
        List<CategoryResponse> responses = categoryService.getChildCategories(id);
        return ResponseEntity.ok(responses);
    }
}
