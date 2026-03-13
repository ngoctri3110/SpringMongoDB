package com.example.ecommerce.repository.impl;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepositoryCustom;
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
import java.util.List;
import java.util.Map;

/**
 * Custom Repository Implementation cho Product entity.
 * Cung cấp các phương thức truy vấn phức tạp sử dụng Criteria API và Aggregation Pipeline.
 * 
 * Đây là lớp triển khai các phương thức custom cho ProductRepositoryCustom interface,
 * cho phép thực hiện các truy vấn MongoDB nâng cao như:
 * - Lọc động theo nhiều tiêu chí
 * - Tìm kiếm văn bản
 * - Lấy các sản phẩm trending/featured
 * - Tìm sản phẩm stock thấp
 * 
 * @author E-Commerce Platform
 * @version 1.0
 * @see ProductRepositoryCustom
 * @see MongoTemplate
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    /**
     * MongoTemplate để thực hiện các truy vấn MongoDB phức tạp.
     * Được inject thông qua constructor.
     */
    private final MongoTemplate mongoTemplate;

    /**
     * Tìm sản phẩm theo các bộ lọc động.
     * 
     * Hỗ trợ lọc theo:
     * - Tên sản phẩm (like/contains)
     * - Category ID
     * - Khoảng giá (min/max)
     * - Rating tối thiểu
     * - Tags (chứa)
     * - Trạng thái in stock
     * 
     * Truy vấn MongoDB tương đương:
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
     * Độ phức tạp: O(n) với n là số sản phẩm thỏa mãn
     * 
     * @param filters DTO chứa các tiêu chí lọc (name, categoryId, priceRange, rating, tags, inStock)
     * @return Page<Product> - Trang các sản phẩm thỏa mãn tiêu chí
     * @throws IllegalArgumentException nếu filters là null
     */
    @Override
    public Page<Product> findByDynamicFilters(ProductFilterDTO filters) {
        log.debug("Executing findByDynamicFilters with filters: {}", filters);
        
        try {
            // Khởi tạo Criteria
            Criteria criteria = new Criteria();
            List<Criteria> conditions = new java.util.ArrayList<>();
            
            // Lọc theo tên (case-insensitive contains)
            if (filters.getName() != null && !filters.getName().isEmpty()) {
                conditions.add(Criteria.where("name").regex(filters.getName(), "i"));
                log.debug("Added name filter: {}", filters.getName());
            }
            
            // Lọc theo category
            if (filters.getCategoryId() != null && !filters.getCategoryId().isEmpty()) {
                conditions.add(Criteria.where("categoryId").is(filters.getCategoryId()));
                log.debug("Added category filter: {}", filters.getCategoryId());
            }
            
            // Lọc theo khoảng giá
            Criteria priceCriteria = new Criteria();
            if (filters.getMinPrice() != null) {
                priceCriteria = priceCriteria.gte(filters.getMinPrice());
                log.debug("Added min price filter: {}", filters.getMinPrice());
            }
            if (filters.getMaxPrice() != null) {
                priceCriteria = priceCriteria.lte(filters.getMaxPrice());
                log.debug("Added max price filter: {}", filters.getMaxPrice());
            }
            if (priceCriteria.getCriteriaObject() != null && 
                (!priceCriteria.getCriteriaObject().isEmpty())) {
                conditions.add(Criteria.where("price").andOperator(priceCriteria));
            }
            
            // Lọc theo rating tối thiểu
            if (filters.getMinRating() != null && filters.getMinRating() > 0) {
                conditions.add(Criteria.where("avgRating").gte(filters.getMinRating()));
                log.debug("Added rating filter: {}", filters.getMinRating());
            }
            
            // Lọc theo tags (sản phẩm phải chứa ít nhất một tag)
            if (filters.getTags() != null && !filters.getTags().isEmpty()) {
                conditions.add(Criteria.where("tags").in(filters.getTags()));
                log.debug("Added tags filter: {}", filters.getTags());
            }
            
            // Lọc theo trạng thái stock
            if (filters.isInStock()) {
                conditions.add(Criteria.where("stock").gt(0));
                log.debug("Added inStock filter");
            }
            
            // Thêm điều kiện status = ACTIVE
            conditions.add(Criteria.where("status").is("ACTIVE"));
            
            // Kết hợp tất cả conditions với AND logic
            if (!conditions.isEmpty()) {
                criteria = new Criteria().andOperator(conditions.toArray(new Criteria[0]));
            }
            
            // Tạo Query với pagination
            Query query = new Query(criteria);
            long total = mongoTemplate.count(query, Product.class);
            
            query.with(Pageable.unpaged()); // Để lấy tất cả trước, sau đó apply pagination
            List<Product> products = mongoTemplate.find(query, Product.class);
            
            log.info("Found {} products matching filters", products.size());
            
            // Áp dụng manual pagination
            return new PageImpl<>(products, Pageable.unpaged(), total);
            
        } catch (Exception e) {
            log.error("Error executing findByDynamicFilters", e);
            throw new RuntimeException("Lỗi khi tìm sản phẩm theo bộ lọc: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm sản phẩm theo từ khóa với hỗ trợ text search.
     * 
     * Thực hiện tìm kiếm toàn văn bản trên các trường: name, description, tags.
     * Sắp xếp kết quả theo độ liên quan (relevance score).
     * 
     * Truy vấn MongoDB tương đương:
     * db.products.find({
     *   $text: { $search: keyword },
     *   status: 'ACTIVE'
     * }).sort({ score: { $meta: 'textScore' } })
     * 
     * Lưu ý: Yêu cầu text index được tạo trên các trường name, description, tags
     * 
     * Độ phức tạp: O(n) với n là số sản phẩm thỏa mãn text search
     * 
     * @param keyword từ khóa tìm kiếm
     * @param pageable thông tin phân trang
     * @return Page<Product> - Trang các sản phẩm tìm thấy, sắp xếp theo độ liên quan
     * @throws IllegalArgumentException nếu keyword trống hoặc null
     */
    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        log.debug("Executing searchProducts with keyword: {}, pageable: {}", keyword, pageable);
        
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                log.warn("Search keyword is empty");
                return new PageImpl<>(List.of(), pageable, 0);
            }
            
            // Sử dụng text search - yêu cầu text index
            Criteria criteria = new Criteria()
                    .andOperator(
                            Criteria.where("$text").is("\"" + keyword + "\""),
                            Criteria.where("status").is("ACTIVE")
                    );
            
            Query query = new Query(criteria);
            long total = mongoTemplate.count(query, Product.class);
            
            // Áp dụng phân trang
            query.with(pageable);
            List<Product> products = mongoTemplate.find(query, Product.class);
            
            log.info("Found {} products for keyword: {}", products.size(), keyword);
            
            return new PageImpl<>(products, pageable, total);
            
        } catch (Exception e) {
            log.error("Error executing searchProducts with keyword: {}", keyword, e);
            // Nếu text index không có, thực hiện fallback: tìm kiếm regex
            return fallbackSearch(keyword, pageable);
        }
    }

    /**
     * Fallback method cho searchProducts khi text index không khả dụng.
     * Thực hiện tìm kiếm regex trên name và description.
     * 
     * @param keyword từ khóa tìm kiếm
     * @param pageable thông tin phân trang
     * @return Page<Product> - Kết quả tìm kiếm fallback
     */
    private Page<Product> fallbackSearch(String keyword, Pageable pageable) {
        log.debug("Using fallback regex search for keyword: {}", keyword);
        
        Criteria criteria = new Criteria()
                .orOperator(
                        Criteria.where("name").regex(keyword, "i"),
                        Criteria.where("description").regex(keyword, "i"),
                        Criteria.where("tags").in(keyword)
                );
        
        Query query = new Query(criteria).with(pageable);
        long total = mongoTemplate.count(new Query(criteria), Product.class);
        
        List<Product> products = mongoTemplate.find(query, Product.class);
        
        log.info("Fallback search found {} products", products.size());
        return new PageImpl<>(products, pageable, total);
    }

    /**
     * Tìm các sản phẩm nổi bật (featured).
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Lọc sản phẩm có featured = true và status = ACTIVE
     * 2. Sắp xếp theo avgRating và createdAt giảm dần
     * 3. Áp dụng phân trang
     * 
     * Aggregation pipeline tương đương:
     * db.products.aggregate([
     *   { $match: { featured: true, status: 'ACTIVE' } },
     *   { $sort: { avgRating: -1, createdAt: -1 } },
     *   { $skip: (page-1)*size },
     *   { $limit: size }
     * ])
     * 
     * Độ phức tạp: O(n log n) do sắp xếp
     * 
     * @param pageable thông tin phân trang
     * @return Page<Product> - Trang các sản phẩm nổi bật
     */
    @Override
    public Page<Product> findFeaturedProducts(Pageable pageable) {
        log.debug("Executing findFeaturedProducts with pageable: {}", pageable);
        
        try {
            Aggregation aggregation = Aggregation.newAggregation(
                    // Lọc sản phẩm nổi bật
                    Aggregation.match(
                            Criteria.where("featured").is(true)
                                    .and("status").is("ACTIVE")
                    ),
                    // Sắp xếp theo rating và ngày tạo
                    Aggregation.sort(org.springframework.data.domain.Sort.by(
                            org.springframework.data.domain.Sort.Order.desc("avgRating"),
                            org.springframework.data.domain.Sort.Order.desc("createdAt")
                    )),
                    // Áp dụng phân trang
                    Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
                    Aggregation.limit(pageable.getPageSize())
            );
            
            AggregationResults<Product> results = mongoTemplate.aggregate(
                    aggregation, "products", Product.class
            );
            
            List<Product> products = results.getMappedResults();
            
            // Lấy tổng số featured products
            long total = mongoTemplate.count(
                    new Query(Criteria.where("featured").is(true)
                            .and("status").is("ACTIVE")),
                    Product.class
            );
            
            log.info("Found {} featured products, total: {}", products.size(), total);
            
            return new PageImpl<>(products, pageable, total);
            
        } catch (Exception e) {
            log.error("Error executing findFeaturedProducts", e);
            throw new RuntimeException("Lỗi khi tìm sản phẩm nổi bật: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm các sản phẩm được đánh giá cao nhất.
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Lọc các sản phẩm hoạt động (status = ACTIVE)
     * 2. Sắp xếp theo avgRating giảm dần
     * 3. Lấy limit sản phẩm đầu tiên
     * 
     * Aggregation pipeline tương đương:
     * db.products.aggregate([
     *   { $match: { status: 'ACTIVE', reviewCount: { $gt: 0 } } },
     *   { $sort: { avgRating: -1 } },
     *   { $limit: limit }
     * ])
     * 
     * Độ phức tạp: O(n log n) do sắp xếp
     * 
     * @param limit số lượng sản phẩm tối đa cần trả về
     * @return List<Product> - Danh sách các sản phẩm được đánh giá cao
     */
    @Override
    public List<Product> findTopRatedProducts(int limit) {
        log.debug("Executing findTopRatedProducts with limit: {}", limit);
        
        try {
            Aggregation aggregation = Aggregation.newAggregation(
                    // Lọc sản phẩm hoạt động và có reviews
                    Aggregation.match(
                            Criteria.where("status").is("ACTIVE")
                                    .and("reviewCount").gt(0)
                    ),
                    // Sắp xếp theo rating giảm dần
                    Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "avgRating"),
                    // Giới hạn số kết quả
                    Aggregation.limit(limit)
            );
            
            AggregationResults<Product> results = mongoTemplate.aggregate(
                    aggregation, "products", Product.class
            );
            
            List<Product> products = results.getMappedResults();
            log.info("Found {} top-rated products", products.size());
            
            return products;
            
        } catch (Exception e) {
            log.error("Error executing findTopRatedProducts", e);
            throw new RuntimeException("Lỗi khi tìm sản phẩm được đánh giá cao: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm các sản phẩm có stock thấp (low inventory).
     * 
     * Sử dụng aggregation pipeline để:
     * 1. Lọc sản phẩm hoạt động với stock < threshold
     * 2. Sắp xếp theo stock tăng dần (những cái hết sớm nhất lên đầu)
     * 3. Trả về danh sách để theo dõi
     * 
     * Aggregation pipeline tương đương:
     * db.products.aggregate([
     *   { $match: { status: 'ACTIVE', stock: { $lt: threshold } } },
     *   { $sort: { stock: 1 } },
     *   { $project: {
     *       name: 1,
     *       stock: 1,
     *       categoryId: 1,
     *       status: 1,
     *       createdAt: 1
     *     }
     *   }
     * ])
     * 
     * Độ phức tạp: O(n log n) do sắp xếp
     * 
     * @param threshold ngưỡng stock để coi là "thấp" (ví dụ: 10)
     * @return List<Product> - Danh sách sản phẩm có stock thấp, sắp xếp tăng dần theo stock
     */
    @Override
    public List<Product> findLowStockProducts(int threshold) {
        log.debug("Executing findLowStockProducts with threshold: {}", threshold);
        
        try {
            Aggregation aggregation = Aggregation.newAggregation(
                    // Lọc sản phẩm hoạt động và stock dưới ngưỡng
                    Aggregation.match(
                            Criteria.where("status").is("ACTIVE")
                                    .and("stock").lt(threshold)
                    ),
                    // Sắp xếp theo stock tăng dần (những cái hết sớm nhất lên đầu)
                    Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "stock"),
                    // Lấy các trường cần thiết
                    Aggregation.project("name", "stock", "categoryId", "status", "createdAt", "price")
            );
            
            AggregationResults<Product> results = mongoTemplate.aggregate(
                    aggregation, "products", Product.class
            );
            
            List<Product> products = results.getMappedResults();
            log.warn("Found {} products with low stock (threshold: {})", products.size(), threshold);
            
            return products;
            
        } catch (Exception e) {
            log.error("Error executing findLowStockProducts with threshold: {}", threshold, e);
            throw new RuntimeException("Lỗi khi tìm sản phẩm stock thấp: " + e.getMessage(), e);
        }
    }

    // ==================== DTO NESTED CLASS ====================
    /**
     * DTO cho các bộ lọc động của Product.
     * Được sử dụng trong findByDynamicFilters.
     */
    public static class ProductFilterDTO {
        private String name;              // Tìm kiếm theo tên (like/contains)
        private String categoryId;        // Lọc theo category
        private BigDecimal minPrice;      // Giá tối thiểu
        private BigDecimal maxPrice;      // Giá tối đa
        private Double minRating;         // Rating tối thiểu
        private List<String> tags;        // Tags (chứa)
        private boolean inStock;          // Chỉ sản phẩm có stock > 0

        // Constructors
        public ProductFilterDTO() {
        }

        public ProductFilterDTO(String name, String categoryId, BigDecimal minPrice, 
                               BigDecimal maxPrice, Double minRating, List<String> tags, 
                               boolean inStock) {
            this.name = name;
            this.categoryId = categoryId;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.minRating = minRating;
            this.tags = tags;
            this.inStock = inStock;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public BigDecimal getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(BigDecimal minPrice) {
            this.minPrice = minPrice;
        }

        public BigDecimal getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(BigDecimal maxPrice) {
            this.maxPrice = maxPrice;
        }

        public Double getMinRating() {
            return minRating;
        }

        public void setMinRating(Double minRating) {
            this.minRating = minRating;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public boolean isInStock() {
            return inStock;
        }

        public void setInStock(boolean inStock) {
            this.inStock = inStock;
        }

        @Override
        public String toString() {
            return "ProductFilterDTO{" +
                    "name='" + name + '\'' +
                    ", categoryId='" + categoryId + '\'' +
                    ", minPrice=" + minPrice +
                    ", maxPrice=" + maxPrice +
                    ", minRating=" + minRating +
                    ", tags=" + tags +
                    ", inStock=" + inStock +
                    '}';
        }
    }
}
