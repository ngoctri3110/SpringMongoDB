package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for database migrations and data operations.
 * Handles schema migrations, index creation, and test data seeding.
 * Designed for manual migration operations before integration with Mongock (Phase 7).
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MigrationService {

    private final MongoTemplate mongoTemplate;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final CartRepository cartRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    /**
     * Executes all database migrations.
     * Should be called during application startup or as part of deployment.
     * Steps:
     * 1. Create all necessary indexes
     * 2. Run data transformations if needed
     * 3. Validate data consistency
     */
    @Transactional
    public void migrateData() {
        log.info("Starting database migration process");

        try {
            createIndexes();
            log.info("Indexes created successfully");

            validateDataConsistency();
            log.info("Data consistency validation completed");

            log.info("Database migration completed successfully");
        } catch (Exception e) {
            log.error("Database migration failed", e);
            throw new RuntimeException("Migration failed: " + e.getMessage(), e);
        }
    }

    /**
     * Creates all necessary MongoDB indexes for optimal query performance.
     * Called during application startup and migration process.
     *
     * MongoDB indexes created:
     * - Products: slug (unique), categoryId, status, avgRating
     * - Orders: userId, orderNumber (unique), status, createdAt
     * - Reviews: productId, userId, productId+userId (compound), status
     * - Carts: userId (unique)
     * - Inventory: productId (unique)
     * - Users: email (unique)
     * - PaymentTransactions: orderId, status, createdAt
     */
    @Transactional
    public void createIndexes() {
        log.info("Creating database indexes");

        try {
            // Products collection indexes
            mongoTemplate.indexOps(Product.class)
                    .ensureIndex(new Index().on("slug", org.springframework.data.domain.Sort.Direction.ASC)
                            .unique());
            mongoTemplate.indexOps(Product.class)
                    .ensureIndex(new Index().on("categoryId", org.springframework.data.domain.Sort.Direction.ASC));
            mongoTemplate.indexOps(Product.class)
                    .ensureIndex(new Index().on("status", org.springframework.data.domain.Sort.Direction.ASC));
            mongoTemplate.indexOps(Product.class)
                    .ensureIndex(new Index().on("avgRating", org.springframework.data.domain.Sort.Direction.DESC));
            mongoTemplate.indexOps(Product.class)
                    .ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.DESC));
            log.debug("Product indexes created");

            // Orders collection indexes
            mongoTemplate.indexOps(Order.class)
                    .ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC));
            mongoTemplate.indexOps(Order.class)
                    .ensureIndex(new Index().on("orderNumber", org.springframework.data.domain.Sort.Direction.ASC)
                            .unique());
            mongoTemplate.indexOps(Order.class)
                    .ensureIndex(new Index().on("status", org.springframework.data.domain.Sort.Direction.ASC));
            mongoTemplate.indexOps(Order.class)
                    .ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.DESC));
            log.debug("Order indexes created");

            // Reviews collection indexes
            mongoTemplate.indexOps(Review.class)
                    .ensureIndex(new Index().on("productId", org.springframework.data.domain.Sort.Direction.ASC));
            mongoTemplate.indexOps(Review.class)
                    .ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC));
            mongoTemplate.indexOps(Review.class)
                    .ensureIndex(new Index().on("status", org.springframework.data.domain.Sort.Direction.ASC));
            mongoTemplate.indexOps(Review.class)
                    .ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.DESC));
            log.debug("Review indexes created");

            // Carts collection indexes
            mongoTemplate.indexOps(Cart.class)
                    .ensureIndex(new Index().on("userId", org.springframework.data.domain.Sort.Direction.ASC)
                            .unique());
            mongoTemplate.indexOps(Cart.class)
                    .ensureIndex(new Index().on("expiresAt", org.springframework.data.domain.Sort.Direction.ASC));
            log.debug("Cart indexes created");

            // Inventory collection indexes
            mongoTemplate.indexOps(Inventory.class)
                    .ensureIndex(new Index().on("productId", org.springframework.data.domain.Sort.Direction.ASC)
                            .unique());
            mongoTemplate.indexOps(Inventory.class)
                    .ensureIndex(new Index().on("quantity", org.springframework.data.domain.Sort.Direction.ASC));
            mongoTemplate.indexOps(Inventory.class)
                    .ensureIndex(new Index().on("lastUpdated", org.springframework.data.domain.Sort.Direction.DESC));
            log.debug("Inventory indexes created");

            // Users collection indexes
            mongoTemplate.indexOps(User.class)
                    .ensureIndex(new Index().on("email", org.springframework.data.domain.Sort.Direction.ASC)
                            .unique());
            mongoTemplate.indexOps(User.class)
                    .ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.DESC));
            log.debug("User indexes created");

            log.info("All indexes created successfully");
        } catch (Exception e) {
            log.error("Error creating indexes", e);
            throw new RuntimeException("Failed to create indexes: " + e.getMessage(), e);
        }
    }

    /**
     * Seeds test/development data into the database.
     * Creates sample products, categories, users, and orders.
     * Should only be called in development/testing environments.
     */
    @Transactional
    public void seedTestData() {
        log.info("Seeding test data");

        try {
            // Clear existing data
            mongoTemplate.remove(new Query(), Category.class);
            mongoTemplate.remove(new Query(), Product.class);
            mongoTemplate.remove(new Query(), User.class);
            mongoTemplate.remove(new Query(), Order.class);
            mongoTemplate.remove(new Query(), Review.class);
            mongoTemplate.remove(new Query(), Inventory.class);
            log.debug("Existing data cleared");

            // Create test categories
            List<Category> categories = createTestCategories();
            log.debug("Created {} test categories", categories.size());

            // Create test products
            List<Product> products = createTestProducts(categories);
            log.debug("Created {} test products", products.size());

            // Create test users
            List<User> users = createTestUsers();
            log.debug("Created {} test users", users.size());

            // Create test inventory
            createTestInventory(products);
            log.debug("Created test inventory");

            // Create test orders
            createTestOrders(users, products);
            log.debug("Created test orders");

            // Create test reviews
            createTestReviews(users, products);
            log.debug("Created test reviews");

            log.info("Test data seeding completed successfully");
        } catch (Exception e) {
            log.error("Error seeding test data", e);
            throw new RuntimeException("Failed to seed test data: " + e.getMessage(), e);
        }
    }

    /**
     * Validates data consistency across collections.
     * Checks for orphaned references and data integrity issues.
     */
    @Transactional(readOnly = true)
    public void validateDataConsistency() {
        log.info("Validating data consistency");

        // Check for orders referencing non-existent products
        var allOrders = orderRepository.findAll();
        int orphanedOrderItems = 0;

        for (Order order : allOrders) {
            for (OrderItem item : order.getItems()) {
                if (!productRepository.existsById(item.getProductId())) {
                    log.warn("Order {} has orphaned product reference: {}",
                            order.getId(), item.getProductId());
                    orphanedOrderItems++;
                }
            }
        }

        // Check for reviews referencing non-existent products
        var allReviews = reviewRepository.findAll();
        int orphanedReviews = 0;

        for (Review review : allReviews) {
            if (!productRepository.existsById(review.getProductId())) {
                log.warn("Review {} references non-existent product: {}",
                        review.getId(), review.getProductId());
                orphanedReviews++;
            }
        }

        // Check for inventory without products
        var allInventory = inventoryRepository.findAll();
        int orphanedInventory = 0;

        for (Inventory inv : allInventory) {
            if (!productRepository.existsById(inv.getProductId())) {
                log.warn("Inventory {} references non-existent product: {}",
                        inv.getId(), inv.getProductId());
                orphanedInventory++;
            }
        }

        log.info("Data consistency check completed - " +
                "Orphaned order items: {}, Orphaned reviews: {}, Orphaned inventory: {}",
                orphanedOrderItems, orphanedReviews, orphanedInventory);

        if (orphanedOrderItems > 0 || orphanedReviews > 0 || orphanedInventory > 0) {
            log.warn("Data consistency issues found - manual cleanup may be required");
        }
    }

    /**
     * Gets migration status and statistics.
     *
     * @return Map containing migration statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMigrationStatus() {
        log.debug("Retrieving migration status");

        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", LocalDateTime.now());
        status.put("products_count", productRepository.count());
        status.put("orders_count", orderRepository.count());
        status.put("reviews_count", reviewRepository.count());
        status.put("carts_count", cartRepository.count());
        status.put("inventory_count", inventoryRepository.count());
        status.put("users_count", userRepository.count());

        return status;
    }

    /**
     * Creates test categories for demo purposes.
     */
    private List<com.example.ecommerce.model.Category> createTestCategories() {
        List<com.example.ecommerce.model.Category> categories = new ArrayList<>();

        String[] categoryNames = {"Electronics", "Clothing", "Books", "Home & Garden", "Sports"};

        for (String name : categoryNames) {
            com.example.ecommerce.model.Category category = new com.example.ecommerce.model.Category();
            category.setName(name);
            category.setSlug(name.toLowerCase().replace(" ", "-"));
            category.setDescription("Test category: " + name);
            category.setActive(true);

            categories.add(categoryRepository.save(category));
        }

        return categories;
    }

    /**
     * Creates test products for demo purposes.
     */
    private List<Product> createTestProducts(List<Category> categories) {
        List<Product> products = new ArrayList<>();

        String[] productNames = {
                "Laptop Pro", "Wireless Mouse", "USB-C Cable",
                "Cotton T-Shirt", "Denim Jeans",
                "Clean Code Book", "Microservices Design",
                "Indoor Plant Pot", "Garden Tools Set",
                "Yoga Mat", "Running Shoes"
        };

        for (int i = 0; i < productNames.length; i++) {
            Product product = new Product();
            product.setName(productNames[i]);
            product.setSlug(productNames[i].toLowerCase().replace(" ", "-"));
            product.setDescription("Test product description for " + productNames[i]);
            product.setPrice(BigDecimal.valueOf(Math.random() * 500 + 10));
            product.setOriginalPrice(product.getPrice().multiply(BigDecimal.valueOf(1.2)));
            product.setCategoryId(categories.get(i % categories.size()).getId());
            product.setStock((int) (Math.random() * 100 + 10));
            product.setStatus("ACTIVE");
            product.setFeatured(i % 3 == 0);
            product.setAvgRating(Math.random() * 5);
            product.setReviewCount((int) (Math.random() * 100));

            products.add(productRepository.save(product));
        }

        return products;
    }

    /**
     * Creates test users for demo purposes.
     */
    private List<com.example.ecommerce.model.User> createTestUsers() {
        List<com.example.ecommerce.model.User> users = new ArrayList<>();

        String[] userEmails = {
                "user1@test.com", "user2@test.com", "user3@test.com",
                "user4@test.com", "user5@test.com"
        };

        for (String email : userEmails) {
            com.example.ecommerce.model.User user = new com.example.ecommerce.model.User();
            user.setEmail(email);
            user.setUsername(email.split("@")[0]);
            user.setFullName("Test User");
            user.setPassword("hashed_password");
            user.setActive(true);

            users.add(userRepository.save(user));
        }

        return users;
    }

    /**
     * Creates test inventory records.
     */
    private void createTestInventory(List<Product> products) {
        for (Product product : products) {
            Inventory inventory = new Inventory();
            inventory.setProductId(product.getId());
            inventory.setQuantity(product.getStock());
            inventory.setReserved(0);
            inventory.setWarehouse("MAIN");
            inventory.setLastUpdated(LocalDateTime.now());

            inventoryRepository.save(inventory);
        }
    }

    /**
     * Creates test orders.
     */
    private void createTestOrders(List<User> users, List<Product> products) {
        for (User user : users) {
            Order order = new Order();
            order.setUserId(user.getId());
            order.setOrderNumber("ORD-2025-" + System.nanoTime() % 10000);
            order.setStatus("DELIVERED");

            List<OrderItem> items = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (int i = 0; i < 2; i++) {
                Product product = products.get((int) (Math.random() * products.size()));
                OrderItem item = new OrderItem();
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setProductSlug(product.getSlug());
                item.setPrice(product.getPrice());
                item.setQuantity(1);
                item.setTotalPrice(product.getPrice());

                items.add(item);
                total = total.add(product.getPrice());
            }

            order.setItems(items);
            order.setTotalAmount(total);

            Address address = new Address();
            address.setStreet("123 Test St");
            address.setCity("Test City");
            address.setState("TS");
            address.setPostalCode("12345");
            address.setCountry("USA");

            order.setShippingAddress(address);

            orderRepository.save(order);
        }
    }

    /**
     * Creates test reviews.
     */
    private void createTestReviews(List<User> users, List<Product> products) {
        for (User user : users) {
            for (int i = 0; i < 2; i++) {
                Product product = products.get((int) (Math.random() * products.size()));

                Review review = new Review();
                review.setUserId(user.getId());
                review.setProductId(product.getId());
                review.setRating((int) (Math.random() * 5 + 1));
                review.setTitle("Great product!");
                review.setComment("This is a test review for " + product.getName());
                review.setVerified(true);
                review.setStatus("APPROVED");

                reviewRepository.save(review);
            }
        }
    }
}
