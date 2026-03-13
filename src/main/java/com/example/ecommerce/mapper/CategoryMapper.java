package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateCategoryRequest;
import com.example.ecommerce.dto.response.CategoryResponse;
import com.example.ecommerce.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface cho Category entity
 * Chuyển đổi giữa Category entity, CategoryResponse DTO và CreateCategoryRequest DTO
 * 
 * Sử dụng MapStruct với Spring Component Model để tự động inject
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Chuyển đổi Category entity sang CategoryResponse DTO
     * Ánh xạ tất cả các trường từ Category sang CategoryResponse
     * 
     * @param category Category entity cần chuyển đổi
     * @return CategoryResponse DTO đã được ánh xạ
     */
    CategoryResponse toResponse(Category category);

    /**
     * Chuyển đổi CreateCategoryRequest DTO sang Category entity
     * Tạo mới Category từ request DTO
     * 
     * @param request CreateCategoryRequest DTO chứa dữ liệu danh mục mới
     * @return Category entity đã được tạo từ request
     */
    Category toEntity(CreateCategoryRequest request);

    /**
     * Cập nhật Category entity từ CreateCategoryRequest DTO (partial update)
     * Chỉ cập nhật các trường được cung cấp trong request
     * 
     * @param request CreateCategoryRequest DTO chứa dữ liệu cần cập nhật
     * @param category Category entity cần cập nhật (target)
     */
    void updateEntity(CreateCategoryRequest request, @MappingTarget Category category);
}
