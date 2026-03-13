package com.example.ecommerce;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base integration test class with Testcontainers setup
 * Provides shared MongoDB container and test utilities
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@SpringBootTest
@Testcontainers
@Slf4j
public abstract class IntegrationTest {

    /**
     * Static MongoDB container shared across all tests
     * Will be reused to improve test execution performance
     */
    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    /**
     * Configure Spring to use Testcontainers MongoDB
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected InventoryRepository inventoryRepository;

    @Autowired
    protected CartRepository cartRepository;

    /**
     * Clear all collections before each test
     */
    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(User.class);
        mongoTemplate.dropCollection(Order.class);
        mongoTemplate.dropCollection(Category.class);
        mongoTemplate.dropCollection(Inventory.class);
        mongoTemplate.dropCollection(Cart.class);
        log.info("Test database cleared");
    }

    // ============== Product Test Fixtures ==============

    /**
     * Create a sample category for testing
     */
    protected Category createTestCategory(String name, String slug) {
        Category category = new Category(name, slug);
        category.setActive(true);
        category.setLevel(0);
        return categoryRepository.save(category);
    }

    /**
     * Create a sample product for testing
     */
    protected Product createTestProduct(String name, String slug, BigDecimal price, String categoryId) {
        Product product = new Product(name, slug, price, categoryId);
        product.setDescription("Test product description");
        product.setStock(100);
        product.setStatus("ACTIVE");
        return productRepository.save(product);
    }

    /**
     * Product builder for flexible test data creation
     */
    protected ProductBuilder productBuilder() {
        return new ProductBuilder();
    }

    /**
     * Create test product with attributes
     */
    protected Product createProductWithAttributes(String name, String categoryId) {
        Product product = new Product(name, "slug-" + name.toLowerCase(), 
                BigDecimal.valueOf(99.99), categoryId);
        product.setDescription("Test product");
        product.setStock(50);
        product.setStatus("ACTIVE");
        product.getAttributes().put("color", "red");
        product.getAttributes().put("size", "L");
        return productRepository.save(product);
    }

    // ============== User Test Fixtures ==============

    /**
     * Create a sample user for testing
     */
    protected User createTestUser(String email, String username, String fullName) {
        User user = new User(email, username, fullName);
        user.setPassword("password123");
        user.setPhoneNumber("0123456789");
        user.setActive(true);
        return userRepository.save(user);
    }

    /**
     * User builder for flexible test data creation
     */
    protected UserBuilder userBuilder() {
        return new UserBuilder();
    }

    /**
     * Create test user with address
     */
    protected User createUserWithAddress(String email, String username, String fullName) {
        User user = createTestUser(email, username, fullName);
        Address address = new Address();
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setPostalCode("12345");
        address.setCountry("Test Country");
        user.getAddresses().add(address);
        return userRepository.save(user);
    }

    // ============== Order Test Fixtures ==============

    /**
     * Create a sample order for testing
     */
    protected Order createTestOrder(String userId, String orderNumber, List<OrderItem> items, 
                                   BigDecimal totalAmount) {
        Order order = new Order(userId, orderNumber, items, totalAmount, "PENDING");
        order.setShippingAddress(createTestAddress());
        return orderRepository.save(order);
    }

    /**
     * Order builder for flexible test data creation
     */
    protected OrderBuilder orderBuilder() {
        return new OrderBuilder();
    }

    /**
     * Create test order item
     */
    protected OrderItem createTestOrderItem(String productId, String productName, 
                                           BigDecimal price, int quantity) {
        return new OrderItem(productId, productName, price, quantity);
    }

    // ============== Address Test Fixtures ==============

    /**
     * Create a test address
     */
    protected Address createTestAddress() {
        Address address = new Address();
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setPostalCode("10000");
        address.setCountry("Test Country");
        address.setPhoneNumber("0123456789");
        return address;
    }

    // ============== Inventory Test Fixtures ==============

    /**
     * Create test inventory for a product
     */
    protected Inventory createTestInventory(String productId, int available) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailable(available);
        inventory.setReserved(0);
        return inventoryRepository.save(inventory);
    }

    // ============== Builders ==============

    /**
     * Product builder for creating test products with custom properties
     */
    public class ProductBuilder {
        private String name = "Test Product";
        private String slug = "test-product";
        private BigDecimal price = BigDecimal.valueOf(99.99);
        private String categoryId;
        private int stock = 100;
        private String status = "ACTIVE";
        private boolean featured = false;

        public ProductBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder withSlug(String slug) {
            this.slug = slug;
            return this;
        }

        public ProductBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ProductBuilder withCategoryId(String categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public ProductBuilder withStock(int stock) {
            this.stock = stock;
            return this;
        }

        public ProductBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public ProductBuilder withFeatured(boolean featured) {
            this.featured = featured;
            return this;
        }

        public Product build() {
            Product product = new Product(name, slug, price, categoryId);
            product.setStock(stock);
            product.setStatus(status);
            product.setFeatured(featured);
            product.setDescription("Test product");
            return productRepository.save(product);
        }
    }

    /**
     * User builder for creating test users with custom properties
     */
    public class UserBuilder {
        private String email = "test@example.com";
        private String username = "testuser";
        private String fullName = "Test User";
        private String password = "password123";
        private boolean active = true;

        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder withFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public User build() {
            User user = new User(email, username, fullName);
            user.setPassword(password);
            user.setActive(active);
            user.setPhoneNumber("0123456789");
            return userRepository.save(user);
        }
    }

    /**
     * Order builder for creating test orders with custom properties
     */
    public class OrderBuilder {
        private String userId;
        private String orderNumber = "ORD-2025-001";
        private List<OrderItem> items = new ArrayList<>();
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private String status = "PENDING";
        private Address shippingAddress;

        public OrderBuilder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public OrderBuilder withOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public OrderBuilder withItems(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public OrderBuilder withTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public OrderBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public OrderBuilder withShippingAddress(Address shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Order build() {
            Order order = new Order(userId, orderNumber, items, totalAmount, status);
            if (shippingAddress != null) {
                order.setShippingAddress(shippingAddress);
            } else {
                order.setShippingAddress(createTestAddress());
            }
            return orderRepository.save(order);
        }
    }

    // ============== Assertion Helpers ==============

    /**
     * Assert that a product has the expected values
     */
    protected void assertProductEquals(Product expected, Product actual) {
        assert actual != null : "Product should not be null";
        assert actual.getId().equals(expected.getId()) : "Product ID mismatch";
        assert actual.getName().equals(expected.getName()) : "Product name mismatch";
        assert actual.getSlug().equals(expected.getSlug()) : "Product slug mismatch";
        assert actual.getPrice().equals(expected.getPrice()) : "Product price mismatch";
    }

    /**
     * Assert that a user has the expected values
     */
    protected void assertUserEquals(User expected, User actual) {
        assert actual != null : "User should not be null";
        assert actual.getId().equals(expected.getId()) : "User ID mismatch";
        assert actual.getEmail().equals(expected.getEmail()) : "User email mismatch";
        assert actual.getUsername().equals(expected.getUsername()) : "User username mismatch";
    }

    /**
     * Assert that an order has the expected values
     */
    protected void assertOrderEquals(Order expected, Order actual) {
        assert actual != null : "Order should not be null";
        assert actual.getId().equals(expected.getId()) : "Order ID mismatch";
        assert actual.getOrderNumber().equals(expected.getOrderNumber()) : "Order number mismatch";
        assert actual.getUserId().equals(expected.getUserId()) : "Order user ID mismatch";
    }

    /**
     * Verify that a resource was deleted
     */
    protected void assertResourceDeleted(String id, Class<?> entityClass) {
        assert !mongoTemplate.exists(org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)), 
                entityClass) : "Resource should be deleted";
    }
}
