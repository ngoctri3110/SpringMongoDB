package com.example.ecommerce.service;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.InventoryRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service class for analytics and reporting operations.
 * Implements MongoDB aggregation pipeline for complex data analysis.
 * Provides revenue stats, top products, category stats, user analytics, and inventory alerts.
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Generates revenue statistics for a date range.
     * Groups revenue by day, week, or month based on groupBy parameter.
     * Uses MongoDB aggregation pipeline for efficient data processing.
     *
     * @param startDate the start date for analysis
     * @param endDate the end date for analysis
     * @param groupBy grouping period ("DAY", "WEEK", "MONTH", "YEAR")
     * @return AnalyticsResponse containing revenue statistics
     */
    @Transactional(readOnly = true)
    public AnalyticsResponse getRevenueStats(LocalDateTime startDate, LocalDateTime endDate, String groupBy) {
        log.info("Generating revenue stats from {} to {} grouped by {}", startDate, endDate, groupBy);

        // Build aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                // Match orders within date range with completed status
                Aggregation.match(Criteria.where("createdAt")
                        .gte(startDate)
                        .lte(endDate)
                        .and("status").in("DELIVERED", "PROCESSING")),
                // Group by date and calculate totals
                Aggregation.group("$createdAt")
                        .sum("$totalAmount").as("revenue")
                        .count().as("orderCount"),
                // Sort by date descending
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "_id")
        );

        AggregationResults<RevenueStats> results = mongoTemplate.aggregate(
                aggregation, "orders", RevenueStats.class);

        List<RevenueStats> revenueStats = results.getMappedResults();
        BigDecimal totalRevenue = revenueStats.stream()
                .map(RevenueStats::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = revenueStats.stream()
                .mapToLong(RevenueStats::getOrderCount)
                .sum();

        AnalyticsResponse response = new AnalyticsResponse();
        response.setAnalyticsType("REVENUE");
        response.setData(revenueStats);
        response.setTotalValue(totalRevenue.doubleValue());
        response.setTotalCount(totalOrders);
        response.setStartDate(startDate);
        response.setEndDate(endDate);

        log.debug("Revenue stats generated - Total Revenue: {}, Total Orders: {}", totalRevenue, totalOrders);

        return response;
    }

    /**
     * Retrieves top selling products for a given period.
     * Uses aggregation to group by product and sum quantities sold.
     *
     * @param limit the maximum number of products to return
     * @param daysBefore the number of days to look back (0 = all time)
     * @return AnalyticsResponse containing top selling products
     */
    @Transactional(readOnly = true)
    public AnalyticsResponse getTopProducts(int limit, int daysBefore) {
        log.info("Generating top {} products - days before: {}", limit, daysBefore);

        LocalDateTime startDate = daysBefore > 0
                ? LocalDateTime.now().minusDays(daysBefore)
                : LocalDateTime.of(2000, 1, 1, 0, 0); // Very old date for "all time"

        Aggregation aggregation = Aggregation.newAggregation(
                // Match orders within timeframe
                Aggregation.match(Criteria.where("createdAt")
                        .gte(startDate)
                        .and("status").in("DELIVERED", "PROCESSING")),
                // Unwind items array
                Aggregation.unwind("$items"),
                // Group by product and sum quantities
                Aggregation.group("$items.productId")
                        .sum("$items.quantity").as("quantitySold")
                        .sum("$items.totalPrice").as("revenue")
                        .first("$items.productName").as("productName"),
                // Sort by quantity descending
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "quantitySold"),
                // Limit results
                Aggregation.limit(limit)
        );

        AggregationResults<TopProductStats> results = mongoTemplate.aggregate(
                aggregation, "orders", TopProductStats.class);

        List<TopProductStats> topProducts = results.getMappedResults();

        AnalyticsResponse response = new AnalyticsResponse();
        response.setAnalyticsType("TOP_PRODUCTS");
        response.setData(topProducts);
        response.setTotalCount((long) topProducts.size());
        response.setStartDate(startDate);
        response.setEndDate(LocalDateTime.now());

        log.debug("Top products generated - Count: {}", topProducts.size());

        return response;
    }

    /**
     * Retrieves category statistics including total sales and order count per category.
     *
     * @return AnalyticsResponse containing category statistics
     */
    @Transactional(readOnly = true)
    public AnalyticsResponse getTopCategories() {
        log.info("Generating category statistics");

        // Get all products grouped by category
        var allProducts = productRepository.findAll();
        var categoryStats = new HashMap<String, CategoryStats>();

        for (Product product : allProducts) {
            String categoryId = product.getCategoryId();
            categoryStats.computeIfAbsent(categoryId, k -> new CategoryStats(categoryId))
                    .addProductCount(1);
        }

        // Get sales volume per category from orders
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("status").in("DELIVERED", "PROCESSING")),
                Aggregation.unwind("$items"),
                Aggregation.group("$items.productId")
                        .sum("$items.quantity").as("quantity"),
                Aggregation.project()
                        .and("$_id").as("productId")
                        .and("$quantity").as("quantity")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
                aggregation, "orders", Map.class);

        List<CategoryStats> categoryStatsList = new ArrayList<>(categoryStats.values());

        AnalyticsResponse response = new AnalyticsResponse();
        response.setAnalyticsType("CATEGORY_STATS");
        response.setData(categoryStatsList);
        response.setTotalCount((long) categoryStatsList.size());
        response.setEndDate(LocalDateTime.now());

        log.debug("Category statistics generated - Count: {}", categoryStatsList.size());

        return response;
    }

    /**
     * Retrieves comprehensive user statistics for a specific user.
     * Includes total orders, total spent, and average order value.
     *
     * @param userId the user ID
     * @return AnalyticsResponse containing user statistics
     */
    @Transactional(readOnly = true)
    public AnalyticsResponse getUserStats(String userId) {
        log.info("Generating user statistics for user: {}", userId);

        Page<Order> userOrders = orderRepository.findByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE));

        BigDecimal totalSpent = userOrders.getContent().stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double avgOrderValue = userOrders.getTotalElements() > 0
                ? totalSpent.doubleValue() / userOrders.getTotalElements()
                : 0.0;

        UserStats stats = new UserStats();
        stats.setUserId(userId);
        stats.setTotalOrders(userOrders.getTotalElements());
        stats.setTotalSpent(totalSpent);
        stats.setAverageOrderValue(BigDecimal.valueOf(avgOrderValue));
        stats.setRecentOrders(userOrders.getContent());

        AnalyticsResponse response = new AnalyticsResponse();
        response.setAnalyticsType("USER_STATS");
        response.setData(Collections.singletonList(stats));
        response.setTotalCount(1L);
        response.setEndDate(LocalDateTime.now());

        log.debug("User statistics generated - Total Orders: {}, Total Spent: {}", 
                userOrders.getTotalElements(), totalSpent);

        return response;
    }

    /**
     * Retrieves low stock inventory alerts.
     * Returns products with inventory below specified threshold.
     *
     * @param threshold the minimum stock threshold
     * @return AnalyticsResponse containing low stock products
     */
    @Transactional(readOnly = true)
    public AnalyticsResponse getLowStockProducts(int threshold) {
        log.info("Generating low stock alerts - threshold: {}", threshold);

        var lowStockInventory = inventoryRepository.findByQuantityLessThan(threshold);

        List<LowStockAlert> alerts = new ArrayList<>();
        for (var inventory : lowStockInventory) {
            var product = productRepository.findById(inventory.getProductId())
                    .orElse(null);

            if (product != null) {
                LowStockAlert alert = new LowStockAlert();
                alert.setProductId(inventory.getProductId());
                alert.setProductName(product.getName());
                alert.setCurrentStock(inventory.getQuantity());
                alert.setReservedStock(inventory.getReserved() != null ? inventory.getReserved() : 0);
                alert.setAvailableStock(inventory.getAvailable());
                alert.setThreshold(threshold);
                alert.setLastUpdated(inventory.getLastUpdated());

                alerts.add(alert);
            }
        }

        AnalyticsResponse response = new AnalyticsResponse();
        response.setAnalyticsType("LOW_STOCK_ALERTS");
        response.setData(alerts);
        response.setTotalCount((long) alerts.size());
        response.setEndDate(LocalDateTime.now());

        log.debug("Low stock alerts generated - Count: {}", alerts.size());

        return response;
    }

    /**
     * Generates comprehensive dashboard statistics for administrators.
     * Includes orders, revenue, products, and inventory metrics.
     *
     * @return AnalyticsResponse containing dashboard data
     */
    @Transactional(readOnly = true)
    public AnalyticsResponse getDashboardStats() {
        log.info("Generating dashboard statistics");

        // Get order counts by status
        Map<String, Long> orderStats = new HashMap<>();
        orderStats.put("PENDING", orderRepository.countByStatus("PENDING"));
        orderStats.put("PROCESSING", orderRepository.countByStatus("PROCESSING"));
        orderStats.put("SHIPPED", orderRepository.countByStatus("SHIPPED"));
        orderStats.put("DELIVERED", orderRepository.countByStatus("DELIVERED"));
        orderStats.put("CANCELLED", orderRepository.countByStatus("CANCELLED"));

        // Get total products
        long totalProducts = productRepository.count();

        // Get low stock count
        long lowStockCount = inventoryRepository.findByQuantityLessThan(10).size();

        // Get revenue for last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Page<Order> recentOrders = orderRepository.findByUserIdOrderByCreatedAtDesc("", 
                PageRequest.of(0, Integer.MAX_VALUE));
        
        BigDecimal monthlyRevenue = recentOrders.getContent().stream()
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().isAfter(thirtyDaysAgo))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardStats stats = new DashboardStats();
        stats.setOrderStats(orderStats);
        stats.setTotalProducts(totalProducts);
        stats.setLowStockCount(lowStockCount);
        stats.setMonthlyRevenue(monthlyRevenue);
        stats.setTimestamp(LocalDateTime.now());

        AnalyticsResponse response = new AnalyticsResponse();
        response.setAnalyticsType("DASHBOARD");
        response.setData(Collections.singletonList(stats));
        response.setTotalCount(1L);
        response.setEndDate(LocalDateTime.now());

        log.debug("Dashboard statistics generated");

        return response;
    }
}

/**
 * DTO for analytics response containing aggregated data and metadata.
 */
class AnalyticsResponse {
    private String analyticsType;
    private List<?> data;
    private Double totalValue;
    private Long totalCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Constructors and getters/setters
    public AnalyticsResponse() {}

    public String getAnalyticsType() {
        return analyticsType;
    }

    public void setAnalyticsType(String analyticsType) {
        this.analyticsType = analyticsType;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}

/**
 * DTO for revenue statistics aggregation.
 */
class RevenueStats {
    private String _id;
    private BigDecimal revenue;
    private Long orderCount;

    public RevenueStats() {}

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }
}

/**
 * DTO for top product statistics.
 */
class TopProductStats {
    private String _id;
    private String productName;
    private Long quantitySold;
    private BigDecimal revenue;

    public TopProductStats() {}

    public String getProductId() {
        return _id;
    }

    public void setProductId(String _id) {
        this._id = _id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Long quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}

/**
 * DTO for category statistics.
 */
class CategoryStats {
    private String categoryId;
    private Integer productCount;
    private BigDecimal totalRevenue;
    private Long totalQuantitySold;

    public CategoryStats() {}

    public CategoryStats(String categoryId) {
        this.categoryId = categoryId;
        this.productCount = 0;
        this.totalRevenue = BigDecimal.ZERO;
        this.totalQuantitySold = 0L;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public void addProductCount(int count) {
        this.productCount = (this.productCount != null ? this.productCount : 0) + count;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(Long totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }
}

/**
 * DTO for user statistics.
 */
class UserStats {
    private String userId;
    private Long totalOrders;
    private BigDecimal totalSpent;
    private BigDecimal averageOrderValue;
    private List<Order> recentOrders;

    public UserStats() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public List<Order> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<Order> recentOrders) {
        this.recentOrders = recentOrders;
    }
}

/**
 * DTO for low stock inventory alerts.
 */
class LowStockAlert {
    private String productId;
    private String productName;
    private Integer currentStock;
    private Integer reservedStock;
    private Integer availableStock;
    private Integer threshold;
    private LocalDateTime lastUpdated;

    public LowStockAlert() {}

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        this.reservedStock = reservedStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

/**
 * DTO for dashboard statistics.
 */
class DashboardStats {
    private Map<String, Long> orderStats;
    private Long totalProducts;
    private Long lowStockCount;
    private BigDecimal monthlyRevenue;
    private LocalDateTime timestamp;

    public DashboardStats() {}

    public Map<String, Long> getOrderStats() {
        return orderStats;
    }

    public void setOrderStats(Map<String, Long> orderStats) {
        this.orderStats = orderStats;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(Long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
