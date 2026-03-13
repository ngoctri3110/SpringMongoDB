package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateUserRequest;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper interface cho User entity
 * Chuyển đổi giữa User entity, UserResponse DTO và CreateUserRequest DTO
 * 
 * Sử dụng MapStruct với Spring Component Model để tự động inject
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Chuyển đổi User entity sang UserResponse DTO
     * 
     * @param user User entity cần chuyển đổi
     * @return UserResponse DTO đã được ánh xạ
     */
    UserResponse toResponse(User user);

    /**
     * Chuyển đổi CreateUserRequest DTO sang User entity
     * 
     * @param request CreateUserRequest DTO chứa dữ liệu người dùng mới
     * @return User entity đã được tạo từ request
     */
    User toEntity(CreateUserRequest request);

    /**
     * Cập nhật User entity từ CreateUserRequest DTO (partial update)
     * Chỉ cập nhật các trường không null
     * 
     * @param request CreateUserRequest DTO chứa dữ liệu cần cập nhật
     * @param user User entity cần cập nhật (target)
     */
    void updateEntity(CreateUserRequest request, @MappingTarget User user);

    /**
     * Chuyển đổi danh sách User entities sang danh sách UserResponse DTOs
     * 
     * @param users Danh sách User entities
     * @return Danh sách UserResponse DTOs đã được ánh xạ
     */
    List<UserResponse> toResponseList(List<User> users);
}
