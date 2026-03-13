# 🎯 Spring Boot + MongoDB - E-Commerce Platform

A complete **e-commerce application** built with Spring Boot, MongoDB, and Docker. Production-ready with comprehensive features for learning modern Java development.

---

## ✨ Features

### Core E-Commerce Features
- **User Management** - Registration, authentication, user profiles
- **Product Catalog** - Browse, search, filter products by categories
- **Shopping Cart** - Add/remove items, manage quantities
- **Orders** - Create, track, and manage orders
- **Payments** - Process payments with multiple payment methods
- **Reviews & Ratings** - User reviews and product ratings
- **Inventory Management** - Stock tracking and management
- **Analytics** - Revenue, top products, category statistics

### Technical Features
- **REST API** - Full RESTful API with Swagger documentation
- **Database Migrations** - Mongock for version control
- **Event-Driven** - Domain events for order and payment processing
- **Validation** - Custom validators and comprehensive input validation
- **Exception Handling** - Global exception handler with proper HTTP responses
- **Pagination** - Efficient pagination for large datasets
- **Docker Support** - Docker Compose for easy local setup
- **Integration Tests** - Testcontainers for MongoDB testing
- **Audit Logging** - Automatic audit of create/update operations

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven 3.8+

### Setup & Run

```bash
# 1. Start MongoDB
docker-compose up -d

# 2. Verify MongoDB
docker-compose ps

# 3. Build project
mvn clean install

# 4. Run application
mvn spring-boot:run

# 5. Access
# API Docs: http://localhost:8080/swagger-ui.html
# Mongo Express: http://localhost:8081
# API: http://localhost:8080
```

### Verify Setup

```bash
# Check if MongoDB is running
curl -X GET http://localhost:8081

# Check API health
curl -X GET http://localhost:8080/v3/api-docs
```

---

## 🏗️ System Architecture

### Document Relationships (MongoDB)

**Note:** MongoDB uses document-based relationships, not ERD. Documents can embed related data or reference other collections.

**Collection Structure:**

```
Collections:
├── users              (User documents with embedded addresses)
├── products           (Product documents with categoryId reference)
├── categories         (Category documents)
├── orders             (Order documents with embedded items)
├── carts              (Cart documents with embedded items)
├── reviews            (Review documents)
├── inventory          (Inventory documents)
├── payment_transactions (Payment records)

Key Relationships:
- User ← Orders (userId reference)
- User ← Reviews (userId reference)
- User ← Cart (userId reference)
- Product ← Orders (productId reference via order items)
- Product ← Reviews (productId reference)
- Category ← Products (categoryId reference)
- Order ← Payments (orderId reference)
```

### API Endpoints Overview

```
Authentication
POST   /api/v1/auth/register
POST   /api/v1/auth/login

Users
GET    /api/v1/users/{id}
POST   /api/v1/users
PUT    /api/v1/users/{id}
DELETE /api/v1/users/{id}
GET    /api/v1/users (paginated)

Products
GET    /api/v1/products
GET    /api/v1/products/{id}
POST   /api/v1/products
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}
GET    /api/v1/products/search
GET    /api/v1/products/category/{categoryId}

Categories
GET    /api/v1/categories
POST   /api/v1/categories
PUT    /api/v1/categories/{id}
DELETE /api/v1/categories/{id}

Orders
GET    /api/v1/orders
GET    /api/v1/orders/{id}
POST   /api/v1/orders
PUT    /api/v1/orders/{id}/status

Cart
GET    /api/v1/carts/{userId}
POST   /api/v1/carts/{userId}/items
PUT    /api/v1/carts/{userId}/items/{itemId}
DELETE /api/v1/carts/{userId}/items/{itemId}

Reviews
GET    /api/v1/reviews/product/{productId}
POST   /api/v1/reviews
PUT    /api/v1/reviews/{id}
DELETE /api/v1/reviews/{id}

Analytics
GET    /api/v1/analytics/revenue
GET    /api/v1/analytics/top-products
GET    /api/v1/analytics/top-categories
```

---

## 📁 Project Structure

```
springboot-mongodb/
├── docs/                           # Tài liệu học tập từng phase
│   ├── 01-fundamentals/            # Phase 1: Foundation
│   │   ├── 01-mongodb-basics.md
│   │   ├── 02-spring-data-mongodb-setup.md
│   │   ├── 03-document-modeling.md
│   │   └── 04-basic-crud.md
│   ├── 02-querying/                # Phase 2: Query Mastery
│   ├── 03-relationships/           # Phase 3: Relationships
│   ├── 04-advanced-queries/        # Phase 4: Aggregation
│   ├── 05-indexing/                # Phase 5: Indexing & Performance
│   ├── 06-performance/
│   ├── 07-transactions/            # Phase 6: Transactions
│   ├── 08-security-production/     # Phase 7: Production
│   └── 09-advanced-patterns/       # Phase 8: Advanced
│
├── src/main/java/com/example/ecommerce/
│   ├── EcommerceApplication.java
│   ├── config/                     # Configuration classes
│   ├── model/                      # Document models
│   │   ├── base/
│   │   │   └── BaseDocument.java   # Audit fields
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Order.java
│   │   ├── Review.java
│   │   ├── Cart.java
│   │   └── Inventory.java
│   ├── repository/                 # Data access layer
│   │   ├── UserRepository.java
│   │   ├── ProductRepository.java
│   │   ├── OrderRepository.java
│   │   ├── ReviewRepository.java
│   │   ├── CartRepository.java
│   │   ├── InventoryRepository.java
│   │   └── custom/                 # Custom repository implementations
│   ├── service/                    # Business logic
│   │   ├── UserService.java
│   │   ├── ProductService.java
│   │   ├── CategoryService.java
│   │   └── ...
│   ├── controller/                 # REST API
│   │   ├── UserController.java
│   │   ├── ProductController.java
│   │   └── ...
│   ├── dto/                        # Request/Response DTOs
│   │   ├── request/
│   │   └── response/
│   ├── mapper/                     # DTO mapping
│   ├── exception/                  # Exception handling
│   ├── event/                      # Domain events
│   ├── validation/                 # Custom validators
│   └── util/                       # Utilities
│
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── mongock/                    # DB migrations
│
├── src/test/java/
│   ├── repository/
│   ├── service/
│   └── controller/
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Language |
| Spring Boot | 3.2+ | Framework |
| Spring Data MongoDB | 4.2+ | MongoDB ORM |
| MongoDB | 7.0+ | Database |
| Mongock | 5.x | Database migration |
| MapStruct | 1.5+ | DTO mapping |
| Lombok | Latest | Boilerplate reduction |
| Testcontainers | 1.19+ | Integration testing |
| SpringDoc OpenAPI | 2.0+ | API documentation |

---

## 📖 Learning Resources

For comprehensive learning materials, see [README_LEARNING.md](README_LEARNING.md) which includes:
- Phase progression guide (8 phases total)
- Detailed learning path
- Documentation structure
- Practice exercises

This is a practical **e-commerce application** you can run and explore!

---

## 💡 Usage Examples

### 1. User Management

```bash
# Create User
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email":"john@example.com",
    "username":"john",
    "password":"pass123",
    "fullName":"John Doe"
  }'

# Get User
curl http://localhost:8080/api/v1/users/{id}

# List Users (paginated)
curl "http://localhost:8080/api/v1/users?page=0&size=10"

# Update User
curl -X PUT http://localhost:8080/api/v1/users/{id} \
  -H "Content-Type: application/json" \
  -d '{"fullName":"John Smith"}'
```

### 2. Product Management

```bash
# Get all products
curl "http://localhost:8080/api/v1/products?page=0&size=10"

# Search products
curl "http://localhost:8080/api/v1/products/search?name=laptop&minPrice=500&maxPrice=1500"

# Get products by category
curl "http://localhost:8080/api/v1/products/category/{categoryId}?page=0&size=10"

# Get top rated products
curl "http://localhost:8080/api/v1/products/top-rated?limit=10"
```

### 3. Shopping Cart & Orders

```bash
# Add item to cart
curl -X POST http://localhost:8080/api/v1/carts/{userId}/items \
  -H "Content-Type: application/json" \
  -d '{"productId":"{productId}","quantity":2,"price":99.99}'

# View cart
curl "http://localhost:8080/api/v1/carts/{userId}"

# Create order
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId":"{userId}",
    "shippingAddress":"123 Main St",
    "paymentMethod":"CREDIT_CARD"
  }'

# Get user orders
curl "http://localhost:8080/api/v1/orders?userId={userId}"
```

### 4. Analytics

```bash
# Get revenue analytics
curl "http://localhost:8080/api/v1/analytics/revenue?from=2024-01-01&to=2024-12-31"

# Get top products
curl "http://localhost:8080/api/v1/analytics/top-products?limit=10&period=30d"

# Get top categories
curl "http://localhost:8080/api/v1/analytics/top-categories"
```

### 5. Reviews & Ratings

```bash
# Get product reviews
curl "http://localhost:8080/api/v1/reviews/product/{productId}"

# Create review
curl -X POST http://localhost:8080/api/v1/reviews \
  -H "Content-Type: application/json" \
  -d '{
    "productId":"{productId}",
    "userId":"{userId}",
    "rating":5,
    "comment":"Great product!",
    "title":"Excellent"
  }'
```

---

## 📊 MongoDB Collections

```javascript
// users - User data
db.createCollection('users')
db.users.createIndex({ email: 1 }, { unique: true })
db.users.createIndex({ username: 1 }, { unique: true })

// products - Product catalog
db.createCollection('products')
db.products.createIndex({ slug: 1 }, { unique: true })
db.products.createIndex({ categoryId: 1 })

// categories - Product categories
db.createCollection('categories')
db.categories.createIndex({ slug: 1 }, { unique: true })

// orders - Customer orders
db.createCollection('orders')
db.orders.createIndex({ userId: 1 })
db.orders.createIndex({ orderNumber: 1 }, { unique: true })

// reviews - Product reviews
db.createCollection('reviews')
db.reviews.createIndex({ productId: 1 })
db.reviews.createIndex({ userId: 1 })

// carts - Shopping carts
db.createCollection('carts')
db.carts.createIndex({ userId: 1 }, { unique: true })

// inventory - Stock levels
db.createCollection('inventory')
db.inventory.createIndex({ productId: 1 }, { unique: true })

// paymentTransactions - Payment records
db.createCollection('paymentTransactions')
db.paymentTransactions.createIndex({ orderId: 1 })
db.paymentTransactions.createIndex({ userId: 1 })
```

---

## 🧪 Testing

### Run Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=UserRepositoryTest

# Coverage
mvn jacoco:report
```

### Integration Tests with Testcontainers

Tests use Testcontainers để spin up MongoDB instance tự động:

```java
@DataMongoTest
@Testcontainers
class UserRepositoryTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
    
    @Test
    void testFindByEmail() {
        // Test code
    }
}
```

---

## 📋 Implementation Checklist

- ✅ User Management (CRUD, authentication)
- ✅ Product Catalog (search, filtering, pagination)
- ✅ Categories (nested products)
- ✅ Shopping Cart (add/remove items)
- ✅ Orders (creation, tracking, status updates)
- ✅ Payments (transaction processing)
- ✅ Reviews & Ratings (with moderation)
- ✅ Inventory Management (stock tracking)
- ✅ Analytics Dashboard (revenue, top products)
- ✅ Audit Logging (automatic tracking)
- ✅ Event-Driven Architecture
- ✅ Database Migrations (Mongock)
- ✅ API Documentation (Swagger)
- ✅ Integration Tests
- ✅ Exception Handling
- ✅ Custom Validators

---

## 🐛 Troubleshooting

### MongoDB not connecting
```bash
# Check container status
docker-compose ps

# View logs
docker-compose logs mongodb

# Restart
docker-compose restart mongodb
```

### Build errors
```bash
# Clean install
mvn clean install -U

# Force update dependencies
mvn dependency:resolve -U
```

### Port already in use
```bash
# Change port in application.yml
server:
  port: 8081  # or another port

# Or stop the container using the port
docker-compose down
```

### API returning errors
```bash
# Check application logs
docker-compose logs app

# Verify MongoDB is initialized
docker-compose logs mongodb init-mongo.js
```

---

## 📚 Related Files

- **[README_LEARNING.md](README_LEARNING.md)** - Learning path and educational materials
- **[IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md)** - Implementation details
- **[docs/](docs/)** - Comprehensive learning materials (Vietnamese)
- **[TEST_SUITE_SUMMARY.md](TEST_SUITE_SUMMARY.md)** - Testing overview
- **[CONFIGURATION_SUMMARY.md](CONFIGURATION_SUMMARY.md)** - Configuration guide

---

## 🚀 Deployment

### Using Docker

```bash
# Build Docker image
docker build -t springboot-mongodb:latest .

# Run with Docker
docker run -p 8080:8080 --name ecommerce \
  -e SPRING_PROFILES_ACTIVE=prod \
  springboot-mongodb:latest
```

### Environment Variables

```bash
# Dev environment
export SPRING_PROFILES_ACTIVE=dev
export MONGODB_URL=mongodb://localhost:27017/ecommerce

# Prod environment
export SPRING_PROFILES_ACTIVE=prod
export MONGODB_URL=mongodb://prod-server:27017/ecommerce
export SERVER_PORT=8080
```

---

## 📞 Support

For issues or questions:
1. Check [TROUBLESHOOTING.md](docs/troubleshooting.md)
2. Review test files for usage examples
3. Check application logs: `docker-compose logs -f`
4. Consult API documentation: http://localhost:8080/swagger-ui.html

---

## 📄 License

This project is provided as-is for educational and commercial use.

---

## 📝 Recent Updates

### Version 1.1.0 (March 13, 2026)
- ✅ Fixed service layer compilation errors
- ✅ Added GitHub Actions CI/CD workflows (build, docker, codeql)
- ✅ Fixed CartController method calls to match CartService
- ✅ Fixed MigrationService user/category field mappings
- ✅ Added missing DTO classes (RefundPaymentRequest, UpdateOrderStatusRequest, etc.)
- ✅ All source code compilation errors fixed

### Known Issues
- SearchController advanced search endpoints require additional implementation
- Some SearchService methods need refinement

---

**Happy Coding! 🚀**

*Last Updated: March 13, 2026*
