package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ProductRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service class for product search and filtering.
 * Implements dynamic filtering using Criteria API.
 * Supports filtering by name, price range, category, tags, rating, and stock status.
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;
    private final ProductRepositoryCustom productRepositoryCustom;

    /**
     * Searches products by text query.
     * Searches in product name and description fields.
     *
     * @param searchText the search text
     * @param pageable pagination information
     * @return Page of products matching the search text
     */
    @Transactional(readOnly = true)
    public Page<Product> searchByText(String searchText, Pageable pageable) {
        log.debug("Searching products by text: {}", searchText);
        return productRepositoryCustom.searchProductsByText(searchText, null, pageable);
    }

    /**
     * Searches products by text with category filter.
     *
     * @param searchText the search text
     * @param categoryId the category ID filter (optional)
     * @param pageable pagination information
     * @return Page of products matching criteria
     */
    @Transactional(readOnly = true)
    public Page<Product> searchByTextAndCategory(String searchText, String categoryId, Pageable pageable) {
        log.debug("Searching products by text: {} in category: {}", searchText, categoryId);
        return productRepositoryCustom.searchProductsByText(searchText, categoryId, pageable);
    }

    /**
     * Searches products by price range.
     *
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return Page of products within the price range
     */
    @Transactional(readOnly = true)
    public Page<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Searching products by price range: {} - {}", minPrice, maxPrice);
        return productRepositoryCustom.findByComplexCriteria(null, minPrice, maxPrice, null, pageable);
    }

    /**
     * Searches products by category.
     *
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return Page of products in the category
     */
    @Transactional(readOnly = true)
    public Page<Product> searchByCategory(String categoryId, Pageable pageable) {
        log.debug("Searching products by category: {}", categoryId);
        return productRepository.findByCategoryIdAndStatusOrderByCreatedAtDesc(
                categoryId, "ACTIVE", pageable);
    }

    /**
     * Searches products by rating.
     *
     * @param minRating the minimum average rating
     * @param pageable pagination information
     * @return Page of products with rating >= minRating
     */
    @Transactional(readOnly = true)
    public Page<Product> searchByRating(Double minRating, Pageable pageable) {
        log.debug("Searching products by minimum rating: {}", minRating);
        return productRepositoryCustom.findByComplexCriteria(null, null, null, minRating, pageable);
    }

    /**
     * Comprehensive product search with multiple filters.
     * Supports filtering by all criteria simultaneously.
     *
     * @param searchText text search (optional)
     * @param categoryId category filter (optional)
     * @param minPrice minimum price (optional)
     * @param maxPrice maximum price (optional)
     * @param minRating minimum rating (optional)
     * @param pageable pagination information
     * @return Page of products matching all provided criteria
     */
    @Transactional(readOnly = true)
    public Page<Product> searchWithFilters(String searchText, String categoryId,
                                            BigDecimal minPrice, BigDecimal maxPrice,
                                            Double minRating, Pageable pageable) {
        log.debug("Searching products with comprehensive filters - text: {}, category: {}, " +
                "price: {}-{}, rating: {}", searchText, categoryId, minPrice, maxPrice, minRating);

        // If search text is provided, use text search with category
        if (searchText != null && !searchText.isEmpty()) {
            return productRepositoryCustom.searchProductsByText(searchText, categoryId, pageable);
        }

        // Otherwise use complex criteria search
        return productRepositoryCustom.findByComplexCriteria(
                categoryId, minPrice, maxPrice, minRating, pageable);
    }

    /**
     * Searches for products with low stock.
     * Returns products with stock below a specified threshold.
     *
     * @param stockThreshold the minimum stock level
     * @param pageable pagination information
     * @return Page of products below stock threshold
     */
    @Transactional(readOnly = true)
    public Page<Product> searchLowStockProducts(int stockThreshold, Pageable pageable) {
        log.debug("Searching for low stock products - threshold: {}", stockThreshold);

        // This uses the repository method to find low stock products
        var products = productRepository.findByStockLessThanAndStatus(stockThreshold, "ACTIVE");
        
        // Note: This could be optimized with a custom query for pagination
        // For now, returning results in memory
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), products.size());
        
        var content = products.subList(start, Math.min(end, products.size()));
        return new org.springframework.data.domain.PageImpl<>(content, pageable, products.size());
    }

    /**
     * Searches for featured products.
     *
     * @param pageable pagination information
     * @return Page of featured products
     */
    @Transactional(readOnly = true)
    public Page<Product> searchFeaturedProducts(Pageable pageable) {
        log.debug("Searching for featured products");

        var featuredProducts = productRepository.findByFeaturedTrueAndStatusOrderByCreatedAtDesc("ACTIVE");
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), featuredProducts.size());
        
        var content = featuredProducts.subList(start, Math.min(end, featuredProducts.size()));
        return new org.springframework.data.domain.PageImpl<>(content, pageable, featuredProducts.size());
    }

    /**
     * Searches for trending products based on sales and ratings.
     *
     * @param limit the maximum number of products to return
     * @return List of trending products
     */
    @Transactional(readOnly = true)
    public java.util.List<Product> searchTrendingProducts(int limit) {
        log.debug("Searching for trending products - limit: {}", limit);
        return productRepositoryCustom.findTrendingProducts(limit);
    }

    /**
     * Searches for top-rated products.
     *
     * @param pageable pagination information
     * @return Page of top-rated products
     */
    @Transactional(readOnly = true)
    public Page<Product> searchTopRatedProducts(Pageable pageable) {
        log.debug("Searching for top-rated products");
        return productRepository.findByStatusOrderByAvgRatingDesc("ACTIVE", pageable);
    }

    /**
     * Searches for products in stock.
     *
     * @param pageable pagination information
     * @return Page of products with stock > 0
     */
    @Transactional(readOnly = true)
    public Page<Product> searchInStockProducts(Pageable pageable) {
        log.debug("Searching for in-stock products");
        return productRepository.findByStatusOrderByCreatedAtDesc("ACTIVE", pageable);
    }

    /**
     * Advanced search with dynamic criteria using Criteria API.
     * This method demonstrates how to use ProductRepositoryCustom for flexible queries.
     *
     * @param criteria the search criteria object
     * @param pageable pagination information
     * @return Page of products matching the criteria
     */
    @Transactional(readOnly = true)
    public Page<Product> advancedSearch(ProductSearchCriteria criteria, Pageable pageable) {
        log.debug("Advanced product search with criteria: {}", criteria);

        return productRepositoryCustom.findByComplexCriteria(
                criteria.getCategoryId(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getMinRating(),
                pageable
        );
    }

    /**
     * Searches for products by multiple product IDs.
     * Useful for fetching specific products efficiently.
     *
     * @param productIds list of product IDs to search for
     * @return List of products with the given IDs
     */
    @Transactional(readOnly = true)
    public java.util.List<Product> searchByProductIds(java.util.List<String> productIds) {
        log.debug("Searching for products by IDs - count: {}", productIds.size());
        return productRepositoryCustom.findByProductIds(productIds);
    }

    /**
     * Checks if a product slug exists.
     *
     * @param slug the product slug
     * @return true if slug exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean slugExists(String slug) {
        return productRepository.existsBySlug(slug);
    }

    /**
     * Finds a product by slug.
     *
     * @param slug the product slug
     * @return the Product object if found
     */
    @Transactional(readOnly = true)
    public Product findBySlug(String slug) {
        log.debug("Searching product by slug: {}", slug);
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new com.example.ecommerce.exception.ResourceNotFoundException(
                        "Product", "slug", slug));
    }
}

/**
 * DTO for encapsulating product search criteria.
 * Provides a clean interface for passing multiple search parameters.
 */
class ProductSearchCriteria {
    private String searchText;
    private String categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double minRating;
    private Integer minStock;
    private String status;

    public ProductSearchCriteria() {
    }

    public ProductSearchCriteria(String searchText, String categoryId, BigDecimal minPrice,
                                 BigDecimal maxPrice, Double minRating) {
        this.searchText = searchText;
        this.categoryId = categoryId;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minRating = minRating;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
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

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProductSearchCriteria{" +
                "searchText='" + searchText + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", minRating=" + minRating +
                ", minStock=" + minStock +
                ", status='" + status + '\'' +
                '}';
    }
}
