package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Product Service - Xử lý logic nghiệp vụ cho Product
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Tạo product mới
     * 
     * @param request CreateProductRequest chứa thông tin product
     * @return ProductResponse
     * @throws ResourceNotFoundException nếu category không tồn tại
     */
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product with name: {}, slug: {}", request.getName(), request.getSlug());
        
        try {
            // Kiểm tra category tồn tại
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

            // Tạo product mới
            Product product = new Product(request.getName(), request.getSlug(), 
                    request.getPrice(), request.getCategoryId());
            product.setDescription(request.getDescription());
            product.setStock(request.getStock());
            product.setStatus(request.getStatus());

            Product savedProduct = productRepository.save(product);
            log.info("Product created successfully with ID: {}", savedProduct.getId());

            return mapToResponse(savedProduct);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy product theo ID
     * 
     * @param id Product ID
     * @return ProductResponse
     * @throws ResourceNotFoundException nếu product không tồn tại
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String id) {
        log.info("Fetching product with ID: {}", id);
        
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
            
            log.info("Product found with ID: {}", id);
            return mapToResponse(product);
        } catch (Exception e) {
            log.error("Error fetching product: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật product
     * 
     * @param id Product ID
     * @param request CreateProductRequest chứa thông tin cập nhật
     * @return ProductResponse
     * @throws ResourceNotFoundException nếu product hoặc category không tồn tại
     */
    public ProductResponse updateProduct(String id, CreateProductRequest request) {
        log.info("Updating product with ID: {}", id);
        
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

            // Kiểm tra category tồn tại (nếu category khác)
            if (!product.getCategoryId().equals(request.getCategoryId())) {
                categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            }

            // Cập nhật thông tin
            product.setName(request.getName());
            product.setSlug(request.getSlug());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setCategoryId(request.getCategoryId());
            product.setStock(request.getStock());
            product.setStatus(request.getStatus());

            Product updatedProduct = productRepository.save(product);
            log.info("Product updated successfully with ID: {}", id);

            return mapToResponse(updatedProduct);
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Xóa product
     * 
     * @param id Product ID
     * @throws ResourceNotFoundException nếu product không tồn tại
     */
    public void deleteProduct(String id) {
        log.info("Deleting product with ID: {}", id);
        
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

            productRepository.delete(product);
            log.info("Product deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy danh sách products (phân trang)
     * 
     * @param pageable Pageable chứa thông tin phân trang
     * @return Page<ProductResponse>
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(Pageable pageable) {
        log.info("Fetching products with pagination - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Product> products = productRepository.findByStatusOrderByCreatedAtDesc("ACTIVE", pageable);
            Page<ProductResponse> responses = products.map(this::mapToResponse);
            
            log.info("Found {} products", responses.getTotalElements());
            return responses;
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy products theo category (phân trang)
     * 
     * @param categoryId Category ID
     * @param pageable Pageable chứa thông tin phân trang
     * @return Page<ProductResponse>
     * @throws ResourceNotFoundException nếu category không tồn tại
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(String categoryId, Pageable pageable) {
        log.info("Fetching products for category ID: {} with pagination - page: {}, size: {}", 
                categoryId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            // Kiểm tra category tồn tại
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

            Page<Product> products = productRepository
                    .findByCategoryIdAndStatusOrderByCreatedAtDesc(categoryId, "ACTIVE", pageable);
            Page<ProductResponse> responses = products.map(this::mapToResponse);
            
            log.info("Found {} products for category ID: {}", responses.getTotalElements(), categoryId);
            return responses;
        } catch (Exception e) {
            log.error("Error fetching products by category: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Map Product entity sang ProductResponse DTO
     * 
     * @param product Product entity
     * @return ProductResponse
     */
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .tags(product.getTags())
                .images(product.getImages())
                .stock(product.getStock())
                .avgRating(product.getAvgRating())
                .reviewCount(product.getReviewCount())
                .status(product.getStatus())
                .featured(product.isFeatured())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
