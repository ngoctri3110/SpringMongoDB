package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateReviewRequest;
import com.example.ecommerce.dto.response.PagedResponse;
import com.example.ecommerce.dto.response.ReviewResponse;
import com.example.ecommerce.service.ReviewService;
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
 * Review Controller - REST endpoints cho Review
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /api/v1/reviews - Tạo review mới
     * 
     * @param request CreateReviewRequest
     * @return ResponseEntity<ReviewResponse> với status 201 Created
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        log.info("POST /api/v1/reviews - Creating review");
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/reviews/{id} - Lấy review theo ID
     * 
     * @param id Review ID
     * @return ResponseEntity<ReviewResponse> với status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable String id) {
        log.info("GET /api/v1/reviews/{} - Fetching review", id);
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/reviews/product/{productId} - Lấy reviews của product (phân trang)
     * 
     * @param productId Product ID
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<ReviewResponse>> với status 200 OK
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<PagedResponse<ReviewResponse>> getReviewsByProduct(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/reviews/product/{} - Fetching product reviews with page: {}, size: {}", 
                productId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> pageResponse = reviewService.getReviewsByProduct(productId, pageable);
        
        PagedResponse<ReviewResponse> response = PagedResponse.<ReviewResponse>builder()
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
     * GET /api/v1/reviews/user/{userId} - Lấy reviews của user (phân trang)
     * 
     * @param userId User ID
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<ReviewResponse>> với status 200 OK
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<ReviewResponse>> getReviewsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/reviews/user/{} - Fetching user reviews with page: {}, size: {}", 
                userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> pageResponse = reviewService.getReviewsByUser(userId, pageable);
        
        PagedResponse<ReviewResponse> response = PagedResponse.<ReviewResponse>builder()
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
     * PUT /api/v1/reviews/{id} - Cập nhật review
     * 
     * @param id Review ID
     * @param request CreateReviewRequest
     * @return ResponseEntity<ReviewResponse> với status 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable String id,
            @Valid @RequestBody CreateReviewRequest request) {
        log.info("PUT /api/v1/reviews/{} - Updating review", id);
        ReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/reviews/{id} - Xóa review
     * 
     * @param id Review ID
     * @return ResponseEntity với status 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        log.info("DELETE /api/v1/reviews/{} - Deleting review", id);
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
