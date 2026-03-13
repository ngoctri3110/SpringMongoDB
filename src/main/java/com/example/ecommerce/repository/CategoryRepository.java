package com.example.ecommerce.repository;

import com.example.ecommerce.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    /**
     * Tìm category bằng slug
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Tìm tất cả top-level categories (parentId = null)
     */
    List<Category> findByParentIdIsNullAndActiveTrue();

    /**
     * Tìm child categories
     */
    List<Category> findByParentIdAndActiveTrueOrderByDisplayOrder(String parentId);

    /**
     * Pagination support
     */
    Page<Category> findByActiveTrueOrderByDisplayOrder(Pageable pageable);

    /**
     * Kiểm tra slug tồn tại
     */
    boolean existsBySlug(String slug);
}
