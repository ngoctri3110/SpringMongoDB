package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateUserRequest;
import com.example.ecommerce.dto.response.PagedResponse;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * User Controller - REST endpoints cho User
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    /**
     * POST /api/v1/users - Tạo user mới
     * 
     * @param request CreateUserRequest
     * @return ResponseEntity<UserResponse> với status 201 Created
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /api/v1/users - Creating user");
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/users/{id} - Lấy user theo ID
     * 
     * @param id User ID
     * @return ResponseEntity<UserResponse> với status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("GET /api/v1/users/{} - Fetching user", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/users/{id} - Cập nhật user
     * 
     * @param id User ID
     * @param request CreateUserRequest
     * @return ResponseEntity<UserResponse> với status 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, 
            @Valid @RequestBody CreateUserRequest request) {
        log.info("PUT /api/v1/users/{} - Updating user", id);
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/users/{id} - Xóa user
     * 
     * @param id User ID
     * @return ResponseEntity với status 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/users - Lấy danh sách users (phân trang)
     * 
     * @param page Số trang (mặc định: 0)
     * @param size Số phần tử mỗi trang (mặc định: 10)
     * @return ResponseEntity<PagedResponse<UserResponse>> với status 200 OK
     */
    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("GET /api/v1/users - Fetching users with page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> pageResponse = userService.getUsers(pageable);
        
        PagedResponse<UserResponse> response = PagedResponse.<UserResponse>builder()
                .content(pageResponse.getContent())
                .page(pageResponse.getNumber())
                .size(pageResponse.getSize())
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages())
                .last(pageResponse.isLast())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
