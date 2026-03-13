package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho tạo Review mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotBlank(message = "ID người dùng không được để trống")
    private String userId;

    @NotBlank(message = "ID sản phẩm không được để trống")
    private String productId;

    @NotNull(message = "Đánh giá không được để trống")
    @Min(value = 1, message = "Đánh giá phải từ 1-5 sao")
    @Max(value = 5, message = "Đánh giá phải từ 1-5 sao")
    private Integer rating;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Bình luận không được để trống")
    private String comment;

    private Boolean verified = false;
}
