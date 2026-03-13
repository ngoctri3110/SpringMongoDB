package com.example.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * MongoDB Auditing Configuration class
 * 
 * Cấu hình tính năng auditing cho MongoDB, bao gồm:
 * - Kích hoạt @EnableMongoAuditing để tự động cập nhật createdAt/updatedAt
 * - Cung cấp AuditorAware bean để theo dõi người tạo/cập nhật tài liệu
 * - Cấu hình DateTimeProvider tùy chỉnh cho timestamp
 * 
 * Auditing fields được tự động áp dụng cho tất cả documents extends BaseDocument:
 * - @CreatedDate: Tự động thiết lập khi tạo mới
 * - @LastModifiedDate: Tự động cập nhật khi sửa đổi
 * 
 * @author E-Commerce Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Configuration
@EnableMongoAuditing
public class MongoAuditConfig {

    /**
     * Cung cấp AuditorAware bean để theo dõi người dùng hiện tại
     * 
     * AuditorAware được sử dụng để tự động lưu thông tin về người tạo/cập nhật
     * tài liệu. Hiện tại trả về "SYSTEM" cho tất cả hoạt động.
     * 
     * Có thể mở rộng để:
     * 1. Lấy thông tin từ SecurityContext (Spring Security)
     * 2. Lấy user từ JWT token
     * 3. Lấy từ request context
     * 
     * Ví dụ với Spring Security:
     * <pre>
     * return Optional.ofNullable(SecurityContextHolder.getContext())
     *     .map(SecurityContext::getAuthentication)
     *     .filter(Authentication::isAuthenticated)
     *     .map(Authentication::getName)
     *     .or(() -> Optional.of("SYSTEM"));
     * </pre>
     * 
     * @return AuditorAware<String> implementation trả về tên người dùng/hệ thống
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<String>() {
            /**
             * Trả về người dùng hiện tại hoặc "SYSTEM" nếu chưa có user
             * 
             * @return Optional<String> chứa tên người dùng
             */
            @Override
            public Optional<String> getCurrentAuditor() {
                // TODO: Tích hợp với Spring Security hoặc authentication mechanism
                // Hiện tại trả về "SYSTEM" cho mục đích demo
                
                return Optional.of("SYSTEM");
                
                // Ví dụ thực tế với Spring Security:
                /*
                return Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getName)
                    .or(() -> Optional.of("SYSTEM"));
                */
            }
        };
    }

    /**
     * Cấu hình DateTimeProvider tùy chỉnh cho MongoDB auditing
     * 
     * DateTimeProvider được sử dụng để lấy timestamp hiện tại khi
     * cập nhật các audit fields. Hiện tại sử dụng LocalDateTime.now()
     * với timezone mặc định của hệ thống.
     * 
     * Có thể mở rộng để:
     * - Sử dụng UTC timezone cố định
     * - Tích hợp với NTP server
     * - Lưu offset timezone
     * 
     * @return DateTimeProvider implementation
     */
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return new DateTimeProvider() {
            /**
             * Trả về Optional<Object> chứa timestamp hiện tại
             * 
             * Spring Data MongoDB sẽ sử dụng giá trị này để set createdAt/updatedAt
             * Trả về LocalDateTime.now() sử dụng hệ thống default timezone
             * 
             * @return Optional chứa LocalDateTime hiện tại
             */
            @Override
            public Optional<Object> getNow() {
                // Sử dụng LocalDateTime.now() với hệ thống timezone
                // Có thể thay đổi thành:
                // - LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                // - Instant.now() cho UTC
                // - ZonedDateTime.now() để lưu timezone info
                
                return Optional.of(LocalDateTime.now(ZoneId.systemDefault()));
                
                // Ví dụ sử dụng UTC:
                // return Optional.of(LocalDateTime.now(ZoneId.of("UTC")));
            }
        };
    }

}
