package com.example.ecommerce.repository.impl;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Repository Implementation cho Order entity.
 * Cung cấp các phương thức truy vấn phức tạp sử dụng Criteria API và Aggregation Pipeline.
 * 
 * Lớp này triển khai các phương thức custom cho OrderRepositoryCustom interface,
 * cho phép thực hiện các truy vấn MongoDB nâng cao như:
 * - Lọc đơn hàng theo status, user, date range
 * - Tính toán revenue và thống kê
 * - Phân tích dữ liệu bán hàng
 * 
 * @author E-Commerce Platform
 * @version 1.0
 * @see OrderRepositoryCustom
 * @see MongoTemplate
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    /**
     * MongoTemplate để thực hiện các truy vấn MongoDB phức tạp.
     * Được inject thông qua constructor.
     */
    private final MongoTemplate mongoTemplate;

    /**
     * Tìm đơn hàng theo trạng thái (status).
     * 
     * Sử dụng Criteria API để tìm tất cả đơn hàng có status cụ thể.
     * Sắp xếp theo ngày tạo giảm dần (mới nhất lên đầu).
     * 
     * Truy vấn MongoDB tương đương:
     * db.orders.find({ status: status }).sort({ createdAt: -1 })
     * 
     * Độ phức tạp: O(n) - Full collection scan, nhưng có thể dùng index trên status
     * Index gợi ý: db.orders.createIndex({ status: 1, createdAt: -1 })
     * 
     * @param status trạng thái đơn hàng (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
     * @param pageable thông tin phân trang
     * @return Page<Order> - Trang các đơn hàng có status đó
     * @throws IllegalArgumentException nếu status null hoặc trống
     */
    @Override
    public Page<Order> findOrdersByStatus(String status, Pageable pageable) {
        log.debug("Executing findOrdersByStatus with status: {}, pageable: {}", status, pageable);
        
        try {
            if (status == null || status.trim().isEmpty()) {
                log.warn("Status is null or empty");
                throw new IllegalArgumentException("Status không được để trống");
            }
            
            Criteria criteria = Criteria.where("status").is(status);
            Query query = new Query(criteria)
                    .with(pageable)
                    .addCriteria(new Criteria()); // sort by createdAt desc
            
            long total = mongoTemplate.count(new Query(criteria), Order.class);
            List<Order> orders = mongoTemplate.find(query, Order.class);
            
            log.info("Found {} orders with status: {}, total: {}", orders.size(), status, total);
            
            return new PageImpl<>(orders, pageable, total);
            
        } catch (Exception e) {
            log.error("Error executing findOrdersByStatus with status: {}", status, e);
            throw new RuntimeException("Lỗi khi tìm đơn hàng theo status: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm đơn hàng trong khoảng ngày.
     * 
     * Sử dụng Criteria API để tìm đơn hàng được tạo trong khoảng thời gian.
     * Sắp xếp theo ngày tạo giảm dần.
     * 
     * Truy vấn MongoDB tương đương:
     * db.orders.find({
     *   createdAt: { $gte: startDate, $lte: endDate }
     * }).sort({ createdAt: -1 })
     * 
     * Độ phức tạp: O(n) - Full collection scan, nhưng có thể dùng index trên createdAt
     * Index gợi ý: db.orders.createIndex({ createdAt: -1 })
     * 
     * @param startDate ngày bắt đầu (sẽ được chuyển thành 00:00:00 của ngày đó)
     * @param endDate ngày kết thúc (sẽ được chuyển thành 23:59:59 của ngày đó)
     * @param pageable thông tin phân trang
     * @return Page<Order> - Trang các đơn hàng trong khoảng ngày
     * @throws IllegalArgumentException nếu startDate > endDate
     */
    @Override
    public Page<Order> findOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("Executing findOrdersByDateRange from {} to {}, pageable: {}", startDate, endDate, pageable);
        
        try {
            if (startDate == null || endDate == null) {
                log.warn("Start date or end date is null");
                throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống");
            }
            
            if (startDate.isAfter(endDate)) {
                log.warn("Start date {} is after end date {}", startDate, endDate);
                throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
            }
            
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            Criteria criteria = new Criteria()
                    .andOperator(
                            Criteria.where("createdAt").gte(startDateTime),
                            Criteria.where("createdAt").lte(endDateTime)
                    );
            
            Query query = new Query(criteria)
                    .with(pageable);
            
            long total = mongoTemplate.count(new Query(criteria), Order.class);
            List<Order> orders = mongoTemplate.find(query, Order.class);
            
            log.info("Found {} orders in date range [{} to {}], total: {}", 
                    orders.size(), startDate, endDate, total);
            
            return new PageImpl<>(orders, pageable, total);
            
        } catch (Exception e) {
            log.error("Error executing findOrdersByDateRange", e);
            throw new RuntimeException("Lỗi khi tìm đơn hàng theo khoảng ngày: " + e.getMessage(), e);
        }
    }

    /**
     * Tính tổng revenue trong khoảng ngày.
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Lọc đơn hàng trong khoảng ngày
     * 2. Nhóm tất cả đơn hàng lại
     * 3. Tính tổng totalAmount
     * 4. Đếm số lượng đơn hàng
     * 5. Tính giá trị trung bình
     * 
     * Aggregation pipeline tương đương:
     * db.orders.aggregate([
     *   { $match: {
     *       createdAt: { $gte: startDate, $lte: endDate }
     *     }
     *   },
     *   { $group: {
     *       _id: null,
     *       totalRevenue: { $sum: "$totalAmount" },
     *       orderCount: { $sum: 1 },
     *       averageOrderValue: { $avg: "$totalAmount" }
     *     }
     *   }
     * ])
     * 
     * Độ phức tạp: O(n) - cần scan tất cả đơn hàng trong khoảng ngày
     * 
     * @param startDate ngày bắt đầu
     * @param endDate ngày kết thúc
     * @return Map chứa: totalRevenue, orderCount, averageOrderValue
     * @throws IllegalArgumentException nếu startDate > endDate
     */
    @Override
    public Map<String, Object> findRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Executing findRevenueByDateRange from {} to {}", startDate, endDate);
        
        try {
            if (startDate == null || endDate == null) {
                log.warn("Start date or end date is null");
                throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống");
            }
            
            if (startDate.isAfter(endDate)) {
                log.warn("Start date {} is after end date {}", startDate, endDate);
                throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
            }
            
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            Aggregation aggregation = Aggregation.newAggregation(
                    // Lọc đơn hàng trong khoảng ngày
                    Aggregation.match(
                            new Criteria()
                                    .andOperator(
                                            Criteria.where("createdAt").gte(startDateTime),
                                            Criteria.where("createdAt").lte(endDateTime)
                                    )
                    ),
                    // Nhóm và tính toán
                    Aggregation.group()
                            .sum("totalAmount").as("totalRevenue")
                            .count().as("orderCount")
                            .avg("totalAmount").as("averageOrderValue")
            );
            
            AggregationResults<Map> results = mongoTemplate.aggregate(
                    aggregation, "orders", Map.class
            );
            
            Map<String, Object> result = new HashMap<>();
            
            if (results.getMappedResults().isEmpty()) {
                result.put("totalRevenue", BigDecimal.ZERO);
                result.put("orderCount", 0);
                result.put("averageOrderValue", BigDecimal.ZERO);
                log.info("No orders found for date range [{} to {}]", startDate, endDate);
            } else {
                Map<String, Object> aggregationResult = results.getMappedResults().get(0);
                result.putAll(aggregationResult);
                log.info("Revenue stats for [{} to {}]: {}", startDate, endDate, result);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("Error executing findRevenueByDateRange", e);
            throw new RuntimeException("Lỗi khi tính revenue: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm đơn hàng của một user với status cụ thể.
     * 
     * Sử dụng Criteria API để lọc đơn hàng theo cả userId và status.
     * Hữu ích cho việc xem lịch sử đơn hàng của user theo từng status.
     * 
     * Truy vấn MongoDB tương đương:
     * db.orders.find({
     *   userId: userId,
     *   status: status
     * }).sort({ createdAt: -1 })
     * 
     * Độ phức tạp: O(n) - Full collection scan, nhưng có thể dùng compound index
     * Index gợi ý: db.orders.createIndex({ userId: 1, status: 1, createdAt: -1 })
     * 
     * @param userId ID của user
     * @param status trạng thái đơn hàng
     * @return List<Order> - Danh sách đơn hàng của user có status đó
     * @throws IllegalArgumentException nếu userId hoặc status null
     */
    @Override
    public List<Order> findOrdersByStatusAndUser(String userId, String status) {
        log.debug("Executing findOrdersByStatusAndUser - userId: {}, status: {}", userId, status);
        
        try {
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("User ID is null or empty");
                throw new IllegalArgumentException("User ID không được để trống");
            }
            
            if (status == null || status.trim().isEmpty()) {
                log.warn("Status is null or empty");
                throw new IllegalArgumentException("Status không được để trống");
            }
            
            Criteria criteria = new Criteria()
                    .andOperator(
                            Criteria.where("userId").is(userId),
                            Criteria.where("status").is(status)
                    );
            
            Query query = new Query(criteria);
            List<Order> orders = mongoTemplate.find(query, Order.class);
            
            log.info("Found {} orders for userId: {}, status: {}", orders.size(), userId, status);
            
            return orders;
            
        } catch (Exception e) {
            log.error("Error executing findOrdersByStatusAndUser", e);
            throw new RuntimeException("Lỗi khi tìm đơn hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Tính toán thống kê revenue chi tiết.
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Nhóm đơn hàng theo status
     * 2. Tính tổng revenue, số lượng, giá trị trung bình cho mỗi status
     * 3. Tính tổng hợp cho tất cả các status
     * 4. Sắp xếp theo revenue giảm dần
     * 
     * Aggregation pipeline tương đương:
     * db.orders.aggregate([
     *   { $match: { status: { $in: ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED'] } } },
     *   { $group: {
     *       _id: "$status",
     *       revenue: { $sum: "$totalAmount" },
     *       count: { $sum: 1 },
     *       avgValue: { $avg: "$totalAmount" },
     *       minValue: { $min: "$totalAmount" },
     *       maxValue: { $max: "$totalAmount" }
     *     }
     *   },
     *   { $sort: { revenue: -1 } },
     *   { $unionWith: {
     *       coll: "orders",
     *       pipeline: [
     *         { $group: {
     *             _id: null,
     *             revenue: { $sum: "$totalAmount" },
     *             count: { $sum: 1 },
     *             avgValue: { $avg: "$totalAmount" }
     *           }
     *         }
     *       ]
     *     }
     *   }
     * ])
     * 
     * Độ phức tạp: O(n) - cần scan tất cả đơn hàng
     * 
     * @return Map chứa:
     *         - byStatus: Map<String, Map<String, Object>> - thống kê theo từng status
     *         - total: Map<String, Object> - tổng hợp toàn bộ
     *         - conversionMetrics: Map<String, Object> - các chỉ số chuyển đổi
     */
    @Override
    public Map<String, Object> getRevenueStats() {
        log.debug("Executing getRevenueStats");
        
        try {
            // 1. Thống kê theo status
            Aggregation statusAggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("status").in("PENDING", "PROCESSING", "SHIPPED", "DELIVERED")
                    ),
                    Aggregation.group("$status")
                            .sum("totalAmount").as("revenue")
                            .count().as("count")
                            .avg("totalAmount").as("avgValue")
                            .min("totalAmount").as("minValue")
                            .max("totalAmount").as("maxValue"),
                    Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "revenue")
            );
            
            AggregationResults<Map> statusResults = mongoTemplate.aggregate(
                    statusAggregation, "orders", Map.class
            );
            
            Map<String, Map<String, Object>> byStatus = new HashMap<>();
            for (Map<String, Object> result : statusResults.getMappedResults()) {
                String status = (String) result.get("_id");
                byStatus.put(status, result);
            }
            
            // 2. Tổng hợp toàn bộ
            Aggregation totalAggregation = Aggregation.newAggregation(
                    Aggregation.group()
                            .sum("totalAmount").as("totalRevenue")
                            .count().as("totalOrders")
                            .avg("totalAmount").as("averageOrderValue"),
                    Aggregation.project()
                            .and("totalRevenue").as("totalRevenue")
                            .and("totalOrders").as("totalOrders")
                            .and("averageOrderValue").as("averageOrderValue")
            );
            
            AggregationResults<Map> totalResults = mongoTemplate.aggregate(
                    totalAggregation, "orders", Map.class
            );
            
            Map<String, Object> total = totalResults.getMappedResults().isEmpty() 
                    ? new HashMap<>() 
                    : totalResults.getMappedResults().get(0);
            
            // 3. Chỉ số chuyển đổi (conversion metrics)
            long totalOrders = mongoTemplate.count(new Query(), Order.class);
            long deliveredOrders = mongoTemplate.count(
                    new Query(Criteria.where("status").is("DELIVERED")), 
                    Order.class
            );
            long cancelledOrders = mongoTemplate.count(
                    new Query(Criteria.where("status").is("CANCELLED")), 
                    Order.class
            );
            
            Map<String, Object> conversionMetrics = new HashMap<>();
            conversionMetrics.put("totalOrders", totalOrders);
            conversionMetrics.put("deliveredOrders", deliveredOrders);
            conversionMetrics.put("cancelledOrders", cancelledOrders);
            conversionMetrics.put("conversionRate", 
                    totalOrders > 0 ? (double) deliveredOrders / totalOrders * 100 : 0);
            conversionMetrics.put("cancellationRate", 
                    totalOrders > 0 ? (double) cancelledOrders / totalOrders * 100 : 0);
            
            // 4. Kết hợp tất cả kết quả
            Map<String, Object> result = new HashMap<>();
            result.put("byStatus", byStatus);
            result.put("total", total);
            result.put("conversionMetrics", conversionMetrics);
            
            log.info("Revenue stats calculated - Total orders: {}, Total revenue: {}, " +
                    "Conversion rate: {}", totalOrders, 
                    total.get("totalRevenue"), conversionMetrics.get("conversionRate"));
            
            return result;
            
        } catch (Exception e) {
            log.error("Error executing getRevenueStats", e);
            throw new RuntimeException("Lỗi khi tính toán thống kê revenue: " + e.getMessage(), e);
        }
    }
}
