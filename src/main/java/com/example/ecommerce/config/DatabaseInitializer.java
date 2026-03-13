package com.example.ecommerce.config;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Database initializer for seeding initial data and creating indexes.
 * 초기 데이터를 시드하고 인덱스를 생성하는 데이터베이스 초기화 프로그램.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Initialize database on application startup.
     * Runs only once - idempotent and safe to run multiple times.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        log.info("Starting database initialization...");
        
        try {
            seedCategories();
            seedProducts();
            seedUsers();
            createIndexes();
            log.info("Database initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during database initialization", e);
        }
    }

    /**
     * Seed sample categories if they don't exist.
     */
    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist, skipping seeding");
            return;
        }

        log.info("Seeding categories...");
        List<Category> categories = new ArrayList<>();
        
        categories.add(new Category("Electronics", "electronics", "Electronic devices and accessories"));
        categories.add(new Category("Fashion", "fashion", "Clothing and fashion accessories"));
        categories.add(new Category("Home & Garden", "home-garden", "Home and garden products"));
        categories.add(new Category("Books", "books", "Books and educational materials"));
        categories.add(new Category("Sports", "sports", "Sports equipment and accessories"));

        categoryRepository.saveAll(categories);
        log.info("Seeded {} categories", categories.size());
    }

    /**
     * Seed sample products if they don't exist.
     */
    private void seedProducts() {
        if (productRepository.count() > 0) {
            log.info("Products already exist, skipping seeding");
            return;
        }

        log.info("Seeding products...");
        List<Category> categories = categoryRepository.findAll();
        List<Product> products = new ArrayList<>();

        if (!categories.isEmpty()) {
            String electronicsId = categories.stream()
                    .filter(c -> c.getSlug().equals("electronics"))
                    .findFirst()
                    .map(Category::getId)
                    .orElse(categories.get(0).getId());

            // Sample Electronics
            products.add(Product.builder()
                    .name("Wireless Headphones")
                    .slug("wireless-headphones")
                    .description("High-quality wireless headphones with noise cancellation")
                    .price(new BigDecimal("79.99"))
                    .categoryId(electronicsId)
                    .stock(100)
                    .status("ACTIVE")
                    .featured(true)
                    .build());

            products.add(Product.builder()
                    .name("USB-C Cable")
                    .slug("usb-c-cable")
                    .description("Durable USB-C charging and data cable")
                    .price(new BigDecimal("12.99"))
                    .categoryId(electronicsId)
                    .stock(500)
                    .status("ACTIVE")
                    .featured(false)
                    .build());
        }

        productRepository.saveAll(products);
        log.info("Seeded {} products", products.size());
    }

    /**
     * Seed sample users if they don't exist.
     */
    private void seedUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping seeding");
            return;
        }

        log.info("Seeding users...");
        List<User> users = new ArrayList<>();

        Address address1 = new Address("123 Main St", "New York", "NY", "10001", "USA");
        Address address2 = new Address("456 Park Ave", "Los Angeles", "CA", "90001", "USA");

        users.add(User.builder()
                .email("john.doe@example.com")
                .username("johndoe")
                .firstName("John")
                .lastName("Doe")
                .phone("+1-555-0001")
                .address(address1)
                .status("ACTIVE")
                .build());

        users.add(User.builder()
                .email("jane.smith@example.com")
                .username("janesmith")
                .firstName("Jane")
                .lastName("Smith")
                .phone("+1-555-0002")
                .address(address2)
                .status("ACTIVE")
                .build());

        userRepository.saveAll(users);
        log.info("Seeded {} users", users.size());
    }

    /**
     * Create necessary MongoDB indexes.
     */
    private void createIndexes() {
        log.info("Creating MongoDB indexes...");
        
        try {
            // User indexes
            IndexOperations userOps = mongoTemplate.indexOps(User.class);
            userOps.ensureIndex(new Index().on("email", 1).unique());
            userOps.ensureIndex(new Index().on("username", 1).unique());
            log.info("Created User indexes");

            // Product indexes
            IndexOperations productOps = mongoTemplate.indexOps(Product.class);
            productOps.ensureIndex(new Index().on("slug", 1).unique());
            productOps.ensureIndex(new Index().on("categoryId", 1));
            productOps.ensureIndex(new Index().on("status", 1));
            log.info("Created Product indexes");

            // Category indexes
            IndexOperations categoryOps = mongoTemplate.indexOps(Category.class);
            categoryOps.ensureIndex(new Index().on("slug", 1).unique());
            log.info("Created Category indexes");

            log.info("All indexes created successfully");
        } catch (Exception e) {
            log.error("Error creating indexes", e);
        }
    }
}
