package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.impl.ProductRepositoryImpl.ProductFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Custom repository interface for Product entity providing complex query capabilities.
 * Allows for dynamic filtering, advanced search, and custom MongoDB aggregations.
 * 
 * Giao diện này định nghĩa các phương thức truy vấn custom cho Product entity,
 * được triển khai trong ProductRepositoryImpl sử dụng Criteria API và Aggregation Pipeline.
 * 
 * @author E-Commerce Platform
 * @version 1.0
 * @see com.example.ecommerce.repository.impl.ProductRepositoryImpl
 */
public interface ProductRepositoryCustom {

    /**
     * Tìm sản phẩm theo các bộ lọc động.
     * 
     * Hỗ trợ lọc theo:
     * - Tên sản phẩm (like/contains)
     * - Category ID
     * - Khoảng giá (min/max)
     * - Rating tối thiểu
     * - Tags (chứa)
     * - Trạng thái in stock (stock > 0)
     * 
     * MongoDB equivalent:
     * db.products.find({
     *   name: { $regex: name, $options: 'i' },
     *   categoryId: categoryId,
     *   price: { $gte: minPrice, $lte: maxPrice },
     *   avgRating: { $gte: minRating },
     *   tags: { $in: tags },
     *   stock: { $gt: 0 },
     *   status: 'ACTIVE'
     * })
     *
     * Độ phức tạp: O(n) - Full collection scan
     * Caching: Nên cache 5-10 phút tùy theo request frequency
     * 
     * @param filters DTO chứa các tiêu chí lọc
     * @return paginated page of products matching complex criteria
     */
    Page<Product> findByDynamicFilters(ProductFilterDTO filters);

    /**
     * Tìm kiếm sản phẩm theo từ khóa với hỗ trợ text search.
     * 
     * Thực hiện tìm kiếm toàn văn bản trên: name, description, tags
     * Kết quả được sắp xếp theo độ liên quan (relevance score).
     * 
     * Yêu cầu: Text index trên các trường name, description, tags
     * Nếu không có text index, sẽ fallback sang regex search
     * 
     * MongoDB equivalent:
     * db.products.find({
     *   $text: { $search: keyword },
     *   status: 'ACTIVE'
     * }).sort({ score: { $meta: 'textScore' } })
     *
     * Độ phức tạp: O(n) để full scan
     * Hiệu năng: Tốt với text index, chậm với regex fallback
     *
     * @param keyword the text to search
     * @param pageable pagination and sorting information
     * @return paginated page of products matching search criteria
     */
    Page<Product> searchProducts(String keyword, Pageable pageable);

    /**
     * Tìm các sản phẩm nổi bật (featured).
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Lọc sản phẩm có featured = true
     * 2. Sắp xếp theo rating và ngày tạo
     * 3. Áp dụng phân trang
     * 
     * MongoDB equivalent:
     * db.products.aggregate([
     *   { $match: { featured: true, status: 'ACTIVE' } },
     *   { $sort: { avgRating: -1, createdAt: -1 } },
     *   { $skip: skip },
     *   { $limit: limit }
     * ])
     *
     * Độ phức tạp: O(n log n) do sắp xếp
     * Use case: Homepage featured products carousel
     * 
     * @param pageable pagination and sorting information
     * @return paginated page of featured products
     */
    Page<Product> findFeaturedProducts(Pageable pageable);

    /**
     * Tìm các sản phẩm được đánh giá cao nhất.
     * 
     * Sử dụng aggregation pipeline để lấy các sản phẩm có rating cao nhất.
     * 
     * MongoDB equivalent:
     * db.products.aggregate([
     *   { $match: { status: 'ACTIVE', reviewCount: { $gt: 0 } } },
     *   { $sort: { avgRating: -1 } },
     *   { $limit: limit }
     * ])
     *
     * Độ phức tạp: O(n log n) do sắp xếp
     * Use case: Best-rated products list
     * 
     * @param limit the maximum number of products to return
     * @return list of top-rated products
     */
    List<Product> findTopRatedProducts(int limit);

    /**
     * Tìm các sản phẩm có stock thấp (low inventory).
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Lọc sản phẩm hoạt động với stock < threshold
     * 2. Sắp xếp theo stock tăng dần
     * 3. Trả về danh sách để theo dõi inventory
     * 
     * MongoDB equivalent:
     * db.products.aggregate([
     *   { $match: { status: 'ACTIVE', stock: { $lt: threshold } } },
     *   { $sort: { stock: 1 } }
     * ])
     *
     * Độ phức tạp: O(n log n) do sắp xếp
     * Use case: Inventory management, alert systems
     * 
     * @param threshold stock threshold to consider as "low" (e.g., 10)
     * @return list of products with low stock
     */
    List<Product> findLowStockProducts(int threshold);
}
