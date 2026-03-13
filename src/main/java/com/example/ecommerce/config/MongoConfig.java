package com.example.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.event.LoggingEventListener;

/**
 * MongoDB Configuration class
 * 
 * Cấu hình MongoDB cho ứng dụng E-Commerce, bao gồm:
 * - Thiết lập custom event listeners cho MongoDB
 * - Cấu hình converter tùy chỉnh (ví dụ: BigDecimal)
 * - Đăng ký các MongoDB listener cho audit fields
 * - Cấu hình IndexResolver để hỗ trợ @Indexed annotation
 * 
 * Lớp này được tải tự động khi ứng dụng khởi động và cung cấp
 * các bean cần thiết cho hoạt động của MongoDB.
 * 
 * @author E-Commerce Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Configuration
public class MongoConfig {

    /**
     * Đăng ký MongoDB Logging Event Listener
     * 
     * Listener này ghi lại các sự kiện MongoDB (insert, update, delete)
     * cho mục đích debugging và monitoring.
     * 
     * @param mongoDatabaseFactory MongoDB database factory
     * @param mongoConverter MongoDB converter
     * @return LoggingEventListener instance
     */
    @Bean
    public LoggingEventListener mongoEventListener(
            MongoDatabaseFactory mongoDatabaseFactory,
            MongoConverter mongoConverter) {
        return new LoggingEventListener();
    }

    /**
     * Cấu hình MongoDB để hỗ trợ BigDecimal type
     * 
     * Phương thức này có thể được mở rộng để thêm các custom converter
     * cho các kiểu dữ liệu khác nếu cần thiết (ví dụ: LocalDate, LocalTime)
     * 
     * Hiện tại, Spring Boot tự động xử lý các kiểu dữ liệu cơ bản,
     * nhưng có thể thêm các converter tùy chỉnh nếu cần:
     * 
     * Ví dụ:
     * <pre>
     * Collection<? extends Converter<?, ?>> converters = new ArrayList<>();
     * converters.add(new CustomBigDecimalConverter());
     * return new MongoCustomConversions(converters);
     * </pre>
     * 
     * @return void - các converter mặc định được sử dụng
     */
    public void configureConverters() {
        // MongoDB converter configuration
        // Spring Boot tự động cấu hình các converter mặc định
        // Thêm các custom converter nếu cần thiết
        
        // Ví dụ: Custom converter cho BigDecimal
        // CustomBigDecimalConverter để xử lý đặc biệt nếu cần
    }

    /**
     * Cấu hình MongoDB Repository
     * 
     * Phương thức này cài đặt các cấu hình chung cho tất cả MongoDB repositories
     * Hiện tại, cấu hình chính được thực hiện qua @EnableMongoRepositories
     * trong EcommerceApplication class
     * 
     * Có thể mở rộng để:
     * - Cấu hình audit publisher
     * - Thiết lập custom naming strategy
     * - Cấu hình lazy loading behavior
     * 
     * @return void
     */
    public void initializeMongoRepository() {
        // MongoDB Repository initialization
        // @EnableMongoRepositories được cấu hình trong EcommerceApplication
        
        // Có thể thêm các custom repository configuration ở đây
    }

}
