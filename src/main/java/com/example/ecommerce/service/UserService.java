package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateUserRequest;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service - Xử lý logic nghiệp vụ cho User
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Tạo user mới
     * 
     * @param request CreateUserRequest chứa thông tin user
     * @return UserResponse
     * @throws DuplicateResourceException nếu email hoặc username đã tồn tại
     */
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}, username: {}", request.getEmail(), request.getUsername());
        
        try {
            // Kiểm tra email đã tồn tại
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("User creation failed - Email already exists: {}", request.getEmail());
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }

            // Kiểm tra username đã tồn tại
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("User creation failed - Username already exists: {}", request.getUsername());
                throw new DuplicateResourceException("User", "username", request.getUsername());
            }

            // Tạo user mới
            User user = new User(request.getEmail(), request.getUsername(), request.getFullName());
            user.setPassword(encodePassword(request.getPassword())); // TODO: Implement in Phase 7
            user.setPhoneNumber(request.getPhoneNumber());

            User savedUser = userRepository.save(user);
            log.info("User created successfully with ID: {}", savedUser.getId());

            return mapToResponse(savedUser);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy user theo ID
     * 
     * @param id User ID
     * @return UserResponse
     * @throws ResourceNotFoundException nếu user không tồn tại
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        log.info("Fetching user with ID: {}", id);
        
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
            
            log.info("User found with ID: {}", id);
            return mapToResponse(user);
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật thông tin user
     * 
     * @param id User ID
     * @param request CreateUserRequest chứa thông tin cập nhật
     * @return UserResponse
     * @throws ResourceNotFoundException nếu user không tồn tại
     */
    public UserResponse updateUser(String id, CreateUserRequest request) {
        log.info("Updating user with ID: {}", id);
        
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            // Kiểm tra email trùng (nếu email khác với hiện tại)
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                log.warn("User update failed - Email already exists: {}", request.getEmail());
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }

            // Kiểm tra username trùng (nếu username khác với hiện tại)
            if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
                log.warn("User update failed - Username already exists: {}", request.getUsername());
                throw new DuplicateResourceException("User", "username", request.getUsername());
            }

            // Cập nhật thông tin
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setFullName(request.getFullName());
            user.setPhoneNumber(request.getPhoneNumber());

            User updatedUser = userRepository.save(user);
            log.info("User updated successfully with ID: {}", id);

            return mapToResponse(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Xóa user
     * 
     * @param id User ID
     * @throws ResourceNotFoundException nếu user không tồn tại
     */
    public void deleteUser(String id) {
        log.info("Deleting user with ID: {}", id);
        
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            userRepository.delete(user);
            log.info("User deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy danh sách user (phân trang)
     * 
     * @param pageable Pageable chứa thông tin phân trang
     * @return Page<UserResponse>
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Pageable pageable) {
        log.info("Fetching users with pagination - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<User> users = userRepository.findAll(pageable);
            Page<UserResponse> responses = users.map(this::mapToResponse);
            
            log.info("Found {} users", responses.getTotalElements());
            return responses;
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Encode password - TODO: Implement in Phase 7 with BCryptPasswordEncoder
     * 
     * @param password Raw password
     * @return Encoded password
     */
    private String encodePassword(String password) {
        // TODO: Implement password encoding with BCryptPasswordEncoder in Phase 7
        // For now, return as-is (insecure - for development only)
        return password;
    }

    /**
     * Map User entity sang UserResponse DTO
     * 
     * @param user User entity
     * @return UserResponse
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .avatar(user.getAvatar())
                .addresses(user.getAddresses())
                .roles(user.getRoles())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
