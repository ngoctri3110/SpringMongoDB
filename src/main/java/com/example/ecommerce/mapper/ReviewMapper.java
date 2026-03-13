package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateReviewRequest;
import com.example.ecommerce.dto.request.UpdateReviewRequest;
import com.example.ecommerce.dto.response.ReviewResponse;
import com.example.ecommerce.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface cho Review entity
 * Chuyển đổi giữa Review entity, ReviewResponse DTO, CreateReviewRequest DTO và UpdateReviewRequest DTO
 * 
 * Sử dụng MapStruct với Spring Component Model để tự động inject
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

    /**
     * Chuyển đổi Review entity sang ReviewResponse DTO
     * Ánh xạ tất cả các trường từ Review sang ReviewResponse
     * 
     * @param review Review entity cần chuyển đổi
     * @return ReviewResponse DTO đã được ánh xạ
     */
    ReviewResponse toResponse(Review review);

    /**
     * Chuyển đổi CreateReviewRequest DTO sang Review entity
     * Tạo mới Review từ request DTO
     * 
     * @param request CreateReviewRequest DTO chứa dữ liệu review mới
     * @return Review entity đã được tạo từ request
     */
    Review toEntity(CreateReviewRequest request);

    /**
     * Cập nhật Review entity từ UpdateReviewRequest DTO (partial update)
     * Chỉ cập nhật các trường được cung cấp trong request
     * 
     * @param request UpdateReviewRequest DTO chứa dữ liệu cần cập nhật
     * @param review Review entity cần cập nhật (target)
     */
    void updateEntity(UpdateReviewRequest request, @MappingTarget Review review);
}
