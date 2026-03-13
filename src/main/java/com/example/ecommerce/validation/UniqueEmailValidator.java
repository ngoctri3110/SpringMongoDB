package com.example.ecommerce.validation;

import com.example.ecommerce.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validator implementation for UniqueEmail constraint.
 * Checks if an email address already exists in the user repository.
 * 
 * Triển khai xác thực cho ràng buộc UniqueEmail.
 * Kiểm tra xem địa chỉ email đã tồn tại trong kho lưu trữ người dùng hay chưa.
 */
@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    @Override
    public void initialize(UniqueEmail annotation) {
        // Initialization logic if needed
    }

    /**
     * Validates that the email is unique in the system.
     * 
     * @param email the email address to validate
     * @param context the constraint validator context
     * @return true if the email is unique, false otherwise
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // Null values are considered valid by default
        if (email == null) {
            return true;
        }

        // Check if email exists in the database
        return !userRepository.existsByEmail(email);
    }
}
