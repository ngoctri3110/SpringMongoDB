package com.example.ecommerce.repository;

import com.example.ecommerce.IntegrationTest;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for ProductRepository
 * Tests CRUD operations and derived queries for Product documents
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@DisplayName("ProductRepository Tests")
class ProductRepositoryTest extends IntegrationTest {

    /**
     * Test: Create a new product and retrieve it
     * Chức năng: Tạo sản phẩm mới và lấy lại
     */
    @Test
    @DisplayName("Tạo sản phẩm mới và lấy lại thành công")
    void testCreateProduct() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        Product product = new Product("Laptop", "laptop-test", BigDecimal.valueOf(999.99), category.getId());
        product.setDescription("High-performance laptop");
        product.setStock(50);

        // Act
        Product savedProduct = productRepository.save(product);
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertThat(foundProduct)
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p)
                        .extracting(Product::getName, Product::getSlug, Product::getStock)
                        .containsExactly("Laptop", "laptop-test", 50)
                );
    }

    /**
     * Test: Update an existing product
     * Chức năng: Cập nhật sản phẩm
     */
    @Test
    @DisplayName("Cập nhật sản phẩm thành công")
    void testUpdateProduct() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Old Name", "old-slug", BigDecimal.valueOf(100.00), category.getId());

        // Act
        product.setName("Updated Name");
        product.setPrice(BigDecimal.valueOf(150.00));
        product.setStock(75);
        Product updatedProduct = productRepository.save(product);

        // Assert
        Product retrieved = productRepository.findById(updatedProduct.getId()).orElseThrow();
        assertThat(retrieved)
                .extracting(Product::getName, Product::getPrice, Product::getStock)
                .containsExactly("Updated Name", BigDecimal.valueOf(150.00), 75);
    }

    /**
     * Test: Delete a product and verify it's not found
     * Chức năng: Xóa sản phẩm và xác nhận không tìm thấy
     */
    @Test
    @DisplayName("Xóa sản phẩm thành công")
    void testDeleteProduct() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Product to Delete", "to-delete", 
                BigDecimal.valueOf(100.00), category.getId());
        String productId = product.getId();

        // Act
        productRepository.delete(product);
        Optional<Product> foundProduct = productRepository.findById(productId);

        // Assert
        assertThat(foundProduct).isEmpty();
        assertThat(productRepository.count()).isZero();
    }

    /**
     * Test: Find product by slug (derived query method)
     * Chức năng: Tìm sản phẩm bằng slug
     */
    @Test
    @DisplayName("Tìm sản phẩm bằng slug thành công")
    void testFindBySlug() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        String uniqueSlug = "unique-product-slug";
        Product product = new Product("Unique Product", uniqueSlug, BigDecimal.valueOf(99.99), category.getId());
        productRepository.save(product);

        // Act
        Optional<Product> foundProduct = productRepository.findBySlug(uniqueSlug);

        // Assert
        assertThat(foundProduct)
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p)
                        .extracting(Product::getName, Product::getSlug)
                        .containsExactly("Unique Product", uniqueSlug)
                );
    }

    /**
     * Test: Find product by category ID with status filtering
     * Chức năng: Tìm sản phẩm theo danh mục
     */
    @Test
    @DisplayName("Tìm sản phẩm theo danh mục thành công")
    void testFindByCategoryId() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        createTestProduct("Product 1", "slug-1", BigDecimal.valueOf(100.00), category.getId());
        createTestProduct("Product 2", "slug-2", BigDecimal.valueOf(200.00), category.getId());
        
        Category otherCategory = createTestCategory("Books", "books");
        createTestProduct("Other Product", "slug-other", BigDecimal.valueOf(50.00), otherCategory.getId());

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> categoryProducts = productRepository
                .findByCategoryIdAndStatusOrderByCreatedAtDesc(category.getId(), "ACTIVE", pageable);

        // Assert
        assertThat(categoryProducts)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Product 1", "Product 2");
        assertThat(categoryProducts.getTotalElements()).isEqualTo(2);
    }

    /**
     * Test: Find products within a price range (complex query)
     * Chức năng: Tìm sản phẩm trong khoảng giá
     */
    @Test
    @DisplayName("Tìm sản phẩm trong khoảng giá thành công")
    void testFindByPriceRange() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        createTestProduct("Cheap Product", "cheap", BigDecimal.valueOf(50.00), category.getId());
        createTestProduct("Mid Product", "mid", BigDecimal.valueOf(150.00), category.getId());
        createTestProduct("Expensive Product", "expensive", BigDecimal.valueOf(500.00), category.getId());

        // Act - Find products with price between 100 and 200
        List<Product> products = productRepository.findAll();
        List<Product> inPriceRange = products.stream()
                .filter(p -> p.getPrice().compareTo(BigDecimal.valueOf(100)) >= 0)
                .filter(p -> p.getPrice().compareTo(BigDecimal.valueOf(200)) <= 0)
                .toList();

        // Assert
        assertThat(inPriceRange)
                .extracting(Product::getName)
                .containsExactly("Mid Product");
    }

    /**
     * Test: Find products with featured status
     * Chức năng: Tìm sản phẩm nổi bật
     */
    @Test
    @DisplayName("Tìm sản phẩm nổi bật thành công")
    void testFindFeaturedProducts() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        
        Product featured1 = createTestProduct("Featured 1", "featured-1", BigDecimal.valueOf(100.00), category.getId());
        featured1.setFeatured(true);
        productRepository.save(featured1);
        
        Product featured2 = createTestProduct("Featured 2", "featured-2", BigDecimal.valueOf(200.00), category.getId());
        featured2.setFeatured(true);
        productRepository.save(featured2);
        
        createTestProduct("Not Featured", "not-featured", BigDecimal.valueOf(300.00), category.getId());

        // Act
        List<Product> featuredProducts = productRepository.findByFeaturedTrueAndStatusOrderByCreatedAtDesc("ACTIVE");

        // Assert
        assertThat(featuredProducts)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Featured 1", "Featured 2")
                .hasSize(2);
    }

    /**
     * Test: Find products with low stock
     * Chức năng: Tìm sản phẩm có tồn kho thấp
     */
    @Test
    @DisplayName("Tìm sản phẩm có tồn kho thấp thành công")
    void testFindByLowStock() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        
        Product lowStock = createTestProduct("Low Stock", "low-stock", BigDecimal.valueOf(100.00), category.getId());
        lowStock.setStock(3);
        productRepository.save(lowStock);
        
        Product highStock = createTestProduct("High Stock", "high-stock", BigDecimal.valueOf(100.00), category.getId());
        highStock.setStock(100);
        productRepository.save(highStock);

        // Act
        List<Product> lowStockProducts = productRepository.findByStockLessThanAndStatus(10, "ACTIVE");

        // Assert
        assertThat(lowStockProducts)
                .extracting(Product::getName, Product::getStock)
                .containsExactly(tuple("Low Stock", 3));
    }

    /**
     * Test: Check if slug already exists
     * Chức năng: Kiểm tra slug đã tồn tại
     */
    @Test
    @DisplayName("Kiểm tra slug tồn tại thành công")
    void testSlugUniqueness() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        createTestProduct("Product", "unique-slug", BigDecimal.valueOf(100.00), category.getId());

        // Act
        boolean exists = productRepository.existsBySlug("unique-slug");
        boolean notExists = productRepository.existsBySlug("non-existent-slug");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    /**
     * Test: Pagination and sorting
     * Chức năng: Phân trang và sắp xếp
     */
    @Test
    @DisplayName("Phân trang và sắp xếp sản phẩm thành công")
    void testPaginationAndSorting() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        for (int i = 1; i <= 15; i++) {
            createTestProduct("Product " + i, "slug-" + i, BigDecimal.valueOf(i * 10), category.getId());
        }

        // Act - First page with 5 items, sorted by creation date descending
        Pageable page1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> firstPage = productRepository.findByStatusOrderByCreatedAtDesc("ACTIVE", page1);

        // Act - Second page
        Pageable page2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> secondPage = productRepository.findByStatusOrderByCreatedAtDesc("ACTIVE", page2);

        // Assert
        assertThat(firstPage)
                .extracting(Product::getName)
                .hasSize(5);
        assertThat(firstPage.getTotalElements()).isEqualTo(15);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.isFirst()).isTrue();
        
        assertThat(secondPage)
                .extracting(Product::getName)
                .hasSize(5);
        assertThat(secondPage.isLast()).isFalse();
    }

    /**
     * Test: Find products sorted by rating
     * Chức năng: Tìm sản phẩm sắp xếp theo đánh giá
     */
    @Test
    @DisplayName("Tìm sản phẩm sắp xếp theo đánh giá thành công")
    void testFindByRating() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        
        Product product1 = createTestProduct("Product 1", "slug-1", BigDecimal.valueOf(100.00), category.getId());
        product1.setAvgRating(4.5);
        product1.setReviewCount(100);
        productRepository.save(product1);
        
        Product product2 = createTestProduct("Product 2", "slug-2", BigDecimal.valueOf(100.00), category.getId());
        product2.setAvgRating(3.5);
        product2.setReviewCount(50);
        productRepository.save(product2);
        
        Product product3 = createTestProduct("Product 3", "slug-3", BigDecimal.valueOf(100.00), category.getId());
        product3.setAvgRating(4.8);
        product3.setReviewCount(200);
        productRepository.save(product3);

        // Act
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "avgRating"));
        Page<Product> ratedProducts = productRepository.findByStatusOrderByAvgRatingDesc("ACTIVE", pageable);

        // Assert
        assertThat(ratedProducts)
                .extracting(Product::getName, Product::getAvgRating)
                .containsExactly(
                        tuple("Product 3", 4.8),
                        tuple("Product 1", 4.5),
                        tuple("Product 2", 3.5)
                );
    }

    /**
     * Test: Multiple filters combined
     * Chức năng: Kết hợp nhiều bộ lọc
     */
    @Test
    @DisplayName("Kết hợp nhiều bộ lọc thành công")
    void testMultipleFilters() {
        // Arrange
        Category electronics = createTestCategory("Electronics", "electronics");
        Category books = createTestCategory("Books", "books");
        
        // Create products in electronics with high price
        createTestProduct("Expensive Laptop", "laptop", BigDecimal.valueOf(1000.00), electronics.getId());
        createTestProduct("Expensive Phone", "phone", BigDecimal.valueOf(800.00), electronics.getId());
        
        // Create products in books with low price
        createTestProduct("Cheap Book", "book", BigDecimal.valueOf(15.00), books.getId());

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expensiveElectronics = productRepository
                .findByCategoryIdAndStatusOrderByCreatedAtDesc(electronics.getId(), "ACTIVE", pageable);

        // Assert
        assertThat(expensiveElectronics.getTotalElements()).isEqualTo(2);
        assertThat(expensiveElectronics)
                .extracting(Product::getCategoryId)
                .allMatch(catId -> catId.equals(electronics.getId()));
    }
}
