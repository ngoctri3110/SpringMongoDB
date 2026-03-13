package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>, ProductRepositoryCustom {

    /**
     * Tìm product bằng slug
     */
    Optional<Product> findBySlug(String slug);

    /**
     * Tìm tất cả products của category
     */
    Page<Product> findByCategoryIdAndStatusOrderByCreatedAtDesc(
            String categoryId, String status, Pageable pageable);

    /**
     * Tìm products theo category
     */
    List<Product> findByCategory​IdAndStatusOrderByCreatedAtDesc(String categoryId, String status);

    /**
     * Pagination toàn bộ products
     */
    Page<Product> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    /**
     * Featured products
     */
    List<Product> findByFeaturedTrueAndStatusOrderByCreatedAtDesc(String status);

    /**
     * Tìm products có rating cao
     */
    Page<Product> findByStatusOrderByAvgRatingDesc(String status, Pageable pageable);

    /**
     * Kiểm tra slug tồn tại
     */
    boolean existsBySlug(String slug);

    /**
     * Tìm products có stock thấp
     */
    List<Product> findByStockLessThanAndStatus(int stockThreshold, String status);
}
