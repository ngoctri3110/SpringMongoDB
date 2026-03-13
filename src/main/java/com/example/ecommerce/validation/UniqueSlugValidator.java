package com.example.ecommerce.validation;

import com.example.ecommerce.repository.ProductRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validator implementation for UniqueSlug constraint.
 * Checks if a product slug already exists in the product repository.
 * 
 * Triển khai xác thực cho ràng buộc UniqueSlug.
 * Kiểm tra xem slug sản phẩm đã tồn tại trong kho lưu trữ sản phẩm hay chưa.
 */
@Component
@RequiredArgsConstructor
public class UniqueSlugValidator implements ConstraintValidator<UniqueSlug, String> {

    private final ProductRepository productRepository;

    @Override
    public void initialize(UniqueSlug annotation) {
        // Initialization logic if needed
    }

    /**
     * Validates that the product slug is unique in the system.
     * 
     * @param slug the product slug to validate
     * @param context the constraint validator context
     * @return true if the slug is unique, false otherwise
     */
    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        // Null values are considered valid by default
        if (slug == null) {
            return true;
        }

        // Check if slug exists in the database
        return !productRepository.existsBySlug(slug);
    }
}
