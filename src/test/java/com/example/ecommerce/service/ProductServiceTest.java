package com.example.ecommerce.service;

import com.example.ecommerce.IntegrationTest;
import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for ProductService
 * Tests business logic for product operations including validation and exception handling
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@DisplayName("ProductService Tests")
class ProductServiceTest extends IntegrationTest {

    @Autowired
    private ProductService productService;

    /**
     * Test: Create product with valid data
     * Chức năng: Tạo sản phẩm hợp lệ
     */
    @Test
    @DisplayName("Tạo sản phẩm hợp lệ thành công")
    void testCreateProductSuccess() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        CreateProductRequest request = new CreateProductRequest();
        request.setName("New Laptop");
        request.setSlug("new-laptop");
        request.setPrice(BigDecimal.valueOf(999.99));
        request.setCategoryId(category.getId());
        request.setDescription("High-performance laptop");
        request.setStock(50);
        request.setStatus("ACTIVE");

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        assertThat(response)
                .isNotNull()
                .extracting(ProductResponse::getName, ProductResponse::getSlug, ProductResponse::getStatus)
                .containsExactly("New Laptop", "new-laptop", "ACTIVE");
        assertThat(response.getId()).isNotNull();
    }

    /**
     * Test: Create product with invalid category
     * Chức năng: Tạo sản phẩm với danh mục không tồn tại
     */
    @Test
    @DisplayName("Tạo sản phẩm với danh mục không tồn tại ném exception")
    void testCreateProductWithInvalidCategory() {
        // Arrange
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Product");
        request.setSlug("product");
        request.setPrice(BigDecimal.valueOf(100.00));
        request.setCategoryId("invalid-category-id");
        request.setStatus("ACTIVE");

        // Act & Assert
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
    }

    /**
     * Test: Get product by ID
     * Chức năng: Lấy sản phẩm theo ID
     */
    @Test
    @DisplayName("Lấy sản phẩm theo ID thành công")
    void testGetProductById() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), category.getId());

        // Act
        ProductResponse response = productService.getProductById(product.getId());

        // Assert
        assertThat(response)
                .isNotNull()
                .extracting(ProductResponse::getName, ProductResponse::getSlug)
                .containsExactly("Laptop", "laptop");
    }

    /**
     * Test: Get product by non-existent ID
     * Chức năng: Lấy sản phẩm với ID không tồn tại
     */
    @Test
    @DisplayName("Lấy sản phẩm không tồn tại ném exception")
    void testGetProductByIdNotFound() {
        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById("non-existent-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
    }

    /**
     * Test: Update product successfully
     * Chức năng: Cập nhật sản phẩm
     */
    @Test
    @DisplayName("Cập nhật sản phẩm thành công")
    void testUpdateProduct() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Old Name", "old-slug", BigDecimal.valueOf(100.00), category.getId());
        
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Updated Name");
        request.setSlug("updated-slug");
        request.setPrice(BigDecimal.valueOf(150.00));
        request.setCategoryId(category.getId());
        request.setStock(75);
        request.setStatus("ACTIVE");

        // Act
        ProductResponse response = productService.updateProduct(product.getId(), request);

        // Assert
        assertThat(response)
                .extracting(ProductResponse::getName, ProductResponse::getPrice, ProductResponse::getStock)
                .containsExactly("Updated Name", BigDecimal.valueOf(150.00), 75);
    }

    /**
     * Test: Update product with non-existent ID
     * Chức năng: Cập nhật sản phẩm không tồn tại
     */
    @Test
    @DisplayName("Cập nhật sản phẩm không tồn tại ném exception")
    void testUpdateProductNotFound() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        CreateProductRequest request = new CreateProductRequest();
        request.setCategoryId(category.getId());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct("non-existent-id", request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * Test: Delete product
     * Chức năng: Xóa sản phẩm
     */
    @Test
    @DisplayName("Xóa sản phẩm thành công")
    void testDeleteProduct() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("To Delete", "to-delete", BigDecimal.valueOf(100.00), category.getId());
        String productId = product.getId();

        // Act
        productService.deleteProduct(productId);

        // Assert
        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * Test: Delete non-existent product
     * Chức năng: Xóa sản phẩm không tồn tại
     */
    @Test
    @DisplayName("Xóa sản phẩm không tồn tại ném exception")
    void testDeleteProductNotFound() {
        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProduct("non-existent-id"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * Test: Get products with pagination
     * Chức năng: Lấy danh sách sản phẩm phân trang
     */
    @Test
    @DisplayName("Lấy danh sách sản phẩm phân trang thành công")
    void testGetProductsWithPagination() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        for (int i = 1; i <= 15; i++) {
            createTestProduct("Product " + i, "slug-" + i, BigDecimal.valueOf(i * 10), category.getId());
        }

        // Act
        Page<ProductResponse> page1 = productService.getProducts(PageRequest.of(0, 5));
        Page<ProductResponse> page2 = productService.getProducts(PageRequest.of(1, 5));

        // Assert
        assertThat(page1.getTotalElements()).isEqualTo(15);
        assertThat(page1.getTotalPages()).isEqualTo(3);
        assertThat(page1.getContent()).hasSize(5);
        assertThat(page2.getContent()).hasSize(5);
    }

    /**
     * Test: Get products by category
     * Chức năng: Lấy sản phẩm theo danh mục
     */
    @Test
    @DisplayName("Lấy sản phẩm theo danh mục thành công")
    void testGetProductsByCategory() {
        // Arrange
        Category electronics = createTestCategory("Electronics", "electronics");
        Category books = createTestCategory("Books", "books");
        
        createTestProduct("Laptop", "laptop", BigDecimal.valueOf(999.99), electronics.getId());
        createTestProduct("Mouse", "mouse", BigDecimal.valueOf(49.99), electronics.getId());
        createTestProduct("Book", "book", BigDecimal.valueOf(29.99), books.getId());

        // Act
        Page<ProductResponse> electronicsProducts = productService
                .getProductsByCategory(electronics.getId(), PageRequest.of(0, 10));

        // Assert
        assertThat(electronicsProducts.getTotalElements()).isEqualTo(2);
        assertThat(electronicsProducts.getContent())
                .extracting(ProductResponse::getName)
                .containsExactlyInAnyOrder("Laptop", "Mouse");
    }

    /**
     * Test: Get products for non-existent category
     * Chức năng: Lấy sản phẩm của danh mục không tồn tại
     */
    @Test
    @DisplayName("Lấy sản phẩm của danh mục không tồn tại ném exception")
    void testGetProductsByNonExistentCategory() {
        // Act & Assert
        assertThatThrownBy(() -> productService
                .getProductsByCategory("non-existent-category", PageRequest.of(0, 10)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * Test: Price update maintains decimal precision
     * Chức năng: Cập nhật giá duy trì độ chính xác thập phân
     */
    @Test
    @DisplayName("Cập nhật giá duy trì độ chính xác thập phân")
    void testPrecisionOnPriceUpdate() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Product", "product", BigDecimal.valueOf(99.99), category.getId());
        
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Product");
        request.setSlug("product");
        request.setPrice(new BigDecimal("199.99"));
        request.setCategoryId(category.getId());
        request.setStatus("ACTIVE");

        // Act
        ProductResponse response = productService.updateProduct(product.getId(), request);

        // Assert
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("199.99"));
    }

    /**
     * Test: Changing product category
     * Chức năng: Thay đổi danh mục sản phẩm
     */
    @Test
    @DisplayName("Thay đổi danh mục sản phẩm thành công")
    void testChangingProductCategory() {
        // Arrange
        Category oldCategory = createTestCategory("Electronics", "electronics");
        Category newCategory = createTestCategory("Gadgets", "gadgets");
        Product product = createTestProduct("Item", "item", BigDecimal.valueOf(100.00), oldCategory.getId());
        
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Item");
        request.setSlug("item");
        request.setPrice(BigDecimal.valueOf(100.00));
        request.setCategoryId(newCategory.getId());
        request.setStatus("ACTIVE");

        // Act
        ProductResponse response = productService.updateProduct(product.getId(), request);

        // Assert
        assertThat(response.getCategoryId()).isEqualTo(newCategory.getId());
    }

    /**
     * Test: Changing category to non-existent category
     * Chức năng: Thay đổi sang danh mục không tồn tại
     */
    @Test
    @DisplayName("Thay đổi sang danh mục không tồn tại ném exception")
    void testChangingToNonExistentCategory() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        Product product = createTestProduct("Item", "item", BigDecimal.valueOf(100.00), category.getId());
        
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Item");
        request.setSlug("item");
        request.setPrice(BigDecimal.valueOf(100.00));
        request.setCategoryId("non-existent-category");
        request.setStatus("ACTIVE");

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(product.getId(), request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * Test: Product status field
     * Chức năng: Trường trạng thái sản phẩm
     */
    @Test
    @DisplayName("Sản phẩm có trạng thái chính xác")
    void testProductStatusField() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Product");
        request.setSlug("product");
        request.setPrice(BigDecimal.valueOf(100.00));
        request.setCategoryId(category.getId());
        request.setStatus("INACTIVE");

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo("INACTIVE");
    }

    /**
     * Test: Empty products list for category
     * Chức năng: Danh sách sản phẩm trống cho danh mục
     */
    @Test
    @DisplayName("Danh mục không có sản phẩm trả về trang trống")
    void testEmptyProductsForCategory() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");

        // Act
        Page<ProductResponse> response = productService
                .getProductsByCategory(category.getId(), PageRequest.of(0, 10));

        // Assert
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getContent()).isEmpty();
    }

    /**
     * Test: Product response contains audit fields
     * Chức năng: Response chứa audit fields
     */
    @Test
    @DisplayName("Product response chứa audit fields")
    void testProductResponseAuditFields() {
        // Arrange
        Category category = createTestCategory("Electronics", "electronics");
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Product");
        request.setSlug("product");
        request.setPrice(BigDecimal.valueOf(100.00));
        request.setCategoryId(category.getId());
        request.setStatus("ACTIVE");

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getId()).isNotNull();
    }
}
