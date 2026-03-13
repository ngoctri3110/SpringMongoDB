package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateCategoryRequest;
import com.example.ecommerce.dto.response.CategoryResponse;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Category Service - Xử lý logic nghiệp vụ cho Category
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Tạo category mới
     * 
     * @param request CreateCategoryRequest chứa thông tin category
     * @return CategoryResponse
     * @throws DuplicateResourceException nếu slug đã tồn tại
     */
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating category with name: {}, slug: {}", request.getName(), request.getSlug());
        
        try {
            // Kiểm tra slug đã tồn tại
            if (categoryRepository.existsBySlug(request.getSlug())) {
                log.warn("Category creation failed - Slug already exists: {}", request.getSlug());
                throw new DuplicateResourceException("Category", "slug", request.getSlug());
            }

            // Kiểm tra parent category tồn tại (nếu có)
            int level = 0;
            if (request.getParentId() != null && !request.getParentId().isEmpty()) {
                Category parentCategory = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
                level = parentCategory.getLevel() + 1;
            }

            // Tạo category mới
            Category category = new Category(request.getName(), request.getSlug());
            category.setDescription(request.getDescription());
            category.setParentId(request.getParentId());
            category.setLevel(level);
            category.setDisplayOrder(request.getDisplayOrder());

            Category savedCategory = categoryRepository.save(category);
            log.info("Category created successfully with ID: {}", savedCategory.getId());

            return mapToResponse(savedCategory);
        } catch (Exception e) {
            log.error("Error creating category: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy category theo ID
     * 
     * @param id Category ID
     * @return CategoryResponse
     * @throws ResourceNotFoundException nếu category không tồn tại
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(String id) {
        log.info("Fetching category with ID: {}", id);
        
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
            
            log.info("Category found with ID: {}", id);
            return mapToResponse(category);
        } catch (Exception e) {
            log.error("Error fetching category: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy category theo slug
     * 
     * @param slug Category slug
     * @return CategoryResponse
     * @throws ResourceNotFoundException nếu category không tồn tại
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        log.info("Fetching category with slug: {}", slug);
        
        try {
            Category category = categoryRepository.findBySlug(slug)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
            
            log.info("Category found with slug: {}", slug);
            return mapToResponse(category);
        } catch (Exception e) {
            log.error("Error fetching category by slug: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật category
     * 
     * @param id Category ID
     * @param request CreateCategoryRequest chứa thông tin cập nhật
     * @return CategoryResponse
     * @throws ResourceNotFoundException nếu category không tồn tại
     */
    public CategoryResponse updateCategory(String id, CreateCategoryRequest request) {
        log.info("Updating category with ID: {}", id);
        
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

            // Kiểm tra slug trùng (nếu slug khác với hiện tại)
            if (!category.getSlug().equals(request.getSlug()) && categoryRepository.existsBySlug(request.getSlug())) {
                log.warn("Category update failed - Slug already exists: {}", request.getSlug());
                throw new DuplicateResourceException("Category", "slug", request.getSlug());
            }

            // Cập nhật thông tin
            category.setName(request.getName());
            category.setSlug(request.getSlug());
            category.setDescription(request.getDescription());
            category.setDisplayOrder(request.getDisplayOrder());

            // Cập nhật parent category nếu có
            if (request.getParentId() != null && !request.getParentId().isEmpty()) {
                if (!request.getParentId().equals(category.getParentId())) {
                    Category parentCategory = categoryRepository.findById(request.getParentId())
                            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
                    category.setParentId(request.getParentId());
                    category.setLevel(parentCategory.getLevel() + 1);
                }
            } else {
                category.setParentId(null);
                category.setLevel(0);
            }

            Category updatedCategory = categoryRepository.save(category);
            log.info("Category updated successfully with ID: {}", id);

            return mapToResponse(updatedCategory);
        } catch (Exception e) {
            log.error("Error updating category: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Xóa category
     * 
     * @param id Category ID
     * @throws ResourceNotFoundException nếu category không tồn tại
     */
    public void deleteCategory(String id) {
        log.info("Deleting category with ID: {}", id);
        
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

            categoryRepository.delete(category);
            log.info("Category deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting category: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy danh sách categories (phân trang)
     * 
     * @param pageable Pageable chứa thông tin phân trang
     * @return Page<CategoryResponse>
     */
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getCategories(Pageable pageable) {
        log.info("Fetching categories with pagination - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Category> categories = categoryRepository.findByActiveTrueOrderByDisplayOrder(pageable);
            Page<CategoryResponse> responses = categories.map(this::mapToResponse);
            
            log.info("Found {} categories", responses.getTotalElements());
            return responses;
        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy tất cả top-level categories
     * 
     * @return List<CategoryResponse>
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getTopLevelCategories() {
        log.info("Fetching top-level categories");
        
        try {
            List<Category> categories = categoryRepository.findByParentIdIsNullAndActiveTrue();
            List<CategoryResponse> responses = categories.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            log.info("Found {} top-level categories", responses.size());
            return responses;
        } catch (Exception e) {
            log.error("Error fetching top-level categories: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy child categories của một category
     * 
     * @param parentId Parent category ID
     * @return List<CategoryResponse>
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getChildCategories(String parentId) {
        log.info("Fetching child categories for parent ID: {}", parentId);
        
        try {
            // Kiểm tra parent category tồn tại
            categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", parentId));

            List<Category> categories = categoryRepository.findByParentIdAndActiveTrueOrderByDisplayOrder(parentId);
            List<CategoryResponse> responses = categories.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            log.info("Found {} child categories for parent ID: {}", responses.size(), parentId);
            return responses;
        } catch (Exception e) {
            log.error("Error fetching child categories: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Map Category entity sang CategoryResponse DTO
     * 
     * @param category Category entity
     * @return CategoryResponse
     */
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .image(category.getImage())
                .parentId(category.getParentId())
                .level(category.getLevel())
                .displayOrder(category.getDisplayOrder())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
