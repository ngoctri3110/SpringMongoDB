package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.AnalyticsResponse;
import com.example.ecommerce.dto.response.RevenueAnalyticsResponse;
import com.example.ecommerce.dto.response.TopProductAnalyticsResponse;
import com.example.ecommerce.dto.response.UserStatsAnalyticsResponse;
import com.example.ecommerce.service.AnalyticsService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Analytics Controller - REST endpoints cho thống kê và phân tích
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * GET /api/v1/analytics/revenue - Lấy thống kê doanh thu theo khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd)
     * @return ResponseEntity<RevenueAnalyticsResponse> với status 200 OK
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueAnalyticsResponse> getRevenueAnalytics(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        log.info("GET /api/v1/analytics/revenue - Fetching revenue analytics from {} to {}", startDate, endDate);
        RevenueAnalyticsResponse response = analyticsService.getRevenueAnalytics(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/analytics/top-products - Lấy danh sách sản phẩm bán chạy nhất
     * 
     * @param limit Số lượng sản phẩm cần lấy (mặc định: 10)
     * @return ResponseEntity<List<TopProductAnalyticsResponse>> với status 200 OK
     */
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductAnalyticsResponse>> getTopProducts(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        log.info("GET /api/v1/analytics/top-products - Fetching top {} products", limit);
        List<TopProductAnalyticsResponse> response = analyticsService.getTopProducts(limit);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/analytics/top-categories - Lấy danh sách danh mục bán chạy nhất
     * 
     * @param limit Số lượng danh mục cần lấy (mặc định: 10)
     * @return ResponseEntity<List<AnalyticsResponse>> với status 200 OK
     */
    @GetMapping("/top-categories")
    public ResponseEntity<List<AnalyticsResponse>> getTopCategories(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        log.info("GET /api/v1/analytics/top-categories - Fetching top {} categories", limit);
        List<AnalyticsResponse> response = analyticsService.getTopCategories(limit);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/analytics/user-stats - Lấy thống kê người dùng
     * 
     * @return ResponseEntity<UserStatsAnalyticsResponse> với status 200 OK
     */
    @GetMapping("/user-stats")
    public ResponseEntity<UserStatsAnalyticsResponse> getUserStats() {
        log.info("GET /api/v1/analytics/user-stats - Fetching user statistics");
        UserStatsAnalyticsResponse response = analyticsService.getUserStats();
        return ResponseEntity.ok(response);
    }
}
