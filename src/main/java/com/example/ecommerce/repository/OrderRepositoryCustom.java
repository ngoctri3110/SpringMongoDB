package com.example.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.ecommerce.model.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Custom repository interface for Order entity providing complex query capabilities.
 * Allows for dynamic filtering, advanced searching, and custom MongoDB aggregations.
 * 
 * Giao diện này định nghĩa các phương thức truy vấn custom cho Order entity,
 * được triển khai trong OrderRepositoryImpl sử dụng Criteria API và Aggregation Pipeline.
 * 
 * @author E-Commerce Platform
 * @version 1.0
 * @see com.example.ecommerce.repository.impl.OrderRepositoryImpl
 */
public interface OrderRepositoryCustom {

    /**
     * Tìm đơn hàng theo trạng thái (status).
     * 
     * Sử dụng Criteria API để tìm tất cả đơn hàng có status cụ thể.
     * Sắp xếp theo ngày tạo giảm dần (mới nhất lên đầu).
     * 
     * MongoDB equivalent:
     * db.orders.find({ status: status }).sort({ createdAt: -1 })
     * 
     * Độ phức tạp: O(n)
     * Index gợi ý: db.orders.createIndex({ status: 1, createdAt: -1 })
     * Use case: Dashboard, order management list, status-based filtering
     * 
     * @param status trạng thái đơn hàng (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
     * @param pageable pagination and sorting information
     * @return paginated page of orders with specified status
     */
    Page<Order> findOrdersByStatus(String status, Pageable pageable);

    /**
     * Tìm đơn hàng trong khoảng ngày.
     * 
     * Sử dụng Criteria API để tìm đơn hàng được tạo trong khoảng thời gian.
     * Sắp xếp theo ngày tạo giảm dần.
     * 
     * MongoDB equivalent:
     * db.orders.find({
     *   createdAt: { $gte: startDate, $lte: endDate }
     * }).sort({ createdAt: -1 })
     * 
     * Độ phức tạp: O(n)
     * Index gợi ý: db.orders.createIndex({ createdAt: -1 })
     * Use case: Reports, date range filtering, analytics
     * 
     * @param startDate ngày bắt đầu (sẽ được chuyển thành 00:00:00)
     * @param endDate ngày kết thúc (sẽ được chuyển thành 23:59:59)
     * @param pageable pagination and sorting information
     * @return paginated page of orders in the specified date range
     */
    Page<Order> findOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Tính tổng revenue trong khoảng ngày.
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Lọc đơn hàng trong khoảng ngày
     * 2. Tính tổng revenue, số lượng, giá trị trung bình
     * 
     * MongoDB equivalent:
     * db.orders.aggregate([
     *   { $match: { createdAt: { $gte: startDate, $lte: endDate } } },
     *   { $group: {
     *       _id: null,
     *       totalRevenue: { $sum: "$totalAmount" },
     *       orderCount: { $sum: 1 },
     *       averageOrderValue: { $avg: "$totalAmount" }
     *     }
     *   }
     * ])
     * 
     * Độ phức tạp: O(n)
     * Use case: Revenue reports, financial analytics, dashboard metrics
     * 
     * @param startDate ngày bắt đầu
     * @param endDate ngày kết thúc
     * @return Map chứa: totalRevenue, orderCount, averageOrderValue
     */
    Map<String, Object> findRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Tìm đơn hàng của một user với status cụ thể.
     * 
     * Sử dụng Criteria API để lọc đơn hàng theo cả userId và status.
     * Hữu ích cho việc xem lịch sử đơn hàng của user theo từng status.
     * 
     * MongoDB equivalent:
     * db.orders.find({
     *   userId: userId,
     *   status: status
     * }).sort({ createdAt: -1 })
     * 
     * Độ phức tạp: O(n)
     * Index gợi ý: db.orders.createIndex({ userId: 1, status: 1, createdAt: -1 })
     * Use case: User order history, status-based order tracking
     * 
     * @param userId ID của user
     * @param status trạng thái đơn hàng
     * @return List<Order> - Danh sách đơn hàng của user có status đó
     */
    List<Order> findOrdersByStatusAndUser(String userId, String status);

    /**
     * Tính toán thống kê revenue chi tiết.
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Nhóm đơn hàng theo status
     * 2. Tính tổng revenue, số lượng, giá trị trung bình, min, max cho mỗi status
     * 3. Tính tổng hợp cho tất cả các status
     * 4. Tính chỉ số chuyển đổi (conversion rate)
     * 5. Sắp xếp theo revenue giảm dần
     * 
     * MongoDB equivalent:
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
     *   { $sort: { revenue: -1 } }
     * ])
     * 
     * Độ phức tạp: O(n log n) do sắp xếp
     * Use case: Business analytics, KPI dashboard, financial reports
     * 
     * @return Map chứa:
     *         - byStatus: Map<String, Map<String, Object>> - thống kê theo từng status
     *         - total: Map<String, Object> - tổng hợp toàn bộ
     *         - conversionMetrics: Map<String, Object> - các chỉ số chuyển đổi
     */
    Map<String, Object> getRevenueStats();
}
